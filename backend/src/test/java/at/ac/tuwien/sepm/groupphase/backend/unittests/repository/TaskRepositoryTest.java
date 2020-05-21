package at.ac.tuwien.sepm.groupphase.backend.unittests.repository;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.TaskDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Animal;
import at.ac.tuwien.sepm.groupphase.backend.entity.Employee;
import at.ac.tuwien.sepm.groupphase.backend.entity.Task;
import at.ac.tuwien.sepm.groupphase.backend.entity.UserLogin;
import at.ac.tuwien.sepm.groupphase.backend.repository.EmployeeRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.TaskRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserLoginRepository;
import at.ac.tuwien.sepm.groupphase.backend.types.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
// This test slice annotation is used instead of @SpringBootTest to load only repository beans instead of
// the entire application context
@DataJpaTest
@ActiveProfiles("test")
public class TaskRepositoryTest implements TestData {

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    TaskRepository taskRepository;


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

    private Task task = Task.builder()
        .title(TASK_TITLE)
        .description(TASK_DESCRIPTION)
        .startTime(TAST_START_TIME)
        .endTime(TAST_END_TIME)
        .status(TaskStatus.NOT_ASSIGNED)
        .build();

    @BeforeEach
    public void beforeEach() {
        taskRepository.deleteAll();

        task = Task.builder()
            .title(TASK_TITLE)
            .description(TASK_DESCRIPTION)
            .startTime(TAST_START_TIME)
            .endTime(TAST_END_TIME)
            .status(TaskStatus.NOT_ASSIGNED)
            .build();
    }
    /*
    @Test
    public void givenNothing_whenSaveTask_thenFindTaskById() {
        task.setAssignedEmployee(anmial_caretaker);
        task.setStatus(TaskStatus.ASSIGNED);
        taskRepository.save(task);
        Task searchTask = taskRepository.findAll().get(0);
        assertNotNull(taskRepository.findById(searchTask.getId()));
    }

    @Test
    public void givenNothing_searchingForTasksOfEmployee_thenFindAllTasks() {
        task.setAssignedEmployee(anmial_caretaker);
        task.setStatus(TaskStatus.ASSIGNED);
        taskRepository.save(task);
        taskRepository.save(task);
        taskRepository.save(task);
        List<Task> searchTask = taskRepository.findAllByAssignedEmployee(anmial_caretaker);
        assertEquals(searchTask.size(), 3);
    }

     */
}
