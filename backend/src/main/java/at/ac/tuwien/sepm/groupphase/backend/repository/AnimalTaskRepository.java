package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.Animal;
import at.ac.tuwien.sepm.groupphase.backend.entity.AnimalTask;
import at.ac.tuwien.sepm.groupphase.backend.entity.Employee;
import at.ac.tuwien.sepm.groupphase.backend.entity.Task;
import at.ac.tuwien.sepm.groupphase.backend.types.EmployeeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.sql.Timestamp;

import javax.persistence.EntityResult;
import javax.persistence.FieldResult;
import javax.persistence.SqlResultSetMapping;
import java.util.List;

@Repository
public interface AnimalTaskRepository extends JpaRepository<AnimalTask, Long> {

    @Query("SELECT new AnimalTask(animT.id, t, animT.subject) " +
        "FROM AnimalTask animT JOIN Task t ON animT.id=t.id WHERE animT.subject.id=:animalId ORDER BY t.startTime")
    List<AnimalTask> findAllAnimalTasksBySubject_Id(@Param("animalId") long animalId);

    //TO_TIMESTAMP('22/09/1999 23:20', 'DD/MM/YYYY HH24:MI')

    @Query("SELECT animaltask " +
        "FROM AnimalTask animaltask " +
        "WHERE (:#{#filterTask.title} IS NULL OR " +
        "UPPER(animaltask.task.title) LIKE CONCAT('%', UPPER(:#{#filterTask.title}), '%')) " +
        "AND (:#{#filterTask.description} IS NULL OR " +
        "UPPER(animaltask.task.description) LIKE CONCAT('%', UPPER(:#{#filterTask.description}), '%')) " +
        "AND ((:#{#filterTask.assignedEmployee.username} IS NULL) OR " +
        "animaltask.task.assignedEmployee.username LIKE :#{#filterTask.assignedEmployee.username}) " +
        "AND ((:#{#employeeType} IS NULL) OR " +
        "(animaltask.task.assignedEmployee.type = :#{#employeeType})) " +
        "AND ((:#{#filterTask.status} IS NULL) OR " +
        "(animaltask.task.status = :#{#filterTask.status})) " +
        "AND ((:#{#filterTask.priority} IS NULL) OR " +
        "(animaltask.task.priority = :#{#filterTask.priority})) " +
        "ORDER BY animaltask.task.startTime")
    List<AnimalTask> findFilteredTasks(@Param("employeeType") EmployeeType employeeType, @Param("filterTask") Task filterTask);

}
