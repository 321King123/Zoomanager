package at.ac.tuwien.sepm.groupphase.backend.unittests.repository;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.entity.Enclosure;
import at.ac.tuwien.sepm.groupphase.backend.repository.EnclosureRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(SpringExtension.class)
// This test slice annotation is used instead of @SpringBootTest to load only repository beans instead of
// the entire application context
@DataJpaTest
@ActiveProfiles("test")
public class EnclosureRepositoryTest implements TestData {

    @Autowired
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
    public void emptyRepository_whenFindAll_thenEmptyList() {
        List<Enclosure> enclosures = enclosureRepository.findAll();
        assertTrue(enclosures.isEmpty());
    }

    @Test
    public void filledRepository_whenFindAll_thenListOfAllEnclosures() {
        enclosureRepository.save(enclosureDetailed);
        enclosureRepository.save(enclosureMinimal);
        List<Enclosure> enclosures = enclosureRepository.findAll();
        assertTrue(enclosures.contains(enclosureDetailed));
        assertTrue(enclosures.contains(enclosureMinimal));
    }

    @Test
    public void findById_returnsCorrespondingEnclosure() {
        long id = enclosureRepository.save(enclosureDetailed).getId();
        enclosureRepository.save(enclosureMinimal);
        assertEquals(enclosureDetailed,enclosureRepository.findById(id));
    }

    @Test
    public void findById_returnsNullIfIdDoesNotExist() {
        long id = enclosureRepository.save(enclosureDetailed).getId();
        assertNull(enclosureRepository.findById(id+1));
    }
}
