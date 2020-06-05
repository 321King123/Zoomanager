package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.*;

import java.time.temporal.ChronoUnit;
import java.util.List;

public interface TaskService {

    /**
     * Method to create an AnimalTask
     * Requirements for assignment:
     * The employee assigned to the task must either be a Doctor or Animal Caretaker,
     * The employee must have no other tasks assigned between start and end time
     * @param task to be created
     * @param animal animal the task is assigned to
     * */
    AnimalTask createAnimalTask(Task task, Animal animal);

    /**
     * Method to create an EnclosureTask
     * Requirements for assignment:
     * The employee assigned to the task must either be a Janitor or Animal Caretaker,
     * The employee must have no other tasks assigned between start and end time
     * @param task to be created
     * @param enclosure enclosure the task is assigned to
     * */
    EnclosureTask createEnclosureTask(Task task, Enclosure enclosure);

    /**
     * Delete all AnimalTasks belonging to an Animal
     * @param animalId you want to delete all AnimalTasks for
     * */
    void deleteAnimalTasksBelongingToAnimal(Long animalId);

    /**
     * Assign Employee to existing Task without assignment
     * @param taskId Id of the task you want to assign the employee to
     * @param assignedEmployee employee you want to assign
     * */
    void updateTask(Long taskId, Employee assignedEmployee);

    /**
     * Get all tasks belonging to one animal
     * @param animalId is the id of the animal
     * @return list of animalTasks
     */
    List<AnimalTask> getAllTasksOfAnimal(Long animalId);

    /**
     * Delete a task
     * @param taskId of the Task that will be deleted
     */
    void deleteTask(Long taskId);

    /**
     * Get All AnimalTasks Of an Employee
     * @param employeeUsername is the username of the employee
     * @return list of animalTasks
     */
    List<AnimalTask> getAllAnimalTasksOfEmployee(String employeeUsername);

    /**
     * Marks task as done
     * @param taskId task id of task to mark as done
     */
    void markTaskAsDone(Long taskId);

    /**
     * Checks if an employee is the one performing the task
     * @param taskId id of task to check
     * @param employeeUsername username of employee to check
     */
    boolean isTaskPerformer(String employeeUsername, Long taskId);

    /**
     * Get All EnclosureTasks Of an Employee
     * @param employeeUsername is the username of the employee
     * @return list of enclosureTasks
     */
    List<EnclosureTask> getAllEnclosureTasksOfEmployee(String employeeUsername);

    /**
     * Get all tasks belonging to one enclosure
     * @param enclosureId is the id of the enclosure
     * @return list of enclosureTasks
     */
    List<EnclosureTask> getAllTasksOfEnclosure(Long enclosureId);

    /**
     * Creates a set amount of tasks
     *
     * @param task template of the tasks that will be created, Start- and Endtime for the first task
     * @param animal the tasks will be assigned to
     * @param amount of tasks that wil be created
     * @param separation which time-frame will be between the tasks
     * @param separationCount how many of the specified time frame will be between the tasks
     * @return List of AnimalTasks that are created
     */
    List<AnimalTask> createRepeatableAnimalTask(Task task, Animal animal, int amount, ChronoUnit separation, int separationCount);

    /**
     * Creates a set amount of tasks
     *
     * @param task template of the tasks that will be created, Start- and Endtime for the first task
     * @param enclosure the tasks will be assigned to
     * @param amount of tasks that wil be created
     * @param separation which time-frame will be between the tasks
     * @param separationCount how many of the specified time frame will be between the tasks
     * @return List of AnimalTasks that are created
     */
    List<EnclosureTask> createRepeatableEnclosureTask(Task task, Enclosure enclosure, int amount, ChronoUnit separation, int separationCount);
}
