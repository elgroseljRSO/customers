package si.fri.rso.samples.imagecatalog.services.beans;

import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.utils.JPAUtils;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.json.JSONObject;
import si.fri.rso.samples.imagecatalog.models.entities.Customer;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.core.UriInfo;
import java.io.BufferedInputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.ByteArrayOutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.logging.Logger;


@RequestScoped
public class CustomerBean {

    private Logger log = Logger.getLogger(CustomerBean.class.getName());

    @Inject
    private EntityManager em;



    public List<Customer> getCustomerFilter(UriInfo uriInfo) {

        QueryParameters queryParameters = QueryParameters.query(uriInfo.getRequestUri().getQuery()).defaultOffset(0)
                .build();

        return JPAUtils.queryEntities(em, Customer.class, queryParameters);
    }


    @Timeout(value = 2, unit = ChronoUnit.SECONDS)
    @CircuitBreaker(requestVolumeThreshold = 3)
    @Fallback(fallbackMethod = "checkEmailFallback")
    public Object checkEmail(String email) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://mailcheck.p.rapidapi.com/?domain=" + email))
                    .header("X-RapidAPI-Key", "709d75220emsh3a9ddc1e258796bp1d05c6jsn83f0d000c9f4")
                    .header("X-RapidAPI-Host", "mailcheck.p.rapidapi.com")
                    .method("GET", HttpRequest.BodyPublishers.noBody())
                    .build();
//            HttpRequest request = HttpRequest.newBuilder()
//                    .uri(URI.create("https://wrongnaslov.p.rapidapi.com/?domain=" + email))
//                    .header("X-RapidAPI-Key", "wrongkey")
//                    .header("X-RapidAPI-Host", "mailcheck.p.rapidapi.com")
//                    .method("GET", HttpRequest.BodyPublishers.noBody())
//                    .build();
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body());

            JSONObject obj = new JSONObject(response.body());
            Boolean valid = obj.getBoolean("valid");
            String text = obj.getString("text");
            String reason = obj.getString("reason");
            Integer risk = obj.getInt("risk");

            if (risk > 40 || !valid) {
                return response.body();
            }
            return null;

        }
        catch (Exception e) {
            log.info("tuki");
            log.severe(e.getMessage());
            throw new InternalServerErrorException(e);
        }

    }

    public Object checkEmailFallback(String email) {
        log.info("FALLBACK");
        if (email.contains("@")) {
            return null;
        }
        return "{\"valid\":true,\"text\":\"OK\",\"reason\":\"All good.\",\"risk\":0}";
    }

    public Object createCustomer(String email) {
        // check if email already exists

        final CriteriaBuilder cbC = em.getCriteriaBuilder();
        final CriteriaQuery<Customer> cqC = cbC.createQuery(Customer.class);
        Root<Customer> rootC = cqC.from(Customer.class);


        cqC.where(cbC.equal(rootC.get("email"), email));

        List<Customer> customers = em.createQuery(cqC).getResultList();
        int numCustomers = customers.size();
        if (numCustomers > 0) {
            return -1;
        }

        // check email validity
        Object o = checkEmail(email);
        if (o instanceof String) {
            return o;
        }


        Customer customer = new Customer(0,email);
        try {
            beginTx();
            em.persist(customer);
            commitTx();
        }
        catch (Exception e) {
            rollbackTx();
        }

        if (customer.getId() == null) {
            throw new RuntimeException("Entity was not persisted");
        }

        return customer;
    }


    public Object payAppointment(int id, String jsonString) {
        Customer customer = em.find(Customer.class, id);
        log.info("customer: "+customer);

        if (customer == null) {
            return 0;
        }

        int cost = -1;
        try {
            JSONObject obj = new JSONObject(jsonString);
            log.info("obj: "+obj);


            obj.put("customer", customer.getEmail());
            jsonString = obj.toString();
            cost = obj.getJSONObject("service_type").getInt("cost");
        } catch (Exception e) {
            
            log.warning("PARSING JSON FAILED.");
            return -3;
        }


        // check if you have enough money
        if (cost > customer.getMoney()) {
            log.info("NOT ENOUGH MONEY.");
            return -1;
        }

        HttpURLConnection httpCon;
        try {
            // make a post request to appointment catalog && update money


            Config config = ConfigProvider.getConfig();
            String configValue = config.getValue("url.appointments", String.class);
            log.info("URL: " + configValue);

//            URL url = new URL("http://localhost:8080/v1/appointments");
            URL url = new URL(configValue);
            httpCon = (HttpURLConnection) url.openConnection();
            httpCon.setDoOutput(true);
            httpCon.setRequestMethod("POST");

            OutputStream os = httpCon.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
            osw.write(jsonString);
            osw.flush();
            osw.close();
            os.close();

            httpCon.connect();
        } catch (Exception e) {
            
            log.warning("CONNECTION TO APPOINTMENTS FAILED.");
            return -2;
        }


        String result;
        try {
            int responseCode = httpCon.getResponseCode();
            if (responseCode == 409) {
                log.warning("APPOINTMENT ALREDY EXISTS.");
                return -4;
            }
            BufferedInputStream bis = new BufferedInputStream(httpCon.getInputStream());
            ByteArrayOutputStream buf = new ByteArrayOutputStream();

            int result2 = bis.read();
            while(result2 != -1) {
                buf.write((byte) result2);
                result2 = bis.read();
            }

            result = buf.toString();
            log.info("RESULT: " + result);
//            System.out.println(result);

            // success, update money

        } catch (Exception e) {
            
            log.warning("RETRIEVING OF NEW APPOINTMENT FAILED.");
            return -2;
        }


        try {
//            JSONObject jsonObj = new JSONObject(result);
//            int appointmentId = jsonObj.getInt("id");
            customer.setMoney(customer.getMoney() - cost);

            return result;
        }
        catch (Exception e) {
            
            log.warning("CHARGING FOR APPOINTMENT FAILED.");
            return -2;
        }


    }


    public Object refillMoney(Integer id, String jsonString) {
        //parse JSON
        Customer c;
        Integer amount;
        try{
            JSONObject obj = new JSONObject(jsonString);
            amount = obj.getInt("amount");
            c = em.find(Customer.class, id);
        }
        catch (Exception e) {
            return -2;
        }


        if (c == null) {
            return -1;
        }
        int newMoney = 0;
        try {
            beginTx();
            newMoney = c.getMoney() + amount;
            c.setMoney(newMoney);
            commitTx();
        }
        catch (Exception e) {
            rollbackTx();
        }

        return c;
    }

    private void beginTx() {
        if (!em.getTransaction().isActive()) {
            em.getTransaction().begin();
        }
    }

    private void commitTx() {
        if (em.getTransaction().isActive()) {
            em.getTransaction().commit();
        }
    }

    private void rollbackTx() {
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
    }

}
