package si.fri.rso.samples.imagecatalog.models.entities;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "employees")
@NamedQueries(value =
        {
                @NamedQuery(name = "Employee.getAll",
                        query = "SELECT e FROM Employee e")
        })
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;


    @ManyToMany
    @JoinTable(
            name = "employees_service_types",
            joinColumns = @JoinColumn(name = "employee_id"),
            inverseJoinColumns = @JoinColumn(name = "service_type_id"))
    private Set<ServiceType> serviceTypes;


    @OneToMany(mappedBy = "employee")
    private Set<Appointment> appointments;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<ServiceType> getServiceTypes() {
        return serviceTypes;
    }

    public void setServiceTypes(Set<ServiceType> serviceTypes) {
        this.serviceTypes = serviceTypes;
    }
}
