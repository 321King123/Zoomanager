package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.Animal;
import at.ac.tuwien.sepm.groupphase.backend.entity.Employee;
import at.ac.tuwien.sepm.groupphase.backend.entity.Enclosure;
import at.ac.tuwien.sepm.groupphase.backend.entity.Message;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SQLInsert;
import org.hibernate.annotations.SQLUpdate;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Repository
public interface AnimalRepository extends JpaRepository<Animal, Long> {

    /**
     * Finds all animals with a specific description
     *
     * @param description of the animal to be found
     * @return animal with the corresponding description
     */

     List<Animal> findByDescription(String description);

    /**
     * Finds an animal with a specific id
     *
     * @param id of the animal to be found
     * @return animal with the corresponding id
     */

     Animal findById(long id);

    /**
     * Finds all Animals with a specific caretaker
     *
     * @param caretaker of which we want the a to be found
     * @return animal with the corresponding description
     */
    List<Animal> findAllByCaretakers(Employee caretaker);

    @Transactional
    @Modifying
    @Query(
        value = "INSERT INTO EMPLOYEE_ASSIGNED_ANIMALS(CARETAKERS_USERNAME, ASSIGNED_ANIMALS_ID) VALUES(:caretaker, :animalId)",
    nativeQuery = true)
    void assignAnimalToCaretaker( @Param("caretaker") String caretaker, @Param("animalId") long animalId);


    @Transactional
    @Modifying
    @Query(value = "DELETE FROM EMPLOYEE_ASSIGNED_ANIMALS eaa where eaa.ASSIGNED_ANIMALS_ID=:animalId", nativeQuery = true)
    void deleteAssignmentsOfAnimal(@Param("animalId") long animalId);
}
