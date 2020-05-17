package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Employee;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;


public class AnimalDto {

    @NotNull(message = "Name must not be null")
    @NotBlank(message = "Name must not be empty")
    private String name;

    private Long id;

    @NotNull(message = "Description must not be null")
    @NotBlank(message = "Description must not be empty")
    private String description;

    @NotNull(message = "Species must not be null")
    @NotBlank(message = "Species must not be empty")
    private String species;

    private String enclosure;

    private String publicInformation;

    private List<Employee> caretakers;


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

    public List<Employee> getCaretakers() { return caretakers; }

    public void setCaretakers(List<Employee> caretakers) { this.caretakers = caretakers; }

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
        private List<Employee> caretakers;

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

        public AnimalDtoBuilder withCaretakers(List<Employee> caretakers) {
            this.caretakers = caretakers;
            return this;
        }

        public AnimalDto build() {
            AnimalDto animalDto = new AnimalDto();

            animalDto.setName(name);
            animalDto.setDescription(description);
            animalDto.setSpecies(species);
            animalDto.setPublicInformation(publicInformation);
            animalDto.setEnclosure(enclosure);
            animalDto.setCaretakers(caretakers);
            return animalDto;
        }

    }
}
