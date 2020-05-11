package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.Animal;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnimalRepository {

    List<Animal> findByDescription(String description);

    Animal findById(long id);

}
