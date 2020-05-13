package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.Animal;
import at.ac.tuwien.sepm.groupphase.backend.repository.AnimalRepository;

import at.ac.tuwien.sepm.groupphase.backend.service.AnimalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.lang.invoke.MethodHandles;


@Service
public class SimpleAnimalService implements AnimalService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final AnimalRepository animalRepository;

    @Autowired
    public SimpleAnimalService(AnimalRepository animalRepository) {
        this.animalRepository = animalRepository;
    }


    @Override
    public Animal saveAnimal(Animal animal) {
        LOGGER.debug("Save new animal {}", animal);
        return animalRepository.save(animal);
    }

}
