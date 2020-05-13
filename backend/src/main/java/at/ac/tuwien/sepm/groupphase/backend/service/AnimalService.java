package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.Animal;
import at.ac.tuwien.sepm.groupphase.backend.entity.Message;

public interface AnimalService {

    /**
     * Creates an single animal.
     *
     * @param animal to save.
     * @return saved animal.
     */
    Animal saveAnimal(Animal animal);
}
