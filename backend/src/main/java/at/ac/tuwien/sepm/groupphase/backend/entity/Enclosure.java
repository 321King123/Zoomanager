package at.ac.tuwien.sepm.groupphase.backend.entity;
import java.util.List;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.*;

@Entity
public class Enclosure {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    public int id;

    @Column(nullable = false)
    private String name;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "enclosure")
    private List<Animal> animals;
}
