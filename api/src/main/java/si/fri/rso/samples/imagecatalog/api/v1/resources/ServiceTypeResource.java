package si.fri.rso.samples.imagecatalog.api.v1.resources;

import com.kumuluz.ee.logs.cdi.Log;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.headers.Header;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import si.fri.rso.samples.imagecatalog.models.entities.ServiceType;
import si.fri.rso.samples.imagecatalog.services.beans.ServiceTypeBean;

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
@Path("/serviceTypes")
public class ServiceTypeResource {


    private Logger log = Logger.getLogger(ServiceTypeResource.class.getName());

    @Inject
    private ServiceTypeBean serviceTypeBean;


    @Context
    protected UriInfo uriInfo;

    @Operation(description = "Get all service types.", summary = "Get all service types.")
    @APIResponses({
            @APIResponse(responseCode = "200",
                    description = "List of service types",
                    content = @Content(schema = @Schema(implementation = ServiceType.class, type = SchemaType.ARRAY)),
                    headers = {@Header(name = "X-Total-Count", description = "Number of objects in list")}
            )})
    @GET
    public Response getServiceTypes() {

        List<ServiceType> serviceTypes = serviceTypeBean.getServiceTypesFilter(uriInfo);

        return Response.status(Response.Status.OK).entity(serviceTypes).build();
    }
}
