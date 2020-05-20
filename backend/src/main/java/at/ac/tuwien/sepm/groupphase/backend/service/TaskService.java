package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.Animal;
import at.ac.tuwien.sepm.groupphase.backend.entity.AnimalTask;
import at.ac.tuwien.sepm.groupphase.backend.entity.Task;

public interface TaskService {

    /**
     * Used for creating new AnimalTasks
     * Also creates ne entry in Task Table
     * @param task to be created
     * @param animal animal the task is assigned to
     * */
    AnimalTask createAnimalTask(Task task, Animal animal);

}
