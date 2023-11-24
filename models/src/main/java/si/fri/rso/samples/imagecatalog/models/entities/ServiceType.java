package si.fri.rso.samples.imagecatalog.models.entities;

import javax.persistence.*;

@Entity
@Table(name = "service_types")
@NamedQueries(value =
        {
                @NamedQuery(name = "ServiceType.getAll",
                        query = "SELECT st FROM ServiceType st")
        })
public class ServiceType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private Integer durationHours;

    private Integer cost;

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

    public Integer getDurationHours() {
        return durationHours;
    }

    public void setDurationHours(Integer durationHours) {
        this.durationHours = durationHours;
    }

    public Integer getCost() {
        return cost;
    }

    public void setCost(Integer cost) {
        this.cost = cost;
    }
}
