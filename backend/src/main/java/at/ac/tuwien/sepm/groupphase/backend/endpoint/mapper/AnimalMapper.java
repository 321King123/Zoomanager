package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.AnimalDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.DetailedMessageDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SimpleMessageDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Animal;
import at.ac.tuwien.sepm.groupphase.backend.entity.Message;
import org.springframework.stereotype.Component;

@Component
public class AnimalMapper {

    public AnimalDto animalToAnimalDto(Animal animal) {
        if ( animal == null ) {
            return null;
        }

        AnimalDto animalDto = new AnimalDto();

        animalDto.setName(animal.getName());
        animalDto.setDescription(animal.getDescription());
        animalDto.setSpecies(animal.getSpecies());
        animalDto.setEnclosure(animal.getEnclosure());
        animalDto.setPublicInformation(animal.getPublicInformation());

        return animalDto;
    }

    public Animal AnimalDtoToAnimal(AnimalDto animalDto) {
        if ( animalDto == null ) {
            return null;
        }

        Animal animal = new Animal();

        animal.setName(animalDto.getName());
        animal.setDescription(animalDto.getDescription());
        animal.setSpecies(animalDto.getSpecies());
        animal.setEnclosure(animalDto.getEnclosure());
        animal.setPublicInformation(animalDto.getPublicInformation());

        return animal;
    }
}
