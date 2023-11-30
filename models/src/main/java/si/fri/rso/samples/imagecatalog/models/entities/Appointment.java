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

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

//    private ServiceType serviceType; #TODO

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

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

//    @Column(name="customer") # TODO
//    private String customer;
//
//    public String getCustomer() {
//        return customer;
//    }
//
//    public void setCustomer(String customer) {
//        this.customer = customer;
//    }
}
