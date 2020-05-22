package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.Animal;
import at.ac.tuwien.sepm.groupphase.backend.entity.Employee;
import at.ac.tuwien.sepm.groupphase.backend.types.EmployeeType;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, String> {


    /**
     * Finds an employee with a specific username
     *
     * @param username of the employee to be found
     * @return employee with the corresponding username
     */
    Employee findEmployeeByUsername(String username);

    /**
     * Method to get all employees
     * @return List containing all Employees
     */
    List<Employee> findAll();


    /**
     * Get Employee by Example
     * @return List containing all Employees that match the Example
     */
    <S extends Employee> List<S> findAll(Example<S> example);

    /**
     *
     * @param animal is the searched for animal
     * @return List of Employees that are assigned to given animal
     */
    List<Employee> findByAssignedAnimalsContains(Animal animal);


    /**
     * Method to get all employees of specific Type
     * @return List containing all Employees of specific Type
     */
    List<Employee> findAllByType(EmployeeType type);
}
