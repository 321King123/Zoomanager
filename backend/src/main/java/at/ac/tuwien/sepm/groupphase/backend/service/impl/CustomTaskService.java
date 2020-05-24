package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.Animal;
import at.ac.tuwien.sepm.groupphase.backend.entity.AnimalTask;
import at.ac.tuwien.sepm.groupphase.backend.entity.Employee;
import at.ac.tuwien.sepm.groupphase.backend.entity.Task;
import at.ac.tuwien.sepm.groupphase.backend.exception.*;
import at.ac.tuwien.sepm.groupphase.backend.repository.AnimalTaskRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.TaskRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.EmployeeService;
import at.ac.tuwien.sepm.groupphase.backend.service.TaskService;
import at.ac.tuwien.sepm.groupphase.backend.types.EmployeeType;
import at.ac.tuwien.sepm.groupphase.backend.types.TaskStatus;
import org.aspectj.weaver.ast.Not;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.ValidationException;
import java.lang.invoke.MethodHandles;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

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

        if(animal == null)
            throw new NotFoundException("Could not find animal with given Id");

        if(task.getStartTime().isAfter(task.getEndTime()))
            throw new ValidationException("Starting time of task cant be later than end time");

        if(employee == null) {
            task.setStatus(TaskStatus.NOT_ASSIGNED);
        }else if(employee.getType() == EmployeeType.JANITOR){
            throw new IncorrectTypeException("A Janitor cant complete an animal Task");
        }else{
            if(employee.getType() == EmployeeType.ANIMAL_CARE && !employeeService.isAssignedToAnimal(employee.getUsername(), animal.getId())){
                throw new NotAuthorisedException("You cant assign an animal caretaker that is not assigned to the animal.");
            }
            task.setStatus(TaskStatus.ASSIGNED);
        }


        if(task.getStatus() == TaskStatus.ASSIGNED && !employeeService.employeeIsFreeBetweenStartingAndEndtime(employee, task)){
            throw new NotFreeException("Employee already works on a task in the given time");
        }

        Task createdTask = taskRepository.save(task);
        AnimalTask animalTask = animalTaskRepository.save(AnimalTask.builder().id(createdTask.getId()).subject(animal).build());
        animalTask.setTask(createdTask);
        animalTask.setSubject(animal);
        return animalTask;
    }

    @Override
    public void deleteAnimalTasksBelongingToAnimal(Long animalId) {
        LOGGER.debug("Deleting Animal Task of Animal with Id " + animalId);
        List<AnimalTask> assignedAnimalTasks = animalTaskRepository.findAllBySubject_Id(animalId);
        animalTaskRepository.deleteAll(assignedAnimalTasks);
    }

    @Override
    public void updateTask(Long taskId, Employee assignedEmployee) {
        LOGGER.debug("Assigning Task with id {} to employee with username {}", taskId, assignedEmployee.getUsername());
        Task foundTask = getTaskById(taskId);
        if(foundTask.getStatus() != TaskStatus.NOT_ASSIGNED){
            throw new IncorrectTypeException("Only currently unassigned Tasks can be assigned to an Employee");
        }
        Employee employee = employeeService.findByUsername(assignedEmployee.getUsername());
        if(employeeService.canBeAssignedToTask(employee, foundTask)){
            foundTask.setAssignedEmployee(employee);
            foundTask.setStatus(TaskStatus.ASSIGNED);
            taskRepository.save(foundTask);
        }else{
            throw new IncorrectTypeException("Employee does not fulfill assignment criteria");
        }
    }

    public List<AnimalTask> getAllTasksOfAnimal(Long animalId){
        LOGGER.debug("Get All Tasks belonging to Animal with id: {}", animalId);
        return animalTaskRepository.findAllBySubject_Id(animalId);
    }

    @Override
    public void deleteTask(Long taskId) {
        LOGGER.debug("Deleting Task with id {}", taskId);
        Task foundTask = getTaskById(taskId);
        AnimalTask foundAnimalTask = getAnimalTaskById(taskId);
        animalTaskRepository.delete(foundAnimalTask);
        taskRepository.delete(foundTask);
    }

    @Override
    public List<AnimalTask> getAllAnimalTasksOfEmployee(String employeeUsername) {
        LOGGER.debug("Get All Tasks belonging to employee with username: {}", employeeUsername);
        Employee employee = employeeService.findByUsername(employeeUsername);
        if(employee == null)
            throw new NotFoundException("Could not find Employee with given Username");
        List<Task> taskList = new LinkedList<>(taskRepository.findAllByAssignedEmployee(employee));
        List<AnimalTask> animalTaskList = new LinkedList<>();
        for(Task t:taskList){
            Optional<AnimalTask> animalTask = animalTaskRepository.findById(t.getId());
            animalTask.ifPresent(animalTaskList::add);
        }
        return animalTaskList;
    }

    @Override
    public void markTaskAsDone(Long taskId) {
        LOGGER.debug("Marking task with id {} as done", taskId);
        Task foundTask = getTaskById(taskId);
        foundTask.setStatus(TaskStatus.DONE);
        taskRepository.save(foundTask);
    }

    @Override
    public boolean isTaskPerformer(String employeeUsername, Long taskId) {
        LOGGER.debug("Check if employee with username {} is performing task with id {}", employeeUsername, taskId);
        Task task = getTaskById(taskId);
        return task.getAssignedEmployee().getUsername().equals(employeeUsername);
    }

    private Task getTaskById(Long taskId){
        LOGGER.debug("Find task with id {}", taskId);
        Optional<Task> task = taskRepository.findById(taskId);
        Task foundTask;
        if(task.isPresent()){
            foundTask = task.get();
        }else{
            throw new NotFoundException("Could not find Task with given Id");
        }
        return foundTask;
    }

    private AnimalTask getAnimalTaskById(Long animalTaskId){
        LOGGER.debug("Find animal task with id {}", animalTaskId);
        Optional<AnimalTask> animalTask = animalTaskRepository.findById(animalTaskId);
        AnimalTask foundTask;
        if(animalTask.isPresent()){
            foundTask = animalTask.get();
        }else{
            throw new NotFoundException("Could not find Task with given Id");
        }
        return foundTask;
    }
}
