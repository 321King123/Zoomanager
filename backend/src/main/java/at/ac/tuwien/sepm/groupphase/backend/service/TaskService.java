package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.Animal;
import at.ac.tuwien.sepm.groupphase.backend.entity.AnimalTask;
import at.ac.tuwien.sepm.groupphase.backend.entity.Employee;
import at.ac.tuwien.sepm.groupphase.backend.entity.Task;

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
     * Get All AnimalTasks Of an Employee
     * @param employeeUsername is the username of the employee
     * @return list of animalTasks
     */
    List<AnimalTask> getAllAnimalTasksOfEmployee(String employeeUsername);
}
