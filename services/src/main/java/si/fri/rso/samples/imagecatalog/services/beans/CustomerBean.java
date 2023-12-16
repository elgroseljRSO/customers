package si.fri.rso.samples.imagecatalog.services.beans;

import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.utils.JPAUtils;
import org.json.JSONObject;
import si.fri.rso.samples.imagecatalog.models.entities.Customer;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.core.UriInfo;
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

//    public Integer payAppointment(int id, String jsonString) {
//        Customer customer = em.find(Customer.class, id);
//
//        JSONObject obj = new JSONObject(jsonString);
//        obj.put("customer", customer.getEmail());
////        int start = obj.getInt("start");
////        int serviceTypeId = obj.getJSONObject("service_type").getInt("id");
//        int cost = obj.getJSONObject("service_type").getInt("cost");
////        int employeeId = obj.getJSONObject("employee").getInt("id");
//
//        // check if you have enough money
//        if (cost > customer.getMoney()) {
//
//        }
//
//        // make a post request to appointment catalog && update money
//        URL url = new URL("http://localhost:8080/v1/appointments");
//        HttpURLConnection con = (HttpURLConnection) url.openConnection();
//        con.setRequestMethod("POST");
//
//
//
//
//    }
}
