package at.ac.tuwien.sepm.groupphase.backend.unittests.repository;


import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.entity.Animal;
import at.ac.tuwien.sepm.groupphase.backend.repository.AnimalRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
// This test slice annotation is used instead of @SpringBootTest to load only repository beans instead of
// the entire application context
@DataJpaTest
@ActiveProfiles("test")
public class AnimalRapositoryTest implements TestData {

    @Autowired
    private AnimalRepository animalRepository;

    @Test
    public void givenNothing_whenSaveAnimal_thenFindAnimalById() {
        Animal animal = Animal.builder()
            .id(1L)
            .name("Horse")
            .description("Fast")
            .enclosure("Barn")
            .species("race")
            .publicInformation("famous")
            .build();

        animalRepository.save(animal);

        assertAll(
            () -> assertNotNull(animalRepository.findById(animal.getId()))
        );
    }
}
