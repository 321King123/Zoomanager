package at.ac.tuwien.sepm.groupphase.backend.integrationtest.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.*;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.AnimalMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.EmployeeMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import at.ac.tuwien.sepm.groupphase.backend.repository.*;
import at.ac.tuwien.sepm.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepm.groupphase.backend.types.TaskStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class TaskEndpointTest implements TestData {

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private AnimalTaskRepository animalTaskRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    //for testing assignment
    @Autowired
    private AnimalRepository animalRepository;

    @Autowired
    private UserLoginRepository userLoginRepository;

    @Autowired
    private EnclosureRepository enclosureRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EmployeeMapper employeeMapper;

    @Autowired
    private AnimalMapper animalMapper;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private SecurityProperties securityProperties;

    String ANIMAL_TASK_CREATION_BASE_URI = TASK_BASE_URI + "/animal";

    private final UserLogin admin_login = UserLogin.builder()
        .isAdmin(true)
        .username(ADMIN_USER)
        .password(passwordEncoder.encode(VALID_TEST_PASSWORD))
        .build();

    private final UserLogin animal_caretaker_login = UserLogin.builder()
        .isAdmin(false)
        .username(USERNAME_ANIMAL_CARE_EMPLOYEE)
        .password(passwordEncoder.encode(VALID_TEST_PASSWORD))
        .build();

    private final Employee anmial_caretaker = Employee.builder()
        .username(USERNAME_ANIMAL_CARE_EMPLOYEE)
        .name(NAME_ANIMAL_CARE_EMPLOYEE)
        .birthday(BIRTHDAY_ANIMAL_CARE_EMPLOYEE)
        .type(TYPE_ANIMAL_CARE_EMPLOYEE)
        .email(EMAIL_ANIMAL_CARE_EMPLOYEE)
        .build();

    private final UserLogin janitor_login = UserLogin.builder()
        .isAdmin(false)
        .username(USERNAME_JANITOR_EMPLOYEE)
        .password(passwordEncoder.encode(VALID_TEST_PASSWORD))
        .build();

    private final Employee janitor = Employee.builder()
        .username(USERNAME_JANITOR_EMPLOYEE)
        .name(NAME_JANITOR_EMPLOYEE)
        .birthday(BIRTHDAY_JANITOR_EMPLOYEE)
        .type(TYPE_JANITOR_EMPLOYEE)
        .email(EMAIL_JANITOR_EMPLOYEE)
        .build();

    private final Enclosure barn = Enclosure.builder().name("Barn").build();

    private final Animal animal = Animal.builder()
        .name("Horse")
        .description("Fast")
        .species("race")
        .publicInformation("famous")
        .build();

    private TaskDto taskDto = TaskDto.builder()
        .title(TASK_TITLE)
        .description(TASK_DESCRIPTION)
        .startTime(TAST_START_TIME)
        .endTime(TAST_END_TIME)
        .build();

    private Task task = Task.builder()
        .title(TASK_TITLE)
        .description(TASK_DESCRIPTION)
        .startTime(TAST_START_TIME)
        .endTime(TAST_END_TIME)
        .status(TaskStatus.ASSIGNED)
        .build();

    @BeforeEach
    public void beforeEach() {
        animalTaskRepository.deleteAll();
        taskRepository.deleteAll();
        employeeRepository.deleteAll();
        userLoginRepository.deleteAll();
        animalRepository.deleteAll();
        enclosureRepository.deleteAll();

        taskDto = TaskDto.builder()
            .title(TASK_TITLE)
            .description(TASK_DESCRIPTION)
            .startTime(TAST_START_TIME)
            .endTime(TAST_END_TIME)
            .build();

        task = Task.builder()
            .title(TASK_TITLE)
            .description(TASK_DESCRIPTION)
            .startTime(TAST_START_TIME)
            .endTime(TAST_END_TIME)
            .status(TaskStatus.ASSIGNED)
            .build();

    }

    @Test
    public void validAnimalTask_createdByAdmin_returnsExpectedAnimalTaskDto() throws Exception {
        enclosureRepository.save(barn);
        Enclosure enclosure = enclosureRepository.findAll().get(0);
        animal.setEnclosure(enclosure);

        animalRepository.save(animal);
        List<Animal> animals = new LinkedList<>();
        animals.add(animal);
        userLoginRepository.save(animal_caretaker_login);
        anmial_caretaker.setAssignedAnimals(animals);
        employeeRepository.save(anmial_caretaker);
        taskDto.setAssignedEmployeeUsername(USERNAME_ANIMAL_CARE_EMPLOYEE);
        String body = objectMapper.writeValueAsString(taskDto);
        Animal savedAnimal = animalRepository.findAll().get(0);

        MvcResult mvcResult = this.mockMvc.perform(post(ANIMAL_TASK_CREATION_BASE_URI + "/" + savedAnimal.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(body)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        AnimalTaskDto messageResponse = objectMapper.readValue(response.getContentAsString(),
            AnimalTaskDto.class);

        assertAll(
            () -> assertEquals(HttpStatus.CREATED.value(), response.getStatus()),
            () -> assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType()),
            () -> assertEquals(taskDto.getTitle(), messageResponse.getTitle()),
            () -> assertEquals(taskDto.getDescription(), messageResponse.getDescription()),
            () -> assertEquals(taskDto.getAssignedEmployeeUsername(), messageResponse.getAssignedEmployeeUsername()),
            () -> assertEquals(taskDto.getStartTime(), messageResponse.getStartTime()),
            () -> assertEquals(taskDto.getEndTime(), messageResponse.getEndTime()),
            () -> assertEquals(messageResponse.getStatus(), TaskStatus.ASSIGNED),
            () -> assertEquals(animal.getName(), messageResponse.getAnimalName())
        );
    }

    @Test
    public void invalidTimeAnimalTask_createdByAdmin_returnsBadRequest() throws Exception {
        enclosureRepository.save(barn);
        Enclosure enclosure = enclosureRepository.findAll().get(0);
        animal.setEnclosure(enclosure);

        animalRepository.save(animal);
        userLoginRepository.save(animal_caretaker_login);
        employeeRepository.save(anmial_caretaker);
        taskDto.setAssignedEmployeeUsername(USERNAME_ANIMAL_CARE_EMPLOYEE);
        LocalDateTime endTime = taskDto.getEndTime();
        taskDto.setEndTime(taskDto.getStartTime());
        taskDto.setStartTime(endTime);
        String body = objectMapper.writeValueAsString(taskDto);
        Animal savedAnimal = animalRepository.findAll().get(0);

        MvcResult mvcResult = this.mockMvc.perform(post(ANIMAL_TASK_CREATION_BASE_URI + "/" + savedAnimal.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(body)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    }

    @Test
    public void validAnimalTaskButInvalidAssignedWorker_createdByAdmin_returnsUnprocessableEntity() throws Exception {
        enclosureRepository.save(barn);
        Enclosure enclosure = enclosureRepository.findAll().get(0);
        animal.setEnclosure(enclosure);

        animalRepository.save(animal);
        userLoginRepository.save(janitor_login);
        employeeRepository.save(janitor);
        taskDto.setAssignedEmployeeUsername(USERNAME_JANITOR_EMPLOYEE);
        String body = objectMapper.writeValueAsString(taskDto);
        Animal savedAnimal = animalRepository.findAll().get(0);

        MvcResult mvcResult = this.mockMvc.perform(post(ANIMAL_TASK_CREATION_BASE_URI + "/" + savedAnimal.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(body)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus());
    }

    @Test
    public void validTaskButAnimalDoesNotExist_createdByAdmin_returnsNotFound() throws Exception {
        enclosureRepository.save(barn);
        Enclosure enclosure = enclosureRepository.findAll().get(0);
        animal.setEnclosure(enclosure);

        animalRepository.save(animal);
        userLoginRepository.save(animal_caretaker_login);
        employeeRepository.save(anmial_caretaker);
        taskDto.setAssignedEmployeeUsername(USERNAME_JANITOR_EMPLOYEE);
        String body = objectMapper.writeValueAsString(taskDto);
        Animal savedAnimal = animalRepository.findAll().get(0);

        MvcResult mvcResult = this.mockMvc.perform(post(ANIMAL_TASK_CREATION_BASE_URI + "/" + (savedAnimal.getId() + 2))
            .contentType(MediaType.APPLICATION_JSON)
            .content(body)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    @Test
    public void validTaskButEmployeeNotFree_createdByAdmin_returnsConflict() throws Exception {
        enclosureRepository.save(barn);
        Enclosure enclosure = enclosureRepository.findAll().get(0);
        animal.setEnclosure(enclosure);

        animalRepository.save(animal);
        List<Animal> animals = new LinkedList<>();
        animals.add(animal);
        userLoginRepository.save(animal_caretaker_login);
        anmial_caretaker.setAssignedAnimals(animals);
        employeeRepository.save(anmial_caretaker);
        taskRepository.save(Task.builder().assignedEmployee(anmial_caretaker).status(TaskStatus.ASSIGNED).title(TASK_TITLE).description(TASK_DESCRIPTION).startTime(TAST_START_TIME).endTime(TAST_END_TIME).build());
        taskDto.setAssignedEmployeeUsername(USERNAME_ANIMAL_CARE_EMPLOYEE);
        String body = objectMapper.writeValueAsString(taskDto);
        Animal savedAnimal = animalRepository.findAll().get(0);

        MvcResult mvcResult = this.mockMvc.perform(post(ANIMAL_TASK_CREATION_BASE_URI + "/" + savedAnimal.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(body)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.CONFLICT.value(), response.getStatus());
    }

    @Test
    public void validAnimalTaskNoGivenUsername_createdByAdmin_returnsNotAssignedAnimalTaskDto() throws Exception {
        enclosureRepository.save(barn);
        Enclosure enclosure = enclosureRepository.findAll().get(0);
        animal.setEnclosure(enclosure);

        animalRepository.save(animal);
        userLoginRepository.save(animal_caretaker_login);
        employeeRepository.save(anmial_caretaker);

        taskDto.setAssignedEmployeeUsername(null);
        String body = objectMapper.writeValueAsString(taskDto);
        Animal savedAnimal = animalRepository.findAll().get(0);

        MvcResult mvcResult = this.mockMvc.perform(post(ANIMAL_TASK_CREATION_BASE_URI + "/" + savedAnimal.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(body)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        AnimalTaskDto messageResponse = objectMapper.readValue(response.getContentAsString(),
            AnimalTaskDto.class);

        assertAll(
            () -> assertEquals(HttpStatus.CREATED.value(), response.getStatus()),
            () -> assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType()),
            () -> assertEquals(taskDto.getTitle(), messageResponse.getTitle()),
            () -> assertEquals(taskDto.getDescription(), messageResponse.getDescription()),
            () -> assertNull(messageResponse.getAssignedEmployeeUsername()),
            () -> assertEquals(taskDto.getStartTime(), messageResponse.getStartTime()),
            () -> assertEquals(taskDto.getEndTime(), messageResponse.getEndTime()),
            () -> assertEquals(messageResponse.getStatus(), TaskStatus.NOT_ASSIGNED),
            () -> assertEquals(animal.getName(), messageResponse.getAnimalName())
        );
    }


    @Test
    public void invalidAnimalTaskUpdate_employeeDoesNotFulfillAssignmentCriteria_returnsUnprocessableEntity() throws Exception {
        enclosureRepository.save(barn);
        Enclosure enclosure = enclosureRepository.findAll().get(0);
        animal.setEnclosure(enclosure);

        animalRepository.save(animal);
        userLoginRepository.save(animal_caretaker_login);
        employeeRepository.save(anmial_caretaker);

        task.setAssignedEmployee(null);
        task.setStatus(TaskStatus.NOT_ASSIGNED);
        taskRepository.save(task);
        task = taskRepository.findAll().get(0);
        AnimalTask animalTask = AnimalTask.builder().subject(animalRepository.findAll().get(0)).id(task.getId()).build();
        animalTaskRepository.save(animalTask);

        EmployeeDto employeeDto = EmployeeDto.builder().username(USERNAME_ANIMAL_CARE_EMPLOYEE).build();
        String body = objectMapper.writeValueAsString(employeeDto);

        MvcResult mvcResult = this.mockMvc.perform(put(TASK_BASE_URI + "/" + task.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(body)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus())
        );
    }

    @Test
    public void invalidAnimalTaskUpdate_alreadyAssigned_returnsUnprocessableEntity() throws Exception {
        enclosureRepository.save(barn);
        Enclosure enclosure = enclosureRepository.findAll().get(0);
        animal.setEnclosure(enclosure);

        animalRepository.save(animal);
        List<Animal> animals = new LinkedList<>();
        animals.add(animalRepository.findAll().get(0));
        userLoginRepository.save(animal_caretaker_login);
        anmial_caretaker.setAssignedAnimals(animals);
        employeeRepository.save(anmial_caretaker);

        task.setAssignedEmployee(null);
        task.setStatus(TaskStatus.ASSIGNED);
        taskRepository.save(task);
        task = taskRepository.findAll().get(0);
        AnimalTask animalTask = AnimalTask.builder().subject(animalRepository.findAll().get(0)).id(task.getId()).build();
        animalTaskRepository.save(animalTask);

        EmployeeDto employeeDto = EmployeeDto.builder().username(USERNAME_ANIMAL_CARE_EMPLOYEE).build();
        String body = objectMapper.writeValueAsString(employeeDto);

        MvcResult mvcResult = this.mockMvc.perform(put(TASK_BASE_URI + "/" + task.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(body)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus())
        );
    }

    @Test
    public void invalidAnimalTaskUpdate_taskIdDoesntExist_returnsNotFound() throws Exception {
        enclosureRepository.save(barn);
        Enclosure enclosure = enclosureRepository.findAll().get(0);
        animal.setEnclosure(enclosure);

        animalRepository.save(animal);
        List<Animal> animals = new LinkedList<>();
        animals.add(animalRepository.findAll().get(0));
        userLoginRepository.save(animal_caretaker_login);
        anmial_caretaker.setAssignedAnimals(animals);
        employeeRepository.save(anmial_caretaker);

        task.setAssignedEmployee(null);
        task.setStatus(TaskStatus.NOT_ASSIGNED);
        taskRepository.save(task);
        task = taskRepository.findAll().get(0);
        AnimalTask animalTask = AnimalTask.builder().subject(animalRepository.findAll().get(0)).id(task.getId()).build();
        animalTaskRepository.save(animalTask);

        EmployeeDto employeeDto = EmployeeDto.builder().username(USERNAME_ANIMAL_CARE_EMPLOYEE).build();
        String body = objectMapper.writeValueAsString(employeeDto);

        MvcResult mvcResult = this.mockMvc.perform(put(TASK_BASE_URI + "/" + (task.getId() + 1))
            .contentType(MediaType.APPLICATION_JSON)
            .content(body)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus())
        );
    }

    @Test
    public void validAnimalTaskUpdate_returnsOk() throws Exception {
        enclosureRepository.save(barn);
        Enclosure enclosure = enclosureRepository.findAll().get(0);
        animal.setEnclosure(enclosure);

        animalRepository.save(animal);
        List<Animal> animals = new LinkedList<>();
        animals.add(animalRepository.findAll().get(0));
        userLoginRepository.save(animal_caretaker_login);
        anmial_caretaker.setAssignedAnimals(animals);
        employeeRepository.save(anmial_caretaker);

        task.setAssignedEmployee(null);
        task.setStatus(TaskStatus.NOT_ASSIGNED);
        taskRepository.save(task);
        task = taskRepository.findAll().get(0);
        AnimalTask animalTask = AnimalTask.builder().subject(animalRepository.findAll().get(0)).id(task.getId()).build();
        animalTaskRepository.save(animalTask);

        EmployeeDto employeeDto = EmployeeDto.builder().username(USERNAME_ANIMAL_CARE_EMPLOYEE).build();
        String body = objectMapper.writeValueAsString(employeeDto);

        MvcResult mvcResult = this.mockMvc.perform(put(TASK_BASE_URI + "/" + task.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(body)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), response.getStatus())
        );
    }

    @Test
    public void deleteTask_StatusOk() throws Exception {
        Animal savedAnimal = animalRepository.save(animal);
        Task savedTask = taskRepository.save(task);
        animalTaskRepository.save(AnimalTask.builder().id(savedTask.getId()).subject(savedAnimal).build());
        MvcResult mvcResult = this.mockMvc.perform(delete(TASK_BASE_URI + "/" + task.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());
    }

    @Test
    public void deleteTask_whenTaskIdNotExisting_StatusNotFound() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(delete(TASK_BASE_URI + "/10")
            .contentType(MediaType.APPLICATION_JSON)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }
}
