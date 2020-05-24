package at.ac.tuwien.sepm.groupphase.backend.repository;
import at.ac.tuwien.sepm.groupphase.backend.entity.Animal;
import at.ac.tuwien.sepm.groupphase.backend.entity.Enclosure;
import org.springframework.data.jpa.repository.JpaRepository;
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
     * Finds all enclosures
     *
     * @return List of all enclosures in the database
     */
    List<Enclosure> findAll();

}
