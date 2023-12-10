package si.fri.rso.samples.imagecatalog.api.v1.resources;

import com.kumuluz.ee.logs.cdi.Log;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.headers.Header;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import si.fri.rso.samples.imagecatalog.models.entities.Appointment;
import si.fri.rso.samples.imagecatalog.models.entities.Customer;
import si.fri.rso.samples.imagecatalog.services.beans.AppointmentBean;
import si.fri.rso.samples.imagecatalog.services.beans.CustomerBean;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
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

}
