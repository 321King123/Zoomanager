package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.AnimalDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.AnimalTaskDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.EmployeeDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.TaskDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.AnimalMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.AnimalTaskMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.EmployeeMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.TaskMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Animal;
import at.ac.tuwien.sepm.groupphase.backend.entity.AnimalTask;
import at.ac.tuwien.sepm.groupphase.backend.entity.Employee;
import at.ac.tuwien.sepm.groupphase.backend.entity.Task;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotAuthorisedException;
import at.ac.tuwien.sepm.groupphase.backend.service.AnimalService;
import at.ac.tuwien.sepm.groupphase.backend.service.EmployeeService;
import at.ac.tuwien.sepm.groupphase.backend.service.TaskService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.lang.invoke.MethodHandles;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/api/v1/tasks")
public class TaskEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final TaskMapper taskMapper;
    private final AnimalTaskMapper animalTaskMapper;
    private final EmployeeMapper employeeMapper;
    private final AnimalService animalService;
    private final TaskService taskService;
    private final EmployeeService employeeService;

    @Autowired
    public TaskEndpoint(TaskMapper taskMapper, TaskService taskService, EmployeeService employeeService, AnimalService animalService, AnimalTaskMapper animalTaskMapper, EmployeeMapper employeeMapper){
        this.employeeMapper = employeeMapper;
        this.animalTaskMapper = animalTaskMapper;
        this.animalService = animalService;
        this.employeeService = employeeService;
        this.taskMapper = taskMapper;
        this.taskService = taskService;
    }


    /**
     * Post Method to assign a task to a worker/animal tuple
     * Requirements for assignment: Person that assigns is either an administrator or is assigned to the animal,
     * The employee assigned to the task must either be a Doctor or Animal Caretaker,
     * The employee must have no other tasks assigned between start and end time
     * @param taskDto contains the information of the task including the username of the employee it is assigned to
     * @param animalId identifies the animal the task is assigned to
     * @return an AnimalTaskDto Object that contains info about animal, task and employee
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/animal/{animalId}")
    @ApiOperation(value = "Create new Animal Task", authorizations = {@Authorization(value = "apiKey")})
    public AnimalTaskDto create(@Valid @RequestBody TaskDto taskDto, @PathVariable Long animalId, Authentication authentication) {
        LOGGER.info("POST /api/v1/tasks/animal/{} body: {}", animalId, taskDto);

        Task task = taskMapper.taskDtoToTask(taskDto);

        //get Objects for Method call
        task.setAssignedEmployee(employeeService.findByUsername(taskDto.getAssignedEmployeeUsername()));
        Animal animal = animalService.findAnimalById(animalId);

        //Only Admin and Employees that are assigned to the animal can create it
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        boolean isAdmin = authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
        if(isAdmin){
            return animalTaskMapper.animalTaskToAnimalTaskDto(taskService.createAnimalTask(task, animal));
        }else{
            String username = (String)authentication.getPrincipal();

            if(employeeService.isAssignedToAnimal(username, animalId))
                return animalTaskMapper.animalTaskToAnimalTaskDto(taskService.createAnimalTask(task, animal));

            throw new NotAuthorisedException("You cant assign Tasks to Animals that are not assigned to you");
        }
    }

    /**
     * Put Method to assign Employee to Task
     * Only Admin and Employees that are assigned to the animal/enclosure can assign other employees to a task
     * Only unassigned task can be assigned
     * @param employeeDto contains the employee you want to assign
     * @param taskId contains the taskId for the task you want to assign
     */
    @ResponseStatus(HttpStatus.OK)
    @PutMapping(value = "/{taskId}")
    @ApiOperation(value = "Assign Employee to Task", authorizations = {@Authorization(value = "apiKey")})
    public void assignEmployee(@RequestBody EmployeeDto employeeDto, @PathVariable Long taskId, Authentication authentication) {
        LOGGER.info("PUT /api/v1/tasks/{} body{}", taskId, employeeDto);

        Employee employee = employeeMapper.employeeDtoToEmployee(employeeDto);
        //Only Admin and Employees that are assigned to the animal can create it
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        boolean isAdmin = authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
        if(isAdmin){
            taskService.updateTask(taskId, employee);
        }else{
            String username = (String)authentication.getPrincipal();

            if(employeeService.hasTaskAssignmentPermissions(username, taskId)) {
                taskService.updateTask(taskId, employeeMapper.employeeDtoToEmployee(employeeDto));
            }else {
                throw new NotAuthorisedException("You cant assign Tasks to Animals that are not assigned to you");
            }

        }
    }

    @Secured("ROLE_USER")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/animal/{animalId}")
    @ApiOperation(value = "Get list of animal tasks belonging to an animal", authorizations = {@Authorization(value = "apiKey")})
    public List<AnimalTaskDto> getAllAnimalTasksBelongingToAnimal(@PathVariable Long animalId, Authentication authentication){
        LOGGER.info("GET /api/v1/tasks/animal/ {}", animalId);
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        boolean isAdmin = authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
        String username = (String) authentication.getPrincipal();
        if(!isAdmin){
            if(!employeeService.isAssignedToAnimal(username,animalId)){
                throw new NotAuthorisedException("You are not allowed to see this animals information.");
            }
        }
        List<AnimalTask> animalTasks = new LinkedList<>(taskService.getAllTasksOfAnimal(animalId));
        List<AnimalTaskDto> animalTaskDtoList = new LinkedList<>();
        for(AnimalTask a: animalTasks){
            animalTaskDtoList.add(animalTaskMapper.animalTaskToAnimalTaskDto(a));
        }
        return animalTaskDtoList;
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping(value = "/{taskId}")
    @ApiOperation(value = "Assign Employee to Task", authorizations = {@Authorization(value = "apiKey")})
    public void deleteTask(@PathVariable Long taskId, Authentication authentication) {
        LOGGER.info("DELETE /api/v1/tasks/{}", taskId);

        //Only Admin and Employees that are assigned to the animal can delete it
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        boolean isAdmin = authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
        if(isAdmin){
            taskService.deleteTask(taskId);
        } else {
            String username = (String)authentication.getPrincipal();

            if(employeeService.hasTaskAssignmentPermissions(username, taskId)) {
                taskService.deleteTask(taskId);
            } else {
                throw new NotAuthorisedException("You cant delete Tasks of Animals that are not assigned to you");
            }
        }
    }

    @Secured("ROLE_USER")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/employee/{employeeUsername}")
    @ApiOperation(value = "Get list of animal tasks belonging to an employee", authorizations = {@Authorization(value = "apiKey")})
    public List<AnimalTaskDto> getAllAnimalTasksBelongingToEmployee(@PathVariable String employeeUsername, Authentication authentication){
        LOGGER.info("GET /api/v1/tasks/employee/{}", employeeUsername);
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        boolean isAdmin = authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
        String username = (String) authentication.getPrincipal();
        if(!isAdmin){
            if(!username.equals(employeeUsername)){
                throw new NotAuthorisedException("You are not allowed to see this employees information.");
            }
        }
        List<AnimalTask> animalTasks = new LinkedList<>(taskService.getAllAnimalTasksOfEmployee(employeeUsername));
        List<AnimalTaskDto> animalTaskDtoList = new LinkedList<>();
        for(AnimalTask a: animalTasks){
            animalTaskDtoList.add(animalTaskMapper.animalTaskToAnimalTaskDto(a));
        }
        return animalTaskDtoList;
    }

    @Secured("ROLE_USER")
    @ResponseStatus(HttpStatus.OK)
    @PutMapping(value = "/finished/{taskId}")
    @ApiOperation(value = "Marking task as done", authorizations = {@Authorization(value = "apiKey")})
    public void markTaskAsDone(@PathVariable Long taskId, Authentication authentication){
        LOGGER.info("PUT /api/v1/tasks/finished/{}", taskId);
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        boolean isAdmin = authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
        String username = (String) authentication.getPrincipal();
        if(!isAdmin){
            if(!taskService.isTaskPerformer(username, taskId)){
                throw new NotAuthorisedException("You are not allowed to see this employees information.");
            }
        }
        taskService.markTaskAsDone(taskId);
    }
}
