package at.ac.tuwien.sepm.groupphase.backend.integrationtest.endpoint;


import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.AnimalDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.MessageInquiryDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.AnimalMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Animal;
import at.ac.tuwien.sepm.groupphase.backend.entity.Employee;
import at.ac.tuwien.sepm.groupphase.backend.entity.Enclosure;
import at.ac.tuwien.sepm.groupphase.backend.entity.UserLogin;
import at.ac.tuwien.sepm.groupphase.backend.repository.AnimalRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.EmployeeRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.EnclosureRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserLoginRepository;
import at.ac.tuwien.sepm.groupphase.backend.security.JwtTokenizer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class AnimalEndpointTest implements TestData {

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private UserLoginRepository userLoginRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AnimalRepository animalRepository;

    @Autowired
    private EnclosureRepository enclosureRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AnimalMapper animalMapper;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private SecurityProperties securityProperties;

    private Animal animal= Animal.builder()
        .id(1L)
        .name("Horse")
        .description("Fast")
        .enclosure(null)
        .species("race")
        .publicInformation("famous")
        .build();

    private Enclosure enclosureDetailed = Enclosure.builder()
        .name(NAME_LION_ENCLOSURE)
        .description(DESCRIPTION_LION_ENCLOSURE)
        .publicInfo(PUBLIC_INFO_LION_ENCLOSURE)
        .picture(PICTURE_LION_ENCLOSURE)
        .build();

    private UserLogin default_user_login = UserLogin.builder()
        .isAdmin(false)
        .username(DEFAULT_USER)
        .password(passwordEncoder.encode(VALID_TEST_PASSWORD))
        .build();

    private Employee default_user = Employee.builder()
        .username(DEFAULT_USER)
        .name(NAME_ANIMAL_CARE_EMPLOYEE)
        .birthday(BIRTHDAY_ANIMAL_CARE_EMPLOYEE)
        .type(TYPE_ANIMAL_CARE_EMPLOYEE)
        .email(EMAIL_JANITOR_EMPLOYEE)
        .build();


    @BeforeEach
    public void beforeEach() {
        animalRepository.deleteAll();
        employeeRepository.deleteAll();
        userLoginRepository.deleteAll();
        enclosureRepository.deleteAll();

        animal = Animal.builder()
            .id(1L)
            .name("Horse")
            .description("Fast")
            .enclosure(null)
            .species("race")
            .publicInformation("famous")
            .build();
    }

    @AfterEach
    public void afterEach(){
        animalRepository.deleteAll();
        employeeRepository.deleteAll();
        userLoginRepository.deleteAll();
        enclosureRepository.deleteAll();
    }

    @Test
    public void givenOneAnimal_whenGet_thenOK() throws Exception {
        animalRepository.save(animal);
        MvcResult mvcResult = this.mockMvc.perform(get(ANIMAL_BASE_URI)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());
    }

    @Test
    public void givenNothing_whenPostInvalid_then400() throws Exception {
        animal.setName(null);
        animal.setDescription(null);
        animal.setSpecies(null);
        AnimalDto animalDto = animalMapper.animalToAnimalDto(animal);
        String body = objectMapper.writeValueAsString(animalDto);

        MvcResult mvcResult = this.mockMvc.perform(post(ANIMAL_BASE_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .content(body)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus())
        );
    }

    @Test
    public void givenAnimal_whenPostSuccessed_then201() throws Exception {
        AnimalDto animalDto = animalMapper.animalToAnimalDto(animal);
        String body = objectMapper.writeValueAsString(animalDto);

        MvcResult mvcResult = this.mockMvc.perform(post(ANIMAL_BASE_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .content(body)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.CREATED.value(), response.getStatus())
        );
    }

    @Test
    public void deleteAssignedAnimal_StatusOk() throws Exception{
        userLoginRepository.save(default_user_login);
        employeeRepository.save(default_user);
        animalRepository.save(animal);
        Animal animalN=animalRepository.findByDescription(animal.getDescription()).get(0);
        animalRepository.assignAnimalToCaretaker(default_user.getUsername(),animalN.getId());

        MvcResult mvcResult = this.mockMvc.perform(delete(ANIMAL_BASE_URI+'/'+animalN.getId())
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), response.getStatus())
        );
    }

    @Test
    public void deleteNotExistingAnimal_statusNotFound() throws Exception{
        MvcResult mvcResult = this.mockMvc.perform(delete(ANIMAL_BASE_URI+"/300")
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        assertAll(
            () -> assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus())
        );
    }

    @Test
    public void getAnimalsByEnclosure_whenNoAnimalsAssigned_statusOkAndEmptyList() throws Exception {
        long id = enclosureRepository.save(enclosureDetailed).getId();
        MvcResult mvcResult = this.mockMvc.perform(get(ANIMAL_BASE_URI + "/enclosure/" + id)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        List<AnimalDto> animalDtos = Arrays.asList(objectMapper.readValue(response.getContentAsString(), AnimalDto[].class));
        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
            () -> assertTrue(animalDtos.isEmpty())
        );
    }

    @Test
    public void getAnimalsByEnclosure_whenAnimalsAssigned_statusOkAndAnimalsInList() throws Exception {
        Enclosure savedEnclosure = enclosureRepository.save(enclosureDetailed);
        Animal newAnimal= Animal.builder()
            .id(1L)
            .name("Horse")
            .description("Fast")
            .enclosure(savedEnclosure)
            .species("race")
            .publicInformation("famous")
            .build();
        Animal savedAnimal = animalRepository.save(newAnimal);
        MvcResult mvcResult = this.mockMvc.perform(get(ANIMAL_BASE_URI + "/enclosure/" + savedEnclosure.getId())
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        List<AnimalDto> animalDtos = Arrays.asList(objectMapper.readValue(response.getContentAsString(), AnimalDto[].class));
        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
            () -> assertTrue(animalDtos.contains(animalMapper.animalToAnimalDto(savedAnimal)))
        );
    }

    @Test
    public void getAnimalsByEnclosure_notExistingEnclosure_statusNotFound() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get(ANIMAL_BASE_URI + "/enclosure/5")
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }
}
