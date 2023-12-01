package si.fri.rso.samples.imagecatalog.services.beans;

import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.utils.JPAUtils;
import org.eclipse.microprofile.metrics.annotation.Timed;
//import si.fri.rso.samples.imagecatalog.lib.ImageMetadata;
//import si.fri.rso.samples.imagecatalog.models.converters.ImageMetadataConverter;
import si.fri.rso.samples.imagecatalog.models.entities.Employee;
//import si.fri.rso.samples.imagecatalog.models.entities.ImageMetadataEntity;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@RequestScoped
public class EmployeeBean {
    private Logger log = Logger.getLogger(EmployeeBean.class.getName());

    @Inject
    private EntityManager em;


    public List<Employee> getEmployeesFilter(UriInfo uriInfo) {

        QueryParameters queryParameters = QueryParameters.query(uriInfo.getRequestUri().getQuery()).defaultOffset(0)
                .build();

        return JPAUtils.queryEntities(em, Employee.class, queryParameters);
    }
}
