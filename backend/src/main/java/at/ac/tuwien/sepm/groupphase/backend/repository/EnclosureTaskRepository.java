package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.Enclosure;
import at.ac.tuwien.sepm.groupphase.backend.entity.EnclosureTask;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EnclosureTaskRepository extends JpaRepository<EnclosureTask, Long> {

//    /**
//     *Finds all Tasks assigned to an Enclosure
//     * @param enclosureId id of the enclosure to find associated tasks from
//     * @return The tasks associated with the given enclosure
//     */
//    List<Enclosure> findAllBySubject_Id(Long enclosureId);

}