package at.ac.tuwien.sepm.groupphase.backend.entity;

import java.util.List;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.*;
import java.io.Serializable;

@Entity

public class Animal implements Serializable{

    @Column(nullable = false)
    private String name;

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String species;

    @Column
    private String enclosure;

    @Column
    private String publicInformation;

    @ManyToMany(mappedBy = "assignedAnimals", fetch = FetchType.EAGER)
    private List<Employee> caretakers;

   /* public Animal( String name, String description, String species) {
        this.name = name;
        this.description = description;
        this.species = species;
    }
*/
   public Animal () {  }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public String getSpecies() { return species; }

    public void setSpecies(String species) { this.species = species; }

    public String getEnclosure() { return enclosure; }

    public void setEnclosure(String enclosure) { this.enclosure = enclosure; }

    public String getPublicInformation() { return publicInformation; }

    public void setPublicInformation(String publicInformation) { this.publicInformation = publicInformation; }

    public List<Employee> getCaretakers() {
        return caretakers;
    }

    public void setCaretakers(List<Employee> caretakers) {
        this.caretakers = caretakers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!( o instanceof Animal)) return false;
        Animal animal = (Animal) o;
        return getId().equals(animal.getId()) &&
            Objects.equals(getName(), animal.getName()) &&
            Objects.equals(getDescription(), animal.getDescription()) &&
            Objects.equals(getSpecies(), animal.getSpecies()) &&
            Objects.equals(getEnclosure(), animal.getEnclosure()) &&
            Objects.equals(getPublicInformation(), animal.getPublicInformation());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getId(), getDescription(), getSpecies(), getEnclosure(), getPublicInformation());
    }

    @Override
    public String toString() {
        return "Animal{" +
            "name='" + name + '\'' +
            ", id=" + id +
            ", description='" + description + '\'' +
            ", species='" + species + '\'' +
            ", enclosure=" + enclosure +
            ", publicInformation='" + publicInformation + '\'' +
            '}';
    }


    public static final class AnimalBuilder {

        private Long id;
        private String name;
        private String description;
        private String species;
        private String enclosure;
        private String publicInformation;
        private List<Employee> caretakers;

        private AnimalBuilder() {
        }

        public static AnimalBuilder anAnimal() {
            return new AnimalBuilder();
        }

        public AnimalBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public AnimalBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public AnimalBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public AnimalBuilder withSpecies(String species) {
            this.species = species;
            return this;
        }

        public AnimalBuilder withEnclosure(String enclosure) {
            this.enclosure = enclosure;
            return this;
        }

        public AnimalBuilder withPublicInformation(String publicInformation) {
            this.publicInformation = publicInformation;
            return this;
        }

        public AnimalBuilder withCaretakers(List<Employee> caretakers) {
            this.caretakers = caretakers;
            return this;
        }

        public Animal build() {
            Animal animal = new Animal();

            animal.setId(id);
            animal.setDescription(description);
            animal.setEnclosure(enclosure);
            animal.setName(name);
            animal.setPublicInformation(publicInformation);
            animal.setCaretakers(caretakers);

            return animal;
        }

   }

}
