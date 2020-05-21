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
        .name("Lion Cage")
        .description("very dangerous")
        .publicInfo("do not enter")
        .picture("data:image/jpeg;base64,/9j/4AAQSkZJRgABAQEAYABgAAD/2wBDAAMCAgMCAgMDAwMEAwMEBQgFBQQEBQoHBwYIDAoMDAsKCwsNDhIQDQ4RDgsLEBYQERMUFRUVDA8XGBYUGBIUFRT/2wBDAQMEBAUEBQkFBQkUDQsNFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBT/wAARCAAJAAwDASIAAhEBAxEB/8QAHwAAAQUBAQEBAQEAAAAAAAAAAAECAwQFBgcICQoL/8QAtRAAAgEDAwIEAwUFBAQAAAF9AQIDAAQRBRIhMUEGE1FhByJxFDKBkaEII0KxwRVS0fAkM2JyggkKFhcYGRolJicoKSo0NTY3ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqDhIWGh4iJipKTlJWWl5iZmqKjpKWmp6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uHi4+Tl5ufo6erx8vP09fb3+Pn6/8QAHwEAAwEBAQEBAQEBAQAAAAAAAAECAwQFBgcICQoL/8QAtREAAgECBAQDBAcFBAQAAQJ3AAECAxEEBSExBhJBUQdhcRMiMoEIFEKRobHBCSMzUvAVYnLRChYkNOEl8RcYGRomJygpKjU2Nzg5OkNERUZHSElKU1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6goOEhYaHiImKkpOUlZaXmJmaoqOkpaanqKmqsrO0tba3uLm6wsPExcbHyMnK0tPU1dbX2Nna4uPk5ebn6Onq8vP09fb3+Pn6/9oADAMBAAIRAxEAPwCfThoHgPwjqWn6Nqc0eo6S5snsZQkTWuH+aREDFdrEqQcn+IZzXnfj7SdA8Qa3HqF1dzaiZ7eNku7cxMki4IznB5BDA+4Na0X/ACVDx3/2w/8ARleBH/jwsP8Ark3/AKNkqYR5lzMuWjsf/9k=".getBytes())
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
    public void filledRepository_whenFindAll_thenListOfAllEmployees() {
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
