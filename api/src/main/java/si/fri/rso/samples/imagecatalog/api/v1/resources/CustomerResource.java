package si.fri.rso.samples.imagecatalog.api.v1.resources;

import com.kumuluz.ee.logs.cdi.Log;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.headers.Header;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.json.JSONObject;
import si.fri.rso.samples.imagecatalog.models.entities.Customer;
import si.fri.rso.samples.imagecatalog.services.beans.CustomerBean;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.logging.Logger;

@Log
@ApplicationScoped
@Path("/customers")
public class CustomerResource {
    private Logger log = Logger.getLogger(CustomerResource.class.getName());

    @Inject
    private CustomerBean customerBean;


    @Context
    protected UriInfo uriInfo;

    @Operation(description = "Get all customers.", summary = "Get all customers")
    @APIResponses({
            @APIResponse(responseCode = "200",
                    description = "List of customers",
                    content = @Content(schema = @Schema(implementation = Customer.class, type = SchemaType.ARRAY)),
                    headers = {@Header(name = "X-Total-Count", description = "Number of objects in list")}
            )})
    @GET
    public Response getCustomers() {

        List<Customer> customers = customerBean.getCustomerFilter(uriInfo);

        return Response.status(Response.Status.OK).entity(customers).build();
    }


    @Operation(description = "Add customer.", summary = "Add customer")
    @APIResponses({
            @APIResponse(responseCode = "201",
                    description = "Customer successfully added."
            ),
            @APIResponse(responseCode = "405", description = "Validation error."),
            @APIResponse(responseCode = "400", description = "Email validation error."),
            @APIResponse(responseCode = "503", description = "E-mail Check service unavailable."),
            @APIResponse(responseCode = "409", description = "Email taken.")

    })
    @POST
    public Response createCustomer(@RequestBody(
            description = "Object with customer email",
            required = true) String jsonString) {
        try{
            JSONObject obj = new JSONObject(jsonString);
            String email = obj.getString("email");

            Object o = customerBean.createCustomer(email);
            if (o instanceof Customer) {
                int customerId = ((Customer)o).getId();
                return Response.status(Response.Status.CREATED).entity(customerId).build();
            }else if(o instanceof String){
                return Response.status(Response.Status.BAD_REQUEST).entity(o).build();

            }else if (o instanceof Integer){
                if ((Integer)o == -1){
                    return Response.status(Response.Status.CONFLICT).build();
                }
                else  {
                    return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
                    // TODO fallback
                }


            }

        }
        catch (Exception e) {
            return Response.status(Response.Status.METHOD_NOT_ALLOWED).build();
        }
        return Response.status(Response.Status.METHOD_NOT_ALLOWED).build();
    }


    @Operation(description = "Customer pays and confirms appointment.", summary = "Pay appointment")
    @APIResponses({
            @APIResponse(responseCode = "201",
                    description = "Appointment successfully paid."
            ),
            @APIResponse(responseCode = "503", description = "Appointments service unavailable."),
            @APIResponse(responseCode = "402", description = "Not enough money.")
    })
    @POST
    @Path("/{id}/pay")
    public Response payAppointment(@RequestBody(description = "Object with appointment data", required = true) String jsonString,
                                   @Parameter(description = "Customer id", required = true) @PathParam("id") Integer id) {


        Integer appointmentId = customerBean.payAppointment(id, jsonString);
        log.info("appointmentId: " + appointmentId);
        if (appointmentId > 0) {
            return Response.status(Response.Status.CREATED).entity(appointmentId).build();
        }
        else {
            if (appointmentId == -1) {
                return Response.status(Response.Status.PAYMENT_REQUIRED).build();
            }else if (appointmentId == -2) {
                return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
            }


        }
        return Response.status(Response.Status.BAD_REQUEST).build();

    }

}
