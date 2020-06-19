package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.AnimalTask;
import at.ac.tuwien.sepm.groupphase.backend.entity.Enclosure;
import at.ac.tuwien.sepm.groupphase.backend.entity.EnclosureTask;
import at.ac.tuwien.sepm.groupphase.backend.entity.Task;
import at.ac.tuwien.sepm.groupphase.backend.types.EmployeeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Repository
public interface EnclosureTaskRepository extends JpaRepository<EnclosureTask, Long> {

    /**
     *Finds all Tasks assigned to an Enclosure
     * @param enclosureIdLong id of the enclosure to find associated tasks from
     * @return The tasks associated with the given enclosure
     */
    @Query("SELECT new EnclosureTask(et.id, t, et.subject) " +
        "FROM EnclosureTask et JOIN Task t ON et.id=t.id WHERE et.subject.id=:enclosureId ORDER BY t.startTime")
    List<EnclosureTask> findAllEnclosureTasksBySubject_Id(@Param("enclosureId")long enclosureIdLong);


    @Query("SELECT new at.ac.tuwien.sepm.groupphase.backend.entity" +
        ".Enclosure(et.subject.id, et.subject.name, et.subject.description, et.subject.publicInfo, et.subject.picture) " +
        "FROM EnclosureTask et WHERE et.id=:taskId")
    Enclosure getTaskSubjectById_WithoutAssignedAnimalsAndTasks(@Param("taskId") long taskIdLong);

    @Query("SELECT new at.ac.tuwien.sepm.groupphase.backend.entity" +
        ".Enclosure(et.subject)" +
        "FROM EnclosureTask et WHERE et.id=:taskId")
    Enclosure getTaskSubjectById(@Param("taskId") long taskIdLong);

    /**
     *Finds the Tasks with the given ID
     * @param taskIdLong id of Task to find
     * @return The EnclosureTask with the given id
     */
    @Query("SELECT new EnclosureTask(et.id, t, et.subject) " +
        "FROM EnclosureTask et JOIN Task t ON et.id=t.id WHERE et.id=:taskId")
    EnclosureTask findEnclosureTaskById(@Param("taskId") long taskIdLong);

    /**
     *Finds the Tasks of the given employee
     * The associated task will not have fetched the assignedEmployee
     * The Subject(Enclosure) will not have fetched the assigned animals and tasks
     * @param employeeUsername username of Employee to find the tasks of
     * @return The EnclosureTask with of the given employee
     */
    @Query("SELECT new EnclosureTask (et.id, t, et.subject)" +
        "FROM EnclosureTask et JOIN Task t ON et.id=t.id WHERE t.assignedEmployee.username =:employeeUsername ORDER BY t.startTime")
    List<EnclosureTask> findEnclosureTaskByEmployeeUsername(@Param("employeeUsername")String employeeUsername);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value = "DELETE " +
        "FROM Task t " +
        "WHERE t.ID IN (SELECT et.ID FROM ENCLOSURE_TASK et WHERE et.SUBJECT_ID=:enclosureId); " +
        "DELETE " +
        "FROM ENCLOSURE_TASK et " +
        "WHERE et.SUBJECT_ID=:enclosureId",
        nativeQuery = true)
    void deleteAllAndBaseTasksBySubject_Id(@Param("enclosureId")long enclosureIdLong);


    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value = "DELETE " +
        "FROM ENCLOSURE_TASK et " +
        "WHERE et.SUBJECT_ID=:enclosureId",
        nativeQuery = true)
    void deleteAllBySubject_Id(@Param("enclosureId")long enclosureIdLong);


    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value = "DELETE " +
        "FROM Task t " +
        "WHERE t.ID IN (SELECT et.ID FROM ENCLOSURE_TASK et); " +
        "DELETE " +
        "FROM ENCLOSURE_TASK",
        nativeQuery = true)
    void deleteAllAndBaseTasks();


    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value =
        "DELETE " +
            "FROM ENCLOSURE_TASK et " +
            "WHERE et.ID=:ecTaskId ; "
            +
            "DELETE " +
            "FROM TASK t " +
            "WHERE t.ID =:ecTaskId ; "
        ,
        nativeQuery = true)
    void deleteEnclosureTaskAndBaseTaskById(@Param("ecTaskId")long enclosureTaskIdLong);

    @Query("SELECT enclosureTask " +
        "FROM EnclosureTask enclosureTask " +
        "WHERE (:#{#filterTask.title} IS NULL OR " +
        "UPPER(enclosureTask.task.title) LIKE CONCAT('%', UPPER(:#{#filterTask.title}), '%')) " +
        "AND (:#{#filterTask.description} IS NULL OR " +
        "UPPER(enclosureTask.task.description) LIKE CONCAT('%', UPPER(:#{#filterTask.description}), '%')) " +
        "AND ((:#{#filterTask.assignedEmployee.username} IS NULL) OR " +
        "enclosureTask.task.assignedEmployee.username LIKE :#{#filterTask.assignedEmployee.username}) " +
        "AND ((:#{#employeeType} IS NULL) OR " +
        "(enclosureTask.task.assignedEmployee.type = :#{#employeeType})) " +
        "AND ((:#{#filterTask.status} IS NULL) OR " +
        "(enclosureTask.task.status = :#{#filterTask.status})) " +
        "AND ((:#{#filterTask.priority} IS NULL) OR " +
        "(enclosureTask.task.priority = :#{#filterTask.priority})) " +
        "ORDER BY enclosureTask.task.startTime")
    List<EnclosureTask> findFilteredTasks(@Param("employeeType") EmployeeType employeeType, @Param("filterTask") Task filterTask);


}