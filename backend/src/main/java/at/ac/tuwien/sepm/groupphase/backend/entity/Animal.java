package at.ac.tuwien.sepm.groupphase.backend.entity;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.*;
import java.io.Serializable;

@Entity
public class Animal implements Serializable{

    @Column(nullable = false)
    private String name;

    @Column
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private int id;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String species;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    private Enclosure enclosure;

    @Column
    private String publicInformation;

    public Animal( String name, String description, String species) {
        this.name = name;
        this.description = description;
        this.species = species;
    }

   protected Animal () {}

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public String getSpecies() { return species; }

    public void setSpecies(String species) { this.species = species; }

    public Enclosure getEnclosure() { return enclosure; }

    public void setEnclosure(Enclosure enclosure) { this.enclosure = enclosure; }

    public String getPublicInformation() { return publicInformation; }

    public void setPublicInformation(String publicInformation) { this.publicInformation = publicInformation; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!( o instanceof Animal)) return false;
        Animal animal = (Animal) o;
        return getId() == animal.getId() &&
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
}
