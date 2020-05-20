package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.Animal;
import at.ac.tuwien.sepm.groupphase.backend.entity.Employee;

import java.time.LocalDateTime;
import java.util.List;

public interface EmployeeService  {

    /**
     * Used for creating new employees
     * @param employee to be created
     * */
    Employee createEmployee(Employee employee);

    /**
     * Method to get all current employees
     * If empty an empty List gets returned
     * @return a List of All current employees
     */
    List<Employee> getAll();

    /**
     * Method for filtered list of all current employees search fields can be combined.
     * If a field is null it is not taken into consideration
     * @param employee field name and type are taken into consideration for search
     * if name is specified all Employees that contain the substring are returned
     * @return a List of All current employees
     */
    List<Employee> findByNameAndType(Employee employee);


    /**
     * Method to get all assigned animals of the employee
     * @return a List of All assigned animals
     */
    List<Animal> findAssignedAnimals(String employeeUsername);

    /**
     * Assigns the animal to the Employee
     */
    void assignAnimal(String employeeUsername, long AnimalId);

    /**
     * Find a single employee by username.
     *
     * @param username the username of the employee to find
     * @return the employee
     */
    Employee findByUsername(String username);


    /**
     * Checks if an Employee is free between start and end
     *
     * @param start is the Time the task you want to assign starts
     * @param end is the Time the task you want to assign ends
     * @param employee employee you want to check
     * @return true if time is free false if occupied
     */
    boolean employeeIsFreeBetweenStartingAndEndtime(Employee employee, LocalDateTime start, LocalDateTime end);
}
