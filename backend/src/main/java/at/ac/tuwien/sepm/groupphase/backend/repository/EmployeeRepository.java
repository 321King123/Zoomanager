package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, String> {

    /**
     * Finds an employee with a specific username
     *
     * @param username of the employee to be found
     * @return employee with the corresponding username
     */
    Employee findEmployeeByUsername(String username);
}
