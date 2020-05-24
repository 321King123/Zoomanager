package at.ac.tuwien.sepm.groupphase.backend.repository;
import at.ac.tuwien.sepm.groupphase.backend.entity.Animal;
import at.ac.tuwien.sepm.groupphase.backend.entity.Enclosure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnclosureRepository extends JpaRepository<Enclosure, Long> {

    /**
     * Find enclosure with a specific id
     *
     * @param id of the enclosure
     * @return enclosure with the specified id, if none exist null will be returned
     */
    Enclosure findById(long id);

    /**
     * Find enclosure with a specific id without fetching Tasks or Animals
     *
     * @param id of the enclosure
     * @return enclosure with the specified id, if none exist null will be returned
     */
    @Query("SELECT new Enclosure(e.id, e.name, e.description, e.publicInfo, e.picture) " +
        "FROM Enclosure e ")
    Enclosure findById_WithoutTasksAndAnimals(long id);

    /**
     * Finds all enclosures
     *
     * @return List of all enclosures in the database
     */
    List<Enclosure> findAll();

}
