package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.Enclosure;
import at.ac.tuwien.sepm.groupphase.backend.entity.EnclosureTask;
import org.hibernate.sql.Select;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface EnclosureTaskRepository extends JpaRepository<EnclosureTask, Long> {

    /**
     *Finds all Tasks assigned to an Enclosure
     * @param enclosureIdLong id of the enclosure to find associated tasks from
     * @return The tasks associated with the given enclosure
     */
    @Query("SELECT new EnclosureTask(et.id, et.priority, t, et.subject) " +
        "FROM EnclosureTask et JOIN Task t ON et.id=t.id WHERE et.subject.id=:enclosureId")
    List<EnclosureTask> findAllEnclosureTasksBySubject_Id(@Param("enclosureId")long enclosureIdLong);


//    @Query("SELECT new at.ac.tuwien.sepm.groupphase.backend.entity" +
//        ".Enclosure(et.subject.id, et.subject.name, et.subject.description, et.subject.publicInfo, et.subject.picture," +
//        "et.subject.animals, et.subject.tasks) " +
//        "FROM EnclosureTask et WHERE et.id=:taskId")
//    Enclosure getTaskSubjectById(@Param("taskId") long taskIdLong);
    
    /**
     *Finds the Tasks with the given ID
     * @param taskIdLong id of Task to find
     * @return The EnclosureTask with the given id
     */
    @Query("SELECT new EnclosureTask(et.id, et.priority, t, et.subject) " +
        "FROM EnclosureTask et JOIN Task t ON et.id=t.id WHERE et.id=:taskId")
    EnclosureTask findEnclosureTaskById(@Param("taskId") long taskIdLong);



}