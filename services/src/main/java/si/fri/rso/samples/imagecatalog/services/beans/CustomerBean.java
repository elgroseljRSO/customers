package si.fri.rso.samples.imagecatalog.services.beans;

import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.utils.JPAUtils;
import org.json.JSONObject;
import si.fri.rso.samples.imagecatalog.models.entities.Customer;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.core.UriInfo;
import java.io.BufferedInputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.ByteArrayOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
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


    public Customer createCustomer(String email) {
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

    public Integer payAppointment(int id, String jsonString) {
        Customer customer = em.find(Customer.class, id);

        JSONObject obj = new JSONObject(jsonString);
        obj.put("customer", customer.getEmail());
        jsonString = obj.toString();
//        int start = obj.getInt("start");
//        int serviceTypeId = obj.getJSONObject("service_type").getInt("id");
        int cost = obj.getJSONObject("service_type").getInt("cost");
//        int employeeId = obj.getJSONObject("employee").getInt("id");

        // check if you have enough money
        if (cost > customer.getMoney()) {
            return -1;
        }

        HttpURLConnection httpCon;
        try {
            // make a post request to appointment catalog && update money
            URL url = new URL("http://localhost:8080/v1/appointments");
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
            e.printStackTrace();
            log.warning("CONNECTION TO APPOINTMENTS FAILED.");
            return -2;
        }

        String result;
        try {

            BufferedInputStream bis = new BufferedInputStream(httpCon.getInputStream());
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            int result2 = bis.read();
            while(result2 != -1) {
                buf.write((byte) result2);
                result2 = bis.read();
            }
            result = buf.toString();
//            System.out.println(result);

            // success, update money

        } catch (Exception e) {
            e.printStackTrace();
            log.warning("RETRIEVING ID OF NEW APPOINTMENT FAILED.");
            return -2;
        }

        try {
            int appointmentId = Integer.valueOf(result);
            customer.setMoney(customer.getMoney() - cost);

            return appointmentId;
        }
        catch (Exception e) {
            e.printStackTrace();
            log.warning("CHARGING FOR APPOINTMENT FAILED.");
            return -2;
        }







    }
}
