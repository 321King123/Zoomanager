package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.Animal;
import at.ac.tuwien.sepm.groupphase.backend.entity.AnimalTask;
import at.ac.tuwien.sepm.groupphase.backend.entity.Employee;
import at.ac.tuwien.sepm.groupphase.backend.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityResult;
import javax.persistence.FieldResult;
import javax.persistence.SqlResultSetMapping;
import java.util.List;
import java.util.Optional;

@Repository
public interface AnimalTaskRepository extends JpaRepository<AnimalTask, Long> {

    /**
     *Finds all Tasks assigned to an Animal
     * @param animalId id of the animal to find associated tasks from
     * @return The tasks associated with the given animal
     */
    @Query("SELECT new AnimalTask(animT.id, t, animT.subject) " +
        "FROM AnimalTask animT JOIN Task t ON animT.id=t.id WHERE animT.subject.id=:animalId ORDER BY t.startTime")
    List<AnimalTask> findAllAnimalTasksBySubject_Id(@Param("animalId") long animalId);

    /**
     *Finds all Events assigned to an Animal
     * @param animalId id of the animal to find associated Events from
     * @return The events associated with the given animal
     */
    @Query("SELECT new AnimalTask(animT.id, t, animT.subject) " +
        "FROM AnimalTask animT JOIN Task t ON animT.id=t.id WHERE animT.subject.id=:animalId AND t.event = true ORDER BY t.startTime")
    List<AnimalTask> findAllAnimalEventsBySubject_Id(@Param("animalId") long animalId);

    /**
     * Finds Animal Event by id
     *
     * @param id identifies the event
     * @return event with corresponding id
     */
    @Query("SELECT new AnimalTask(animT.id, t, animT.subject) " +
            "FROM AnimalTask animT JOIN Task t ON animT.id=t.id WHERE t.event = true AND animT.id =:id")
    Optional<AnimalTask> findAnimalEventById(@Param("id")long id);

}
