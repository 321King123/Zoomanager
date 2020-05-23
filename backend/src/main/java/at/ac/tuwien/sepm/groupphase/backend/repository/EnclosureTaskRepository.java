package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.Enclosure;
import at.ac.tuwien.sepm.groupphase.backend.entity.EnclosureTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface EnclosureTaskRepository extends JpaRepository<EnclosureTask, Long> {

//    /**
//     *Finds all Tasks assigned to an Enclosure
//     * @param enclosureId id of the enclosure to find associated tasks from
//     * @return The tasks associated with the given enclosure
//     */
//    List<Enclosure> findAllBySubject_Id(Long enclosureId);

//    @Transactional
//    @Query(
//        value = "SELECT e.ID, e.NAME, e.DESCRIPTION, e.PICTURE, e.PUBLIC_INFO FROM ENCLOSURE e WHERE ID = (SELECT SUBJECT_ID FROM ENCLOSURE_TASK et WHERE et.ID=:taskId)",
//        nativeQuery = true)
//    Enclosure getTaskSubjectById(@Param("taskId") long taskIdLong);

//    @Query(
//        value = "SELECT et From ENCLOSURE_TASK et JOIN et.SUBJECT_ID WHERE et.ID=:taskId",
//        nativeQuery = true)
//    EnclosureTask getTaskAndSubjectById(@Param("taskId") long taskIdLong);

//    @Transactional
//    @Query("select * from EnclosureTask on")
//    Enclosure getTaskById(@Param("taskId") long taskIdLong);

    @Transactional
    @Query(value =
        "SELECT * FROM ENCLOSURE_TASK et " +
            "INNER JOIN ENCLOSURE e ON et.SUBJECT_ID=e.ID " +
            "INNER JOIN Task t ON et.id = t.id " +
            "WHERE et.ID=:taskId",
    nativeQuery = true)
    EnclosureTask getEnclosureTaskWithEnclosureAndAssociatedTaskById(@Param("taskId") long taskIdLong);

    @Transactional
    @Query("FROM EnclosureTask et " +
            "JOIN Task t ON et.id = t.id " +
            "WHERE et.id=:taskId")
    EnclosureTask getComplete(@Param("taskId") long taskIdLong);

}