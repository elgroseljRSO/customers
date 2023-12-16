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
import si.fri.rso.samples.imagecatalog.models.entities.Appointment;
import si.fri.rso.samples.imagecatalog.models.entities.Customer;
import si.fri.rso.samples.imagecatalog.models.entities.ServiceType;
import si.fri.rso.samples.imagecatalog.services.beans.AppointmentBean;
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

//    @Operation(description = "Customer pays and confirms appointment.", summary = "Pay appointment")
//    @APIResponses({
//            @APIResponse(responseCode = "201",
//                    description = "Appointment successfully paid."
//            ),
//            @APIResponse(responseCode = "405", description = "Validation error.")
//    })
//    @POST
//    @Path("/{id}/pay")
//    public Response payAppointment(@RequestBody(description = "Object with appointment data", required = true) String jsonString,
//                                   @Parameter(description = "Customer id", required = true) @PathParam("id") Integer id) {
//
//
//        int appointmentId = customerBean.payAppointment(id, jsonString);
//        return Response.status(Response.Status.OK).entity(appointmentId).build();
//
//
//
//    }

}
