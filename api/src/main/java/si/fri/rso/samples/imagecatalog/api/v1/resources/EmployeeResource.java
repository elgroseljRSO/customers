package si.fri.rso.samples.imagecatalog.api.v1.resources;


import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.headers.Header;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import si.fri.rso.samples.imagecatalog.models.entities.Employee;
import si.fri.rso.samples.imagecatalog.services.beans.EmployeeBean;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.logging.Logger;


@ApplicationScoped
@Path("/employees")
public class EmployeeResource {
    private Logger log = Logger.getLogger(EmployeeResource.class.getName());

    @Inject
    private EmployeeBean employeeBean;


    @Context
    protected UriInfo uriInfo;

    @Operation(description = "Get all employees.", summary = "Get all employees.")
    @APIResponses({
            @APIResponse(responseCode = "200",
                    description = "List of employees",
                    content = @Content(schema = @Schema(implementation = Employee.class, type = SchemaType.ARRAY)),
                    headers = {@Header(name = "X-Total-Count", description = "Number of objects in list")}
            )})
    @GET
    public Response getEmployees() {

        List<Employee> employees = employeeBean.getEmployeesFilter(uriInfo);

        return Response.status(Response.Status.OK).entity(employees).build();
    }
}
