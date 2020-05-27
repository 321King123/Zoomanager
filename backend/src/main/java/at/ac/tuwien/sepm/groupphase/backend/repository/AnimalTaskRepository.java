package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.Animal;
import at.ac.tuwien.sepm.groupphase.backend.entity.AnimalTask;
import at.ac.tuwien.sepm.groupphase.backend.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityResult;
import javax.persistence.FieldResult;
import javax.persistence.SqlResultSetMapping;
import java.util.List;

@Repository
public interface AnimalTaskRepository extends JpaRepository<AnimalTask, Long> {

    @Query(value = "SELECT anTask.id, anTask.subject_id FROM ANIMAL_TASK anTask JOIN TASK t ON anTask.id=t.id WHERE anTask.subject_id=:animalId ORDER BY t.start_time", nativeQuery = true)
    List<AnimalTask> findAllAnimalTasksBySubject_Id(@Param("animalId") long animalId);

}
