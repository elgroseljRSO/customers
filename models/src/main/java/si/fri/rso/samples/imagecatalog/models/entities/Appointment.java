package si.fri.rso.samples.imagecatalog.models.entities;

import javax.persistence.*;

@Entity
@Table(name = "appointments")
@NamedQueries(value =
        {
                @NamedQuery(name = "Appointment.getAll",
                        query = "SELECT a FROM Appointment a")
        })
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer start;
//    private Integer finish;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getStart() {
        return start;
    }

    public void setStart(Integer start) {
        this.start = start;
    }


    private String customer;

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        customer = customer;
    }

    @ManyToOne
    @JoinColumn(name = "service_type_id")
    private ServiceType service_type;

    public ServiceType getService_type() {
        return service_type;
    }

    public void setService_type(ServiceType serviceType) {
        this.service_type = serviceType;
    }

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee_id) {
        this.employee = employee_id;
    }
}
