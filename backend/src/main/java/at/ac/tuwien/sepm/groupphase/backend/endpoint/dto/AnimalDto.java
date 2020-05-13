package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Animal;
import at.ac.tuwien.sepm.groupphase.backend.entity.Enclosure;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Objects;


public class AnimalDto {

    @NotNull(message = "Name must not be null")
    private String name;

    private Long id;

    @NotNull(message = "Description must not be null")
    private String description;

    @NotNull(message = "Species must not be null")
    private String species;

    private String enclosure;

    private String publicInformation;


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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AnimalDto)) return false;
        AnimalDto animalDto = (AnimalDto) o;
        return getId().equals(animalDto.getId()) &&
            getName().equals(animalDto.getName()) &&
            getDescription().equals(animalDto.getDescription()) &&
            getSpecies().equals(animalDto.getSpecies()) &&
            Objects.equals(getEnclosure(), animalDto.getEnclosure()) &&
            Objects.equals(getPublicInformation(), animalDto.getPublicInformation());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getId(), getDescription(), getSpecies(), getEnclosure(), getPublicInformation());
    }

    @Override
    public String toString() {
        return "AnimalDto{" +
            "name='" + name + '\'' +
            ", id=" + id +
            ", description='" + description + '\'' +
            ", species='" + species + '\'' +
            ", enclosure=" + enclosure +
            ", publicInformation='" + publicInformation + '\'' +
            '}';
    }

    public static final class AnimalDtoBuilder{

        private String name;
        private Long id;
        private String description;
        private String species;
        private String enclosure;
        private String publicInformation;

        private AnimalDtoBuilder() {
        }

        public static  AnimalDtoBuilder anAnimalDtoBuilder() { return new AnimalDtoBuilder(); }

        public AnimalDtoBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public AnimalDtoBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public AnimalDtoBuilder withSpecies(String species) {
            this.species = species;
            return this;
        }

        public AnimalDtoBuilder withPublicInformation(String publicInformation) {
            this.publicInformation = publicInformation;
            return this;
        }

        public AnimalDtoBuilder withEnclosure(String enclosure) {
            this.enclosure = enclosure;
            return this;
        }

        public AnimalDto build() {
            AnimalDto animalDto = new AnimalDto();

            animalDto.setName(name);
            animalDto.setDescription(description);
            animalDto.setSpecies(species);
            animalDto.setPublicInformation(publicInformation);
            animalDto.setEnclosure(enclosure);
            return animalDto;
        }

    }
}
