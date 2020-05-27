package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.Animal;
import at.ac.tuwien.sepm.groupphase.backend.entity.AnimalTask;
import at.ac.tuwien.sepm.groupphase.backend.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnimalTaskRepository extends JpaRepository<AnimalTask, Long> {


    @Query("SELECT new AnimalTask(at.id, at.task, at.subject) " +
        "FROM AnimalTask at JOIN Task t ON at.id=t.id WHERE at.subject.id=:animalId ORDER BY t.startTime")
    List<AnimalTask> findAllBySubject_Id(@Param("animalId")Long animalId);

}
