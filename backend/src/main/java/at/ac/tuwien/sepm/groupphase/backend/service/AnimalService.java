package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.Animal;
import at.ac.tuwien.sepm.groupphase.backend.entity.Employee;
import at.ac.tuwien.sepm.groupphase.backend.entity.Message;

import java.util.List;

public interface AnimalService {

    /**
     * Creates an single animal.
     *
     * @param animal to save.
     * @return saved animal.
     */
    Animal saveAnimal(Animal animal);


    /**
     * Method to get all current animals
     * @return a List of all current animals
     */
    List<Animal> getAll();

    Animal getById(Long id);
}
