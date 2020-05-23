package at.ac.tuwien.sepm.groupphase.backend.unittests.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.Animal;
import at.ac.tuwien.sepm.groupphase.backend.entity.Enclosure;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.AnimalRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.EnclosureRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.EnclosureService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.LinkedList;
import java.util.List;

import static at.ac.tuwien.sepm.groupphase.backend.basetest.TestData.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class EnclosureServiceTest {

    @Autowired
    EnclosureService enclosureService;

    @MockBean
    EnclosureRepository enclosureRepository;

    @MockBean
    AnimalRepository animalRepository;

    private Enclosure enclosureDetailed = Enclosure.builder()
        .name(NAME_LION_ENCLOSURE)
        .description(DESCRIPTION_LION_ENCLOSURE)
        .publicInfo(PUBLIC_INFO_LION_ENCLOSURE)
        .picture(PICTURE_LION_ENCLOSURE)
        .build();

    private Enclosure enclosureMinimal = Enclosure.builder()
        .name("Wolf Enclosure")
        .description(null)
        .publicInfo(null)
        .picture(null)
        .build();

    private final Animal animal = Animal.builder()
        .name("Brandy")
        .description("racing Horse")
        .enclosure(new Enclosure())
        .species("race")
        .publicInformation(null)
        .build();

    @BeforeEach
    public void beforeEach() {
        enclosureRepository.deleteAll();
    }

    @AfterEach
    public void afterEach() {
        enclosureRepository.deleteAll();
    }

    @Test
    public void filledRepository_whenFindAll_thenListOfAllEnclosures() {

        List<Enclosure> enclosures = new LinkedList<>();
        enclosures.add(enclosureDetailed);
        enclosures.add(enclosureMinimal);

        Mockito.when(enclosureRepository.findAll()).thenReturn(enclosures);
        assertEquals(2, enclosureService.getAll().size());
    }


    @Test
    public void EmptyRepository_whenFindAll_thenEmptyListOfAllEnclosures() {

        List<Enclosure> enclosures = new LinkedList<>();

        Mockito.when(enclosureRepository.findAll()).thenReturn(enclosures);
        assertTrue(enclosureService.getAll().isEmpty());
    }


    @Test
    public void saveEnclosurebyGivingOnlyMandatoryValues() {

        Mockito.when(enclosureService.create(enclosureMinimal)).thenReturn(enclosureMinimal);

        assertEquals(enclosureMinimal, enclosureService.create(enclosureMinimal));
    }

    @Test
    public void saveEnclosurebyGivingAllValues() {

        Mockito.when(enclosureService.create(enclosureDetailed)).thenReturn(enclosureDetailed);

        assertEquals(enclosureDetailed, enclosureService.create(enclosureDetailed));
    }

    @Test
    public void findByAnimalId_whenNoValidId_thenNotFoundException() {

        Mockito.when(animalRepository.findById(1)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> enclosureService.findByAnimalId(1));
    }

    @Test
    public void findByAnimalId_whenValidId_thenReturnEnclosure() {

        Mockito.when(animalRepository.findById(1)).thenReturn(animal);

        assertEquals(animal.getEnclosure(), enclosureService.findByAnimalId(1));
    }

}
