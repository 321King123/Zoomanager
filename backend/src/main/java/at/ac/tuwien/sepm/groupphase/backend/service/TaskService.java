package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.*;

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

}
