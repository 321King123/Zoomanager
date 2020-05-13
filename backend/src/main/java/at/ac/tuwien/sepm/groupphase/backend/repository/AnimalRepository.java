package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.Animal;
import at.ac.tuwien.sepm.groupphase.backend.entity.Enclosure;
import at.ac.tuwien.sepm.groupphase.backend.entity.Message;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


@Repository
public interface AnimalRepository extends CrudRepository<Animal, Long> {

    /**
     * Finds an animal with a specific description
     *
     * @param description of the animal to be found
     * @return animal with the corresponding description
     */

     List<Animal> findByDescription(String description);

    /**
     * Finds an animal with a specific description
     *
     * @param id of the animal to be found
     * @return animal with the corresponding id
     */

     Animal findById(long id);

}
