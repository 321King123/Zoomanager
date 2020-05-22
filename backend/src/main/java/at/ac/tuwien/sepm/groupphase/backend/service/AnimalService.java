package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.Animal;

import java.util.List;
import java.util.Optional;

public interface AnimalService {

    /**
     * Creates an single animal.
     * @param animal to save.
     * @return saved animal.
     */
    Animal saveAnimal(Animal animal);


    /**
     * Method to get all current animals
     * @return a List of all current animals
     */
    List<Animal> getAll();

    /**
     * Deletes an single animal.
     * @param id of animal to be deleted.
     */
    void deleteAnimal(Long id);


    /**
     * finds an single animal.
     * @param id of animal to be deleted.
     * @return
     */
    Animal findAnimalById(Long id);
}
