package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.AnimalTaskDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.EnclosureTaskDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.TaskDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.AnimalMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.AnimalTaskMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.EnclosureTaskMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.TaskMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotAuthorisedException;
import at.ac.tuwien.sepm.groupphase.backend.service.AnimalService;
import at.ac.tuwien.sepm.groupphase.backend.service.EmployeeService;
import at.ac.tuwien.sepm.groupphase.backend.service.EnclosureService;
import at.ac.tuwien.sepm.groupphase.backend.service.TaskService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.lang.invoke.MethodHandles;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/api/v1/tasks")
public class TaskEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final TaskMapper taskMapper;
    private final AnimalTaskMapper animalTaskMapper;
    private final EnclosureTaskMapper enclosureTaskMapper;
    private final AnimalService animalService;
    private final TaskService taskService;
    private final EmployeeService employeeService;
    private final EnclosureService enclosureService;

    @Autowired
    public TaskEndpoint(EnclosureService enclosureService, TaskMapper taskMapper, EnclosureTaskMapper enclosureTaskMapper, TaskService taskService, EmployeeService employeeService, AnimalService animalService, AnimalTaskMapper animalTaskMapper){
        this.animalTaskMapper = animalTaskMapper;
        this.animalService = animalService;
        this.employeeService = employeeService;
        this.taskMapper = taskMapper;
        this.taskService = taskService;
        this.enclosureTaskMapper = enclosureTaskMapper;
        this.enclosureService= enclosureService;
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
        LOGGER.info("POST /api/v1/tasks body: {}", taskDto);

        Task task = taskMapper.taskDtoToTask(taskDto);

        //set Employee from transmitted Username
        task.setAssignedEmployee(employeeService.findByUsername(taskDto.getAssignedEmployeeUsername()));

        //find animal transmitted in Path
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

            //if no animal with transmitted Id is assigned to User
            throw new NotAuthorisedException("You cant assign Tasks to Animals that are not assigned to you");
        }
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/enclosure/{enclosureId}")
    @ApiOperation(value = "Create new Enclosure Task", authorizations = {@Authorization(value = "apiKey")})
    public EnclosureTaskDto createEnclosureTask(@Valid @RequestBody TaskDto taskDto, @PathVariable Long enclosureId, Authentication authentication) {
        LOGGER.info("POST /api/v1/tasksEnc body: {}", taskDto);

        Task task = taskMapper.taskDtoToTask(taskDto);

        //set Employee from transmitted Username
        task.setAssignedEmployee(employeeService.findByUsername(taskDto.getAssignedEmployeeUsername()));

        //find enclosure transmitted in Path
        Enclosure enclosure = enclosureService.findById(enclosureId);

        //Only Admin and Employees that are assigned to the enclosure can create it
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        boolean isAdmin = authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
        if(isAdmin){

            return enclosureTaskMapper.enclosureTaskToEclosureTaskDto(taskService.createEnclosureTask(task,enclosure));
        }else{
            String username = (String)authentication.getPrincipal();

            if(employeeService.findAssignedEnclosures(username).contains(enclosure)) {

                return enclosureTaskMapper.enclosureTaskToEclosureTaskDto(taskService.createEnclosureTask(task,enclosure));
            }
            //if no animal with transmitted Id is assigned to User
            throw new NotAuthorisedException("You cant assign Tasks to Enclosures that are not assigned to you");
        }
    }
}
