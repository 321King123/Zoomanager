package at.ac.tuwien.sepm.groupphase.backend.entity;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.*;

@Entity
public class Enclosure {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String name;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "enclosure")
    private List<Animal> animals;

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public List<Animal> getAnimals() { return animals;  }

    public void setAnimals(List<Animal> animals) { this.animals = animals; }


    public static final class EnclosureBuilder {

        private Long id;
        private String name;
        private List<Animal> animals;

        private EnclosureBuilder() { }

        public static EnclosureBuilder anEnclosure() { return new EnclosureBuilder(); }

        public EnclosureBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public EnclosureBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public EnclosureBuilder withAnimals(List<Animal> animals) {
            this.animals = animals;
            return this;
        }

        public Enclosure build() {
            Enclosure enclosure = new Enclosure();
            enclosure.setId(id);
            enclosure.setName(name);
            enclosure.setAnimals(animals);
            return enclosure;
        }

    }
}

