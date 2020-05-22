package at.ac.tuwien.sepm.groupphase.backend.unittests.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.Enclosure;
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
        assertTrue(enclosureService.getAll().size()==2);
    }


    @Test
    public void EmptyRepository_whenFindAll_thenEmptyListOfAllEnclosures() {

        List<Enclosure> enclosures = new LinkedList<>();

        Mockito.when(enclosureRepository.findAll()).thenReturn(enclosures);
        assertTrue(enclosureService.getAll().size()==0);
    }


    @Test
    public void saveEnclosurebyGivingOnlyMandatoryValues() {

        Mockito.when(enclosureService.create(enclosureMinimal)).thenReturn(enclosureMinimal);

        assertAll(
            () -> assertEquals(enclosureMinimal, enclosureService.create(enclosureMinimal))
        );
    }

    @Test
    public void saveEnclosurebyGivingAllValues() {

        Mockito.when(enclosureService.create(enclosureDetailed)).thenReturn(enclosureDetailed);

        assertAll(
            () -> assertEquals(enclosureDetailed, enclosureService.create(enclosureDetailed))
        );
    }



}
