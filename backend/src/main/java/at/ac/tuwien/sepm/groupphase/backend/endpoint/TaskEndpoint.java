package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.AnimalTaskDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.TaskDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.AnimalMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.AnimalTaskMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.TaskMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Animal;
import at.ac.tuwien.sepm.groupphase.backend.entity.AnimalTask;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.lang.invoke.MethodHandles;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/tasks")
public class TaskEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final TaskMapper taskMapper;
    private final AnimalTaskMapper animalTaskMapper;
    private final AnimalMapper animalMapper;
    private final AnimalService animalService;
    private final TaskService taskService;
    private final EmployeeService employeeService;

    @Autowired
    public TaskEndpoint(TaskMapper taskMapper, TaskService taskService, EmployeeService employeeService, AnimalService animalService, AnimalTaskMapper animalTaskMapper, AnimalMapper animalMapper){
        this.animalMapper = animalMapper;
        this.animalTaskMapper = animalTaskMapper;
        this.animalService = animalService;
        this.employeeService = employeeService;
        this.taskMapper = taskMapper;
        this.taskService = taskService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/animal/{animalId}")
    @ApiOperation(value = "Create new Animal Task", authorizations = {@Authorization(value = "apiKey")})
    public AnimalTaskDto create(@Valid @RequestBody TaskDto taskDto, @PathVariable Long animalId, Authentication authentication) {
        LOGGER.info("POST /api/v1/tasks body: {}", taskDto);
        Task task = taskMapper.taskDtoToTask(taskDto);
        task.setAssignedEmployee(employeeService.findByUsername(taskDto.getAssignedEmployeeUsername()));
        Animal animal = animalService.getById(animalId);
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        boolean isAdmin = authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
        if(isAdmin){
            return animalTaskMapper.animalTaskToAnimalTaskDto(taskService.createAnimalTask(task, animal));
        }else{
            String username = (String)authentication.getPrincipal();
            List<Animal> assignedAnimals = employeeService.findAssignedAnimals(username);
            for(Animal a: assignedAnimals){
                if(a.getId().equals(animal.getId())) {
                    AnimalTask animalTask = taskService.createAnimalTask(task, animal);
                    return animalTaskMapper.animalTaskToAnimalTaskDto(taskService.createAnimalTask(task, animal));
                }
            }
            throw new NotAuthorisedException("You cant assign Tasks to Animals that are not assigned to you");
        }
    }
}
