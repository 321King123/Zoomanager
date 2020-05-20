package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.Animal;
import at.ac.tuwien.sepm.groupphase.backend.entity.AnimalTask;
import at.ac.tuwien.sepm.groupphase.backend.entity.Employee;
import at.ac.tuwien.sepm.groupphase.backend.entity.Task;
import at.ac.tuwien.sepm.groupphase.backend.exception.IncorrectTypeException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFreeException;
import at.ac.tuwien.sepm.groupphase.backend.repository.AnimalTaskRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.TaskRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.EmployeeService;
import at.ac.tuwien.sepm.groupphase.backend.service.TaskService;
import at.ac.tuwien.sepm.groupphase.backend.types.EmployeeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;

@Service
public class CustomTaskService implements TaskService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final TaskRepository taskRepository;

    private final EmployeeService employeeService;

    private final AnimalTaskRepository animalTaskRepository;

    @Autowired
    public CustomTaskService(TaskRepository taskRepository, AnimalTaskRepository animalTaskRepository, EmployeeService employeeService) {
        this.taskRepository = taskRepository;
        this.animalTaskRepository = animalTaskRepository;
        this.employeeService = employeeService;
    }

    @Override
    public AnimalTask createAnimalTask(Task task, Animal animal) {
        LOGGER.debug("Creating new Animal Task");
        Employee employee = task.getAssignedEmployee();
        if(employee.getType() == EmployeeType.JANITOR){
            throw new IncorrectTypeException("A Janitor cant complete an animal Task");
        }
        if(!employeeService.employeeIsFreeBetweenStartingAndEndtime(employee, task)){
            throw new NotFreeException("Employee already works on a task in the given time");
        }
        taskRepository.save(task);
        //TODO: check maybe need to add task to builder
        animalTaskRepository.save(AnimalTask.builder().id(task.getId()).subject(animal).build());
        return null;
    }
}
