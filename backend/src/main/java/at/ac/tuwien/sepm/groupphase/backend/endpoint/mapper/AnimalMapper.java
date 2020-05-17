package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.AnimalDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Animal;
import org.springframework.stereotype.Component;

@Component
public class AnimalMapper {

    public AnimalDto animalToAnimalDto(Animal animal) {
        if ( animal == null ) {
            return null;
        }

        AnimalDto animalDto = new AnimalDto();

        animalDto.setId(animal.getId());
        animalDto.setName(animal.getName());
        animalDto.setDescription(animal.getDescription());
        animalDto.setSpecies(animal.getSpecies());
        animalDto.setEnclosure(animal.getEnclosure());
        animalDto.setPublicInformation(animal.getPublicInformation());
        //animalDto.setCaretakers(animal.getCaretakers());

        return animalDto;
    }

    public Animal AnimalDtoToAnimal(AnimalDto animalDto) {
        if ( animalDto == null ) {
            return null;
        }

        Animal animal = new Animal();

        animal.setId(animalDto.getId());
        animal.setName(animalDto.getName());
        animal.setDescription(animalDto.getDescription());
        animal.setSpecies(animalDto.getSpecies());
        animal.setEnclosure(animalDto.getEnclosure());
        animal.setPublicInformation(animalDto.getPublicInformation());
        animal.setCaretakers(animalDto.getCaretakers());

        return animal;
    }
}
