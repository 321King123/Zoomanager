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
        task_not_assigned.setStatus(TaskStatus.NOT_ASSIGNED);
        task_not_assigned.setAssignedEmployee(anmial_caretaker);
        AnimalTask animalTask = taskService.createAnimalTask(task_not_assigned,animal);
        Assertions.assertEquals(TaskStatus.ASSIGNED,animalTask.getTask().getStatus());
        task_not_assigned.setStatus(TaskStatus.NOT_ASSIGNED);
    }
}
