package at.ac.tuwien.sepm.groupphase.backend.unittests.service;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.entity.Animal;
import at.ac.tuwien.sepm.groupphase.backend.entity.AnimalTask;
import at.ac.tuwien.sepm.groupphase.backend.entity.Employee;
import at.ac.tuwien.sepm.groupphase.backend.entity.Task;
import at.ac.tuwien.sepm.groupphase.backend.exception.IncorrectTypeException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.AnimalTaskRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.TaskRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.EmployeeService;
import at.ac.tuwien.sepm.groupphase.backend.service.TaskService;
import at.ac.tuwien.sepm.groupphase.backend.types.TaskStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static org.mockito.AdditionalAnswers.returnsFirstArg;

import javax.validation.ValidationException;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class TaskServiceTest implements TestData {

    @Autowired
    TaskService taskService;

    @MockBean
    EmployeeService employeeService;

    @MockBean
    TaskRepository taskRepository;

    @MockBean
    AnimalTaskRepository animalTaskRepository;

    Animal animal = Animal.builder()
        .id(2L)
        .name("Brandy")
        .description("racing Horce")
        .enclosure(null)
        .species("race")
        .publicInformation(null)
        .build();

    private Employee anmial_caretaker = Employee.builder()
        .username(USERNAME_ANIMAL_CARE_EMPLOYEE)
        .name(NAME_ANIMAL_CARE_EMPLOYEE)
        .birthday(BIRTHDAY_ANIMAL_CARE_EMPLOYEE)
        .type(TYPE_ANIMAL_CARE_EMPLOYEE)
        .email(EMAIL_ANIMAL_CARE_EMPLOYEE)
        .build();


    private Task task_assigned = Task.builder()
        .id(2L)
        .title(TASK_TITLE)
        .description(TASK_DESCRIPTION)
        .startTime(TAST_START_TIME)
        .endTime(TAST_END_TIME)
        .status(TaskStatus.ASSIGNED)
        .build();

    private Task task_not_assigned = Task.builder()
        .id(1L)
        .title(TASK_TITLE)
        .description(TASK_DESCRIPTION)
        .startTime(TAST_START_TIME)
        .endTime(TAST_END_TIME)
        .status(TaskStatus.NOT_ASSIGNED)
        .build();

    private Task task_endTimeBeforeStartTime = Task.builder()
        .id(1L)
        .title(TASK_TITLE)
        .description(TASK_DESCRIPTION)
        .startTime(TAST_END_TIME)
        .endTime(TAST_START_TIME)
        .status(TaskStatus.NOT_ASSIGNED)
        .build();

    private AnimalTask animalTask_not_assigned = AnimalTask.builder()
        .id(1L)
        .subject(animal)
        .task(task_not_assigned)
        .build();

    private Employee janitor = Employee.builder()
        .username(USERNAME_JANITOR_EMPLOYEE)
        .name(NAME_JANITOR_EMPLOYEE)
        .birthday(BIRTHDAY_JANITOR_EMPLOYEE)
        .type(TYPE_JANITOR_EMPLOYEE)
        .email(EMAIL_JANITOR_EMPLOYEE)
        .build();

    private Task task_assigned_to_janitor = Task.builder()
        .id(3L)
        .title(TASK_TITLE)
        .description(TASK_DESCRIPTION)
        .startTime(TAST_START_TIME)
        .endTime(TAST_END_TIME)
        .status(TaskStatus.ASSIGNED)
        .assignedEmployee(janitor)
        .build();

    @BeforeEach
    public void beforeEach() {
        Mockito.when(taskRepository.save(Mockito.any(Task.class))).then(returnsFirstArg());
        Mockito.when(animalTaskRepository.save(Mockito.any(AnimalTask.class))).then(returnsFirstArg());
        Mockito.when(employeeService.employeeIsFreeBetweenStartingAndEndtime(Mockito.any(Employee.class),
                Mockito.any(Task.class))).thenReturn(true);
    }

    @Test
    public void testWithAnimalNull_expectNotFoundException() throws Exception {
        assertThrows(NotFoundException.class, () -> {
            taskService.createAnimalTask(task_not_assigned, null);
        });
    }

    @Test
    public void testWithEndTimeBeforeStartTime_expectValidationException() throws Exception {
        assertThrows(ValidationException.class, () -> {
            taskService.createAnimalTask(task_endTimeBeforeStartTime, animal);
        });
    }

    @Test
    public void testWithJanitor_expectIncorrectTypeException() {
        assertThrows(IncorrectTypeException.class, () -> {
            taskService.createAnimalTask(task_assigned_to_janitor, animal);
        });
    }

   @Test
    public void testReturnedAnimal_expectStatusNotAssigned(){
        task_assigned.setStatus(TaskStatus.ASSIGNED);
         AnimalTask animalTask = taskService.createAnimalTask(task_assigned,animal);
         Assertions.assertEquals(TaskStatus.NOT_ASSIGNED,animalTask.getTask().getStatus());
         task_assigned.setStatus(TaskStatus.ASSIGNED);
    }

    @Test
    public void testWithAssignedEmployee_expectStatusAssigned(){
        Mockito.when(employeeService.isAssignedToAnimal(Mockito.any(String.class),
            Mockito.any(Long.class))).thenReturn(true);
        task_not_assigned.setStatus(TaskStatus.NOT_ASSIGNED);
        task_not_assigned.setAssignedEmployee(anmial_caretaker);
        AnimalTask animalTask = taskService.createAnimalTask(task_not_assigned,animal);
        Assertions.assertEquals(TaskStatus.ASSIGNED,animalTask.getTask().getStatus());
        task_not_assigned.setStatus(TaskStatus.NOT_ASSIGNED);
    }

    @Test
    public void validTaskAndEmployee_updateTask_expectNoErrors(){
        Optional<Task> task = Optional.of(task_not_assigned);
        Mockito.when(taskRepository.findById(Mockito.any(Long.class))).thenReturn(task);
        Mockito.when(employeeService.canBeAssignedToTask(Mockito.any(Employee.class),
            Mockito.any(Task.class))).thenReturn(true);
        Mockito.when(employeeService.findByUsername(Mockito.anyString())).thenReturn(anmial_caretaker);
        taskService.updateTask(1L, anmial_caretaker);
    }

    @Test
    public void validTaskButEmployeeNotFulfillingCriteria_updateTask_expectIncorrectTypeException(){
        Optional<Task> task = Optional.of(task_not_assigned);
        Mockito.when(taskRepository.findById(Mockito.any(Long.class))).thenReturn(task);
        Mockito.when(employeeService.canBeAssignedToTask(Mockito.any(Employee.class),
            Mockito.any(Task.class))).thenReturn(false);
        Mockito.when(employeeService.findByUsername(Mockito.anyString())).thenReturn(anmial_caretaker);
        assertThrows(IncorrectTypeException.class, () -> taskService.updateTask(1L, anmial_caretaker));
    }

    @Test
    public void TaskDoesNotExist_updateTask_expectNotFoundException(){
        Optional<Task> task = Optional.empty();
        Mockito.when(taskRepository.findById(Mockito.any(Long.class))).thenReturn(task);
        Mockito.when(employeeService.canBeAssignedToTask(Mockito.any(Employee.class),
            Mockito.any(Task.class))).thenReturn(false);
        Mockito.when(employeeService.findByUsername(Mockito.anyString())).thenReturn(anmial_caretaker);
        assertThrows(NotFoundException.class, () -> taskService.updateTask(1L, anmial_caretaker));
    }

    @Test
    public void validTaskButAlreadyAssigned_updateTask_expectIncorrectTypeException(){
        Optional<Task> task = Optional.of(task_assigned);
        Mockito.when(taskRepository.findById(Mockito.any(Long.class))).thenReturn(task);
        Mockito.when(employeeService.canBeAssignedToTask(Mockito.any(Employee.class),
            Mockito.any(Task.class))).thenReturn(true);
        Mockito.when(employeeService.findByUsername(Mockito.anyString())).thenReturn(anmial_caretaker);
        assertThrows(IncorrectTypeException.class, () -> taskService.updateTask(1L, anmial_caretaker));
    }

    @Test
    public void validEmployeeGetAllAnimalTasksReturnsHisTasks(){
        List<Task> tasks = new LinkedList<>();
        tasks.add(task_assigned);
        Mockito.when(taskRepository.findAllByAssignedEmployee(anmial_caretaker)).thenReturn(tasks);
        Optional<AnimalTask> animalTask = Optional.of(animalTask_not_assigned);
        Mockito.when(animalTaskRepository.findById(Mockito.any(Long.class))).thenReturn(animalTask);
        Mockito.when(employeeService.findByUsername(Mockito.anyString())).thenReturn(anmial_caretaker);
        List<AnimalTask> animalTasks = taskService.getAllAnimalTasksOfEmployee(anmial_caretaker.getUsername());
        assertEquals(1, animalTasks.size());
        assertEquals(animalTask_not_assigned, animalTasks.get(0));
    }

    @Test
    public void invalidEmployeeGetAllAnimalTasksReturnsNotFound(){
        List<Task> tasks = new LinkedList<>();
        tasks.add(task_assigned);
        Mockito.when(taskRepository.findAllByAssignedEmployee(anmial_caretaker)).thenReturn(tasks);
        Optional<AnimalTask> animalTask = Optional.of(animalTask_not_assigned);
        Mockito.when(animalTaskRepository.findById(Mockito.any(Long.class))).thenReturn(animalTask);
        Mockito.when(employeeService.findByUsername(Mockito.anyString())).thenReturn(null);
        assertThrows(NotFoundException.class, () -> taskService.getAllAnimalTasksOfEmployee(anmial_caretaker.getUsername()));
    }

    @Test
    public void validEmployeeGetAllAnimalTasksButNoTasksExistReturnsEmptyList(){
        List<Task> tasks = new LinkedList<>();
        Mockito.when(taskRepository.findAllByAssignedEmployee(anmial_caretaker)).thenReturn(tasks);
        Mockito.when(employeeService.findByUsername(Mockito.anyString())).thenReturn(anmial_caretaker);
        List<AnimalTask> animalTasks = taskService.getAllAnimalTasksOfEmployee(anmial_caretaker.getUsername());
        assertEquals(0, animalTasks.size());
    }
}
