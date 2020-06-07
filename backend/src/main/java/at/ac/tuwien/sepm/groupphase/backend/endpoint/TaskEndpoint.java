package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.*;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.*;
import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotAuthorisedException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.service.AnimalService;
import at.ac.tuwien.sepm.groupphase.backend.service.EmployeeService;
import at.ac.tuwien.sepm.groupphase.backend.service.EnclosureService;
import at.ac.tuwien.sepm.groupphase.backend.service.TaskService;
import at.ac.tuwien.sepm.groupphase.backend.types.EmployeeType;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.websocket.server.PathParam;
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
    private final EnclosureTaskMapper enclosureTaskMapper;
    private final AnimalService animalService;
    private final TaskService taskService;
    private final EmployeeService employeeService;
    private final EnclosureService enclosureService;
    private final CombinedTaskMapper combinedTaskMapper;

    @Autowired
    public TaskEndpoint(TaskMapper taskMapper, TaskService taskService, EmployeeService employeeService,
                        AnimalService animalService, AnimalTaskMapper animalTaskMapper, EnclosureService enclosureService,
                        EnclosureTaskMapper enclosureTaskMapper, EmployeeMapper employeeMapper, CombinedTaskMapper combinedTaskMapper){
        this.combinedTaskMapper = combinedTaskMapper;
        this.employeeMapper = employeeMapper;
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

            if(employeeService.isAssignedToAnimal(username, animalId)) {
                return animalTaskMapper.animalTaskToAnimalTaskDto(taskService.createAnimalTask(task, animal));
            }else {
                throw new NotAuthorisedException("You cant assign Tasks to Animals that are not assigned to you");
            }

        }
    }

    /**
     * Method to assign an animal Task to a doctor automatically
     * If its a priority tasks soonest possible time is found otherwise it will be assigned to the least busy worker that has time
     * Requirements for assignment: Person that assigns is either an administrator or is assigned to the animal
     * @param id contains the information of the task including the username of the employee it is assigned to and Animal
     */
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "auto/animal/doctor/{id}")
    @ApiOperation(value = "Automatically assign Animal Task to Doctor", authorizations = {@Authorization(value = "apiKey")})
    public void autoAssignAnimalTaskDoctor(@PathVariable Long id, Authentication authentication) {
        LOGGER.info("POST /api/v1/tasks/auto/animal/doctor/{}", id);

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        boolean isAdmin = authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
        if(isAdmin){
            taskService.automaticallyAssignAnimalTask(id, EmployeeType.DOCTOR);
        }else{
            String username = (String)authentication.getPrincipal();

            if(employeeService.hasTaskAssignmentPermissions(username,id)) {
                taskService.automaticallyAssignAnimalTask(id, EmployeeType.DOCTOR);
            }else {
                throw new NotAuthorisedException("You cant assign Tasks to Animals that are not assigned to you");
            }

        }
    }

    /**
     * Method to assign an animal Task to a caretaker automatically
     * If its a priority tasks soonest possible time is found otherwise it will be assigned to the least busy worker that has time
     * Requirements for assignment: Person that assigns is either an administrator or is assigned to the animal
     * @param id of animal task to be automaticly assigned
     */
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "auto/animal/caretaker/{id}")
    @ApiOperation(value = "Automatically assign Animal Task to Animal Caretaker", authorizations = {@Authorization(value = "apiKey")})
    public void autoAssignAnimalTaskCaretaker(@PathVariable Long id, Authentication authentication) {
        LOGGER.info("POST /api/v1/tasks/auto/animal/caretaker/{}",id);

        //Only Admin and Employees that are assigned to the animal can create it
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        boolean isAdmin = authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
        if(isAdmin){
            taskService.automaticallyAssignAnimalTask(id, EmployeeType.ANIMAL_CARE);
        }else{
            String username = (String)authentication.getPrincipal();

            if(employeeService.hasTaskAssignmentPermissions(username,id)) {
                taskService.automaticallyAssignAnimalTask(id, EmployeeType.ANIMAL_CARE);
            }else {
                throw new NotAuthorisedException("You cant assign Tasks to Animals that are not assigned to you");
            }

        }
    }

    /**
     * Method to assign a enclosure Task to a caretaker automatically
     * If its a priority tasks soonest possible time is found otherwise it will be assigned to the least busy worker that has time
     * Requirements for assignment: Person that assigns is either an administrator or is assigned to the enclosure
     * @param id contains the information of the task including the username of the employee it is assigned to and Animal
     */
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "auto/enclosure/caretaker/{id}")
    @ApiOperation(value = "Automatically assign Enclosure Task to Animal Caretaker", authorizations = {@Authorization(value = "apiKey")})
    public void autoAssignEnclosureTaskCaretaker(@PathVariable Long id, Authentication authentication) {
        LOGGER.info("POST /api/v1/tasks/auto/enclosure/{}", id);

        //Only Admin and Employees that are assigned to the enclosure can create it
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        boolean isAdmin = authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
        if(isAdmin){
            taskService.automaticallyAssignEnclosureTask(id, EmployeeType.ANIMAL_CARE);
        }else{
            String username = (String)authentication.getPrincipal();

            if(employeeService.hasTaskAssignmentPermissions(username,id)) {
                taskService.automaticallyAssignEnclosureTask(id, EmployeeType.ANIMAL_CARE);
            }else {
                throw new NotAuthorisedException("You cant assign Tasks to Enclosures that are not assigned to you");
            }

        }
    }

    /**
     * Method to assign a enclosure Task to a janitor automatically
     * If its a priority tasks soonest possible time is found otherwise it will be assigned to the least busy worker that has time
     * Requirements for assignment: Person that assigns is either an administrator or is assigned to the enclosure
     * @param id contains the information of the task including the username of the employee it is assigned to and Animal
     */
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "auto/enclosure/janitor/{id}")
    @ApiOperation(value = "Automatically assign Enclosure Task to Janitor", authorizations = {@Authorization(value = "apiKey")})
    public void autoAssignEnclosureTaskJanitor(@PathVariable Long id, Authentication authentication) {
        LOGGER.info("POST /api/v1/tasks/auto/enclosure/janitor/{}", id);

        //Only Admin and Employees that are assigned to the enclosure can create it
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        boolean isAdmin = authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
        if(isAdmin){
            taskService.automaticallyAssignEnclosureTask(id, EmployeeType.JANITOR);
        }else{
            String username = (String)authentication.getPrincipal();

            if(employeeService.hasTaskAssignmentPermissions(username,id)) {
                taskService.automaticallyAssignEnclosureTask(id, EmployeeType.JANITOR);
            }else {
                throw new NotAuthorisedException("You cant assign Tasks to Enclosures that are not assigned to you");
            }

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

            if(employeeService.isAssignedToEnclosure(username, enclosureId)) {

                return enclosureTaskMapper.enclosureTaskToEclosureTaskDto(taskService.createEnclosureTask(task,enclosure));
            }else {
                //if no animal with transmitted Id is assigned to User
                throw new NotAuthorisedException("You cant assign Tasks to Enclosures that are not assigned to you");
            }
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
    public List<CombinedTaskDto> getAllAnimalTasksBelongingToAnimal(@PathVariable Long animalId, Authentication authentication){
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
        return combinedTaskMapper.animalTaskListToCombinedTaskDtoList(animalTasks);
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

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping(value = "/repeatable/{taskId}")
    @ApiOperation(value = "delete Task and future instances", authorizations = {@Authorization(value = "apiKey")})
    public void repeatDeleteTask(@PathVariable Long taskId, Authentication authentication) {
        LOGGER.info("DELETE /api/v1/tasks/repeatable/{}", taskId);

        //Only Admin and Employees that are assigned to the subject can delete it
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        boolean isAdmin = authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
        if(isAdmin){
            taskService.repeatDeleteTask(taskId);
        } else {
            String username = (String)authentication.getPrincipal();

            if(employeeService.hasTaskAssignmentPermissions(username, taskId)) {
                taskService.repeatDeleteTask(taskId);
            } else {
                throw new NotAuthorisedException("You cant delete Tasks of Animals or Enclosures that are not assigned to you");
            }
        }
    }



    @Secured("ROLE_USER")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/employee/animal-task/{employeeUsername}")
    @ApiOperation(value = "Get list of animal tasks belonging to an employee", authorizations = {@Authorization(value = "apiKey")})
    public List<AnimalTaskDto> getAllAnimalTasksBelongingToEmployee(@PathVariable String employeeUsername, Authentication authentication){
        LOGGER.info("GET /api/v1/tasks/employee/{}", employeeUsername);
        ValidateViewEmployeeInfoPermission(employeeUsername, authentication);
        List<AnimalTask> animalTasks = new LinkedList<>(taskService.getAllAnimalTasksOfEmployee(employeeUsername));
        List<AnimalTaskDto> animalTaskDtoList = new LinkedList<>();
        for(AnimalTask a: animalTasks){
            animalTaskDtoList.add(animalTaskMapper.animalTaskToAnimalTaskDto(a));
        }
        return animalTaskDtoList;
    }

    @Secured("ROLE_USER")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/enclosure/{enclosureId}")
    @ApiOperation(value = "Get list of enclosure tasks belonging to an enclosure", authorizations = {@Authorization(value = "apiKey")})
    public List<CombinedTaskDto> getAllEnclosureTasksBelongingToEnclosure(@PathVariable Long enclosureId, Authentication authentication){
        LOGGER.info("GET /api/v1/tasks/enclosure/ {}", enclosureId);
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        boolean isAdmin = authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
        String username = (String) authentication.getPrincipal();
        if(!isAdmin){
            if(!employeeService.isAssignedToEnclosure(username,enclosureId)){
                throw new NotAuthorisedException("You are not allowed to see this enclosures information.");
            }
        }
        List<EnclosureTask> enclosureTasks = new LinkedList<>(taskService.getAllTasksOfEnclosure(enclosureId));
        return combinedTaskMapper.enclosureTaskListToCombinedTaskDtoList(enclosureTasks);
    }

    @Secured("ROLE_USER")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/employee/enclosure-task/{employeeUsername}")
    @ApiOperation(value = "Get list of enclosure tasks belonging to an employee", authorizations = {@Authorization(value = "apiKey")})
    public List<EnclosureTaskDto> getAllEnclosureTasksBelongingToEmployee(@PathVariable String employeeUsername, Authentication authentication){
        LOGGER.info("GET /api/v1/tasks/employee/enclosure-task/{}", employeeUsername);
        ValidateViewEmployeeInfoPermission(employeeUsername, authentication);
        List<EnclosureTask> enclosureTasks = new LinkedList<>(taskService.getAllEnclosureTasksOfEmployee(employeeUsername));
        return mapEnclouresTaskListToEnclosureTaskDtosList(enclosureTasks);
    }

    @Secured("ROLE_USER")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/employee/{employeeUsername}")
    @ApiOperation(value = "Get list of enclosure tasks belonging to an employee", authorizations = {@Authorization(value = "apiKey")})
    public List<CombinedTaskDto> getAllTasksBelongingToEmployee(@PathVariable String employeeUsername, Authentication authentication){
        LOGGER.info("GET /api/v1/tasks/employee/{}", employeeUsername);
        ValidateViewEmployeeInfoPermission(employeeUsername, authentication);
        List<EnclosureTask> enclosureTasks = new LinkedList<>(taskService.getAllEnclosureTasksOfEmployee(employeeUsername));
        List<AnimalTask> animalTasks = new LinkedList<>(taskService.getAllAnimalTasksOfEmployee(employeeUsername));
        return combinedTaskMapper.sortedEnclosureTaskListAndAnimalTaskListToSortedCombinedTaskDtoList(enclosureTasks, animalTasks);
    }



    private void ValidateViewEmployeeInfoPermission(String employeeUsername, Authentication authentication) {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        boolean isAdmin = authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
        String username = (String) authentication.getPrincipal();
        if(!isAdmin){
            if(!username.equals(employeeUsername)){
                throw new NotAuthorisedException("You are not allowed to see this employees information.");
            }
        }
    }

    private List<EnclosureTaskDto> mapEnclouresTaskListToEnclosureTaskDtosList(List<EnclosureTask> enclosureTasks) {
        List<EnclosureTaskDto> enclosureTaskDtos = new LinkedList<>();
        for(EnclosureTask e: enclosureTasks){
            enclosureTaskDtos.add(enclosureTaskMapper.enclosureTaskToEclosureTaskDto(e));
        }
        return enclosureTaskDtos;
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

    @Secured("ROLE_USER")
    @ResponseStatus(HttpStatus.CREATED)
    @PutMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void updateTask(@Valid @RequestBody CombinedTaskDto combinedTaskDto, Authentication authentication){
        LOGGER.info("PUT /api/v1/tasks/update body: {}",combinedTaskDto);
        //authorisation check
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        boolean isAdmin = authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
        String username = (String) authentication.getPrincipal();
        if(!isAdmin){
            if(!employeeService.hasTaskAssignmentPermissions(username,combinedTaskDto.getId())){
                throw new NotAuthorisedException("You are not authorized to update this task.");
            }
        }
        boolean animalTask = false;
        // checking if the task is an animal or an enclosure task
        // postman testing with isAnimalTask=true doesnt work, the api does not recognize the "true" value
        taskService.getTaskById(combinedTaskDto.getId()); //checking if the task exists
        try{
            taskService.getAnimalTaskById(combinedTaskDto.getId());
            animalTask=true;
        }catch (NotFoundException e){}

        if(animalTask)  taskService.updateFullAnimalTaskInformation(combinedTaskMapper.combinedTaskDtoToAnimalTask(combinedTaskDto));
        else taskService.updateFullEnclosureTaskInformation(combinedTaskMapper.combinedTaskDtoToEnclosureTask(combinedTaskDto));



    }

    @Secured("ROLE_USER")
    @ResponseStatus(HttpStatus.CREATED)
    @PutMapping(value = "/update/repeat", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void repeatUpdateTask(@Validated(CombinedTaskDto.ValidRepeatUpdate.class) @RequestBody CombinedTaskDto combinedTaskDto, Authentication authentication){
        LOGGER.info("PUT /api/v1/tasks/update/repeat body: {}",combinedTaskDto);
        //authorisation check
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        boolean isAdmin = authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
        String username = (String) authentication.getPrincipal();
        if(!isAdmin){
            if(!employeeService.hasTaskAssignmentPermissions(username,combinedTaskDto.getId())){
                throw new NotAuthorisedException("You are not authorized to update this task.");
            }
        }
        boolean animalTask = false;
        // checking if the task is an animal or an enclosure task
        // postman testing with isAnimalTask=true doesnt work, the api does not recognize the "true" value
        taskService.getTaskById(combinedTaskDto.getId()); //checking if the task exists
        try{
            taskService.getAnimalTaskById(combinedTaskDto.getId());
            animalTask=true;
        }catch (NotFoundException ignored){}

        if(animalTask)  taskService.repeatUpdateAnimalTaskInformation(combinedTaskMapper.combinedTaskDtoToAnimalTask(combinedTaskDto));
        else taskService.repeatUpdateEnclosureTaskInformation(combinedTaskMapper.combinedTaskDtoToEnclosureTask(combinedTaskDto));
    }

    /**
     * Creates a specified number of tasks with a certain duration between them
     * @param repeatableTaskDto contains the information of the task including the username of the employee it is assigned to and
     *                          how many tasks should be created in what time intervals
     * @param animalId identifies the animal the task is assigned to
     * @return the AnimalTaskDto Object that occurs first, containing info about animal, task and employee
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/animal/repeatable/{animalId}")
    @ApiOperation(value = "Create new repeatable Animal Task", authorizations = {@Authorization(value = "apiKey")})
    public AnimalTaskDto createRepeatableAnimalTask(@Valid @RequestBody RepeatableTaskDto repeatableTaskDto, @PathVariable Long animalId, Authentication authentication) {
        LOGGER.info("POST /api/v1/tasks/animal/repeatable/{} body: {}", animalId, repeatableTaskDto);

        Task task = taskMapper.repeatableTaskDtoToTask(repeatableTaskDto);

        //get Objects for Method call
        task.setAssignedEmployee(employeeService.findByUsername(repeatableTaskDto.getAssignedEmployeeUsername()));
        Animal animal = animalService.findAnimalById(animalId);

        //Only Admin and Employees that are assigned to the animal can create it
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        boolean isAdmin = authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
        if(isAdmin){
            List<AnimalTask> animalTasks = taskService.createRepeatableAnimalTask(task, animal,
                repeatableTaskDto.getAmount(), repeatableTaskDto.getSeparation(), repeatableTaskDto.getSeparationAmount());

            return animalTaskMapper.animalTaskToAnimalTaskDto(animalTasks.get(animalTasks.size() - 1));
        }else{
            String username = (String)authentication.getPrincipal();

            if(employeeService.isAssignedToAnimal(username, animalId)) {
                List<AnimalTask> animalTasks = taskService.createRepeatableAnimalTask(task, animal,
                    repeatableTaskDto.getAmount(), repeatableTaskDto.getSeparation(), repeatableTaskDto.getSeparationAmount());

                return animalTaskMapper.animalTaskToAnimalTaskDto(animalTasks.get(animalTasks.size() - 1));
            }else {
                throw new NotAuthorisedException("You cant assign Tasks to Animals that are not assigned to you");
            }

        }
    }

    /**
     * Creates a specified number of tasks with a certain duration between them
     * @param repeatableTaskDto contains the information of the task including the username of the employee it is assigned to and
     *                          how many tasks should be created in what time intervals
     * @param enclosureId identifies the animal the task is assigned to
     * @return the AnimalTaskDto Object that occurs first, containing info about animal, task and employee
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/enclosure/repeatable/{enclosureId}")
    @ApiOperation(value = "Create new repeatable Enclosure Task", authorizations = {@Authorization(value = "apiKey")})
    public EnclosureTaskDto createRepeatableEnclosureTask(@Valid @RequestBody RepeatableTaskDto repeatableTaskDto, @PathVariable Long enclosureId, Authentication authentication) {
        LOGGER.info("POST /api/v1/tasks/enclosure/repeatable/{} body: {}", enclosureId, repeatableTaskDto);

        Task task = taskMapper.repeatableTaskDtoToTask(repeatableTaskDto);

        //get Objects for Method call
        task.setAssignedEmployee(employeeService.findByUsername(repeatableTaskDto.getAssignedEmployeeUsername()));
        Enclosure enclosure = enclosureService.findById(enclosureId);

        //Only Admin and Employees that are assigned to the enclosure can create it
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        boolean isAdmin = authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
        if(isAdmin){
            List<EnclosureTask> enclosureTasks = taskService.createRepeatableEnclosureTask(task, enclosure,
                repeatableTaskDto.getAmount(), repeatableTaskDto.getSeparation(), repeatableTaskDto.getSeparationAmount());

            return enclosureTaskMapper.enclosureTaskToEclosureTaskDto(enclosureTasks.get(enclosureTasks.size() - 1));
        }else{
            String username = (String)authentication.getPrincipal();

            if(employeeService.isAssignedToEnclosure(username, enclosureId)) {
                List<EnclosureTask> enclosureTasks = taskService.createRepeatableEnclosureTask(task, enclosure,
                    repeatableTaskDto.getAmount(), repeatableTaskDto.getSeparation(), repeatableTaskDto.getSeparationAmount());

                return enclosureTaskMapper.enclosureTaskToEclosureTaskDto(enclosureTasks.get(enclosureTasks.size() - 1));
            }else {
                //if no animal with transmitted Id is assigned to User
                throw new NotAuthorisedException("You cant assign Tasks to Enclosures that are not assigned to you");
            }
        }
    }
}
