package at.ac.tuwien.sepm.groupphase.backend.basetest;

import at.ac.tuwien.sepm.groupphase.backend.entity.Animal;
import at.ac.tuwien.sepm.groupphase.backend.entity.UserLogin;
import at.ac.tuwien.sepm.groupphase.backend.types.EmployeeType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

public interface TestData {


    Long ID = 1L;
    String TEST_NEWS_TITLE = "Title";
    String TEST_NEWS_SUMMARY = "Summary";
    String TEST_NEWS_TEXT = "TestMessageText";
    LocalDateTime TEST_NEWS_PUBLISHED_AT =
        LocalDateTime.of(2019, 11, 13, 12, 15, 0, 0);

    String BASE_URI = "/api/v1";
    String MESSAGE_BASE_URI = BASE_URI + "/messages";
    String ANIMAL_BASE_URI = BASE_URI + "/animals";
    String EMPLOYEE_BASE_URI = BASE_URI + "/employee";

    String USERNAME_ANIMAL_CARE_EMPLOYEE = "AnimalCarer";
    String NAME_ANIMAL_CARE_EMPLOYEE = "AnimalCarerName";
    Date BIRTHDAY_ANIMAL_CARE_EMPLOYEE = new Date(2000,Calendar.JANUARY,1);
    EmployeeType TYPE_ANIMAL_CARE_EMPLOYEE = EmployeeType.ANIMAL_CARE;
    String EMAIL_ANIMAL_CARE_EMPLOYEE = "animalcare@email.com";
    List<Animal> ASSIGNED_ANIMALS_ANIMAL_CARE_EMPLOYEE = new LinkedList<>();

    String USERNAME_DOCTOR_EMPLOYEE = "Doctor";
    String NAME_DOCTOR_EMPLOYEE = "DoctorName";
    Date BIRTHDAY_DOCTOR_EMPLOYEE = new Date(2000,Calendar.JANUARY,1);
    EmployeeType TYPE_DOCTOR_EMPLOYEE = EmployeeType.DOCTOR;
    String EMAIL_DOCTOR_EMPLOYEE = "doctor@email.com";

    String USERNAME_JANITOR_EMPLOYEE = "Janitor";
    String NAME_JANITOR_EMPLOYEE = "JanitorName";
    Date BIRTHDAY_JANITOR_EMPLOYEE = new Date(2000,Calendar.JANUARY,1);
    EmployeeType TYPE_JANITOR_EMPLOYEE = EmployeeType.JANITOR;
    String EMAIL_JANITOR_EMPLOYEE = "janitor@email.com";

    String VALID_TEST_PASSWORD = "$2a$10$Wlg9Rz/smx0T1ULlBFaGneTWjsTHNd6URL/odEdOoyZ63MpQv28vq";

    String ADMIN_USER = "admin";
    List<String> ADMIN_ROLES = new ArrayList<>() {
        {
            add("ROLE_ADMIN");
            add("ROLE_USER");
        }
    };
    String DEFAULT_USER = "user";
    List<String> USER_ROLES = new ArrayList<>() {
        {
            add("ROLE_USER");
        }
    };

}
