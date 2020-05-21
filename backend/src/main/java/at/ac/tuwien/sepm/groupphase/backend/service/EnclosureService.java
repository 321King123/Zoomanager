package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.Enclosure;

import java.util.List;

public interface EnclosureService {

    /**
     * method to get all Enclosures currently in the database
     *
     * @return List of all Enclosures
     */
    List<Enclosure> getAll();

    /**
     * Get an Enclosure by Id
     *
     * @param id of the enclosure
     * @return Enclosure with the specified id
     */
    Enclosure findById(long id);

    /**
     * Creates a new Enclosure in the Database
     *
     * @param enclosure to be created
     * @return new Enclosure as saved in the Database
     */
    Enclosure create(Enclosure enclosure);

    /**
     * Find Enclosure by Id of the animal that inhabits it
     *
     * @param animalId of the animal that lives in the requested Enclosure
     * @return Enclosure inhabited by animal with specified id
     */
    Enclosure findByAnimalId(long animalId);
}
