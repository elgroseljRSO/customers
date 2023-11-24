package si.fri.rso.samples.imagecatalog.services.beans;

import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.utils.JPAUtils;
import org.eclipse.microprofile.metrics.annotation.Timed;
import si.fri.rso.samples.imagecatalog.models.entities.Appointment;
import si.fri.rso.samples.imagecatalog.models.entities.Employee;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.logging.Logger;

@RequestScoped
public class AppointmentBean {
    private Logger log = Logger.getLogger(AppointmentBean.class.getName());

    @Inject
    private EntityManager em;

    @Timed
    public List<Appointment> getAppointmentFilter(UriInfo uriInfo) {

        QueryParameters queryParameters = QueryParameters.query(uriInfo.getRequestUri().getQuery()).defaultOffset(0)
                .build();

        return JPAUtils.queryEntities(em, Appointment.class, queryParameters);
    }
}
