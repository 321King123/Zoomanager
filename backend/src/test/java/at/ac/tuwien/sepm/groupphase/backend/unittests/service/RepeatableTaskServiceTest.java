package at.ac.tuwien.sepm.groupphase.backend.unittests.service;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import at.ac.tuwien.sepm.groupphase.backend.repository.*;
import at.ac.tuwien.sepm.groupphase.backend.service.EmployeeService;
import at.ac.tuwien.sepm.groupphase.backend.service.TaskService;
import at.ac.tuwien.sepm.groupphase.backend.types.TaskStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

import java.time.temporal.ChronoUnit;
import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class RepeatableTaskServiceTest implements TestData {

    @Autowired
    TaskService taskService;

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    AnimalTaskRepository animalTaskRepository;

    @Autowired
    RepeatableTaskRepository repeatableTaskRepository;

    @Autowired
    AnimalRepository animalRepository;

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    EmployeeService employeeService;

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
        .workTimeStart(TEST_LOCAL_TIME_START)
        .workTimeEnd(TEST_LOCAL_TIME_END)
        .build();

    private Task task_assigned = Task.builder()
        .title(TASK_TITLE)
        .description(TASK_DESCRIPTION)
        .startTime(TAST_START_TIME)
        .endTime(TAST_END_TIME)
        .status(TaskStatus.ASSIGNED)
        .priority(false)
        .build();

    @BeforeEach
    public void beforeEach() {
        animalTaskRepository.deleteAll();
        repeatableTaskRepository.deleteAll();
        taskRepository.deleteAll();
        employeeRepository.deleteAll();
        animalRepository.deleteAll();
        animal.setId(animalRepository.save(animal).getId());
        task_assigned.setAssignedEmployee(employeeRepository.save(anmial_caretaker));
        employeeService.assignAnimal(anmial_caretaker.getUsername(), animal.getId());
    }

    @AfterEach
    public void afterEach() {
        animalTaskRepository.deleteAll();
        repeatableTaskRepository.deleteAll();
        taskRepository.deleteAll();
        employeeRepository.deleteAll();
        animalRepository.deleteAll();
        animal.setId(2L);
        task_assigned.setAssignedEmployee(null);
    }

    @Test
    public void creatingRepeatableTasks_thenTasksInRepository() {
        List<AnimalTask> animalTaskList = taskService.createRepeatableAnimalTask(task_assigned, animal, 4, ChronoUnit.DAYS, 2);

        RepeatableTask firstTaskRepeatable = repeatableTaskRepository.findById(animalTaskList.get(3).getId()).get();
        Task firstTask = firstTaskRepeatable.getTask();
        Task secondTask = firstTaskRepeatable.getFollowTask();

        assertNotNull(firstTaskRepeatable);
        assertEquals(4, taskRepository.findAll().size());
        assertEquals(firstTask.getStartTime().plus(2, ChronoUnit.DAYS), secondTask.getStartTime());
    }
}
