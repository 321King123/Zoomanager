package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import at.ac.tuwien.sepm.groupphase.backend.exception.IncorrectTypeException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFreeException;

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
     * Method to get all assigned enclosures of the employee
     * @return a List of All assigned enclosures
     */
    List<Enclosure> findAssignedEnclosures(String employeeUsername);

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
     * Delete a single employee by username.
     *
     * @param username the username of the employee to delete
     */
    void deleteEmployeeByUsername(String username);


    /**
     * Checks if an Employee is free between start and end
     *
     * @param task task that contains time fields
     * @param employee employee you want to check
     * @return true if time is free
     * @throws NotFreeException if time is not free
     */
    boolean employeeIsFreeBetweenStartingAndEndtime(Employee employee, Task task) ;

    /**
     *Checks if Employee is Assigned to specific Animal
     * @param username username of empoyee
     * @param animalId id of animal
     */
    boolean isAssignedToAnimal(String username, Long animalId);

    /**
     *Checks if Employee is Assigned to specific Enclosure
     * @param username username of empoyee
     * @param enclosureId id of animal
     */
    public boolean isAssignedToEnclosure(String username, Long enclosureId);

    /**
     *Checks if Employee has rights to change assignment of Task
     * Will return false for admins they are handled separatly
     * @param UsernameEmployee employee to check
     * @param taskId task to check
     */
    boolean hasTaskAssignmentPermissions(String UsernameEmployee, Long taskId);

    /**
     * Checks if employee if free and of right type for task
     * @param employee to check
     * @param task task to be assigned to
     * @return true when employee can be assigned
     * @throws IncorrectTypeException when an employee of an invalid type would be assigned to the task
     * @throws NotFreeException if employee is not free during that time
     */
    boolean canBeAssignedToTask(Employee employee, Task task);

    /**
     * Gets all Employees assigned to specific Animal
     * @param animal animal you want the information for
     */
    List<Employee> getAllAssignedToAnimal(Animal animal);


    /**
     * Gets all Employees assigned to specific Enclosure
     * @param enclosure you want the info for
     */
    List<Employee> getAllAssignedToEnclosure(Enclosure enclosure);


    /**
     * Gets all Doctors
     */
    List<Employee> getAllDocotrs();

    /**
     * Gets all Doctors
     */
    List<Employee> getAllJanitors();

    /**
     * Editing Enclosure that is already in the Database
     *
     * @param employeeToEdit to be edited
     * @param oldUsername username to be edited
     * @return edited Enployee as saved in the Database
     */
    Employee editEmployee(Employee employeeToEdit, String oldUsername);

    boolean checkIfThereAreTaskBetweenGivenWorkHours(Employee employee);
}
