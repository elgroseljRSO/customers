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
import si.fri.rso.samples.imagecatalog.lib.ImageMetadata;
import si.fri.rso.samples.imagecatalog.models.entities.ServiceType;
import si.fri.rso.samples.imagecatalog.services.beans.ServiceTypeBean;

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
@Path("/service_types")
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

    @Operation(description = "Get a service type.", summary = "Get a service type")
    @APIResponses({
            @APIResponse(responseCode = "200",
                    description = "Service type",
                    content = @Content(
                            schema = @Schema(implementation = ImageMetadata.class))
            )})
    @GET
    @Path("/{serviceTypeId}")
    public Response getServiceType(@Parameter(description = "Service id", required = true)
                                     @PathParam("serviceTypeId") Integer serviceTypeId) {

        ServiceType serviceType = serviceTypeBean.getServiceType(serviceTypeId);

        if (serviceType == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.status(Response.Status.OK).entity(serviceType).build();
    }


    @Operation(description = "Add service type.", summary = "Add service type")
    @APIResponses({
            @APIResponse(responseCode = "201",
                    description = "Service type successfully added."
            ),
            @APIResponse(responseCode = "405", description = "Validation error .")
    })
    @POST
    public Response createServiceType(@RequestBody(
            description = "Object with service type data",
            required = true, content = @Content(
            schema = @Schema(implementation = ServiceType.class))) ServiceType serviceType) {

        if ((serviceType.getName() == null || serviceType.getCost() == null || serviceType.getDurationHours() == null)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        else {
            serviceType = serviceTypeBean.createServiceType(serviceType);
        }

        return Response.status(Response.Status.OK).entity(serviceType).build();

    }
}
