package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.AnimalTask;
import at.ac.tuwien.sepm.groupphase.backend.entity.Employee;
import at.ac.tuwien.sepm.groupphase.backend.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    /**
     * Finds an task by id
     *
     * @param id identifies the task
     * @return task with corresponding id
     */
    Optional<Task> findById(Long id);

    /**
     * Finds all AnimalTasks from a specific employee
     *
     * @param employee of the employee to be found
     * @return employee with the corresponding username
     */
    List<Task> findAllByAssignedEmployee(Employee employee);
}