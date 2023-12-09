package si.fri.rso.samples.imagecatalog.services.beans;

import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.utils.JPAUtils;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Timed;
import si.fri.rso.samples.imagecatalog.models.entities.Appointment;
import si.fri.rso.samples.imagecatalog.models.entities.Employee;
import si.fri.rso.samples.imagecatalog.models.entities.ServiceType;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@RequestScoped
public class AppointmentBean {
    private Logger log = Logger.getLogger(AppointmentBean.class.getName());

    @Inject
    private EntityManager em;


    @Counted(name = "appointmentFilterCounter")
    public List<Appointment> getAppointmentFilter(UriInfo uriInfo) {

        QueryParameters queryParameters = QueryParameters.query(uriInfo.getRequestUri().getQuery()).defaultOffset(0)
                .build();

        return JPAUtils.queryEntities(em, Appointment.class, queryParameters);
    }

    @Timed(name = "appointmentAvailableByServiceTypeTimer", unit = "milliseconds")
    public List<Appointment> getAppointmentAvailableByServiceType(int serviceTypeId) {
        ServiceType serviceType = em.find(ServiceType.class, serviceTypeId);

        int duration = serviceType.getDurationHours();
        int numAvailableAppointments = 30; //TODO

        final CriteriaBuilder cb = em.getCriteriaBuilder();
        final CriteriaQuery<Appointment> cq = cb.createQuery(Appointment.class);
        List<Appointment> appointments = em.createQuery(cq).getResultList();


        final CriteriaBuilder cbE = em.getCriteriaBuilder();
        final CriteriaQuery<Employee> cqE = cbE.createQuery(Employee.class);
        List<Employee> employees = em.createQuery(cqE).getResultList();
        int numEmployees = employees.size();

        List<Appointment> availableAppointments = new ArrayList<>();
        for (int start = 8; start < 20; start++) {
            emmployeeLoop:
            for (int employeeId = 1; employeeId <= numEmployees; employeeId++) {
                Employee employee = em.find(Employee.class, employeeId);
                if (!(employee.getServiceTypes().contains(serviceType))){
                    continue;
                }
                appointmentsLoop:
                for (Appointment appointment : appointments) {
                    for (int i = 0; i < duration; i++) {
                        if (start + i > 20 | appointment.getStart() == start + i && appointment.getEmployee().getId() == employeeId) {
                            break appointmentsLoop;
                        }
                    }
                    // survived
                    Appointment availableAppointment = new Appointment();
                    availableAppointment.setStart(start);
                    availableAppointment.setEmployee(employee);
                    availableAppointment.setService_type(serviceType);
                    availableAppointments.add(availableAppointment);

                    if (availableAppointments.size() >= numAvailableAppointments) {
                        return availableAppointments;
                    }

                    break emmployeeLoop;

                }
            }
        }

        return availableAppointments;
    }

}
