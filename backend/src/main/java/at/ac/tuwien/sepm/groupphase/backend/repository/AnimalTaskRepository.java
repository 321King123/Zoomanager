package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.AnimalTask;
import at.ac.tuwien.sepm.groupphase.backend.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnimalTaskRepository extends JpaRepository<AnimalTask, Long> {

    /**
     * Finds all AnimalTasks from a specific employee
     *
     * @param employee of the employee to be found
     * @return employee with the corresponding username
     */
    List<AnimalTask> findAllByAssignedEmployee(Employee employee);
}
