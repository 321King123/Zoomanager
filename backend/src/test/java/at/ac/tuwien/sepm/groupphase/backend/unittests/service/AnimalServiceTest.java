package at.ac.tuwien.sepm.groupphase.backend.unittests.service;
import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.entity.Animal;
import at.ac.tuwien.sepm.groupphase.backend.repository.AnimalRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.AnimalService;
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

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class AnimalServiceTest implements TestData {

    @Autowired
    AnimalService animalService;

    @MockBean
    AnimalRepository animalRepository;

    private Animal animal = Animal.builder()
        .id(2L)
        .name("Brandy")
        .description("racing Horce")
        .enclosure(null)
        .species("race")
        .publicInformation(null)
        .build();

    @Test
    public void saveAnimalbyGivingOnlyMandatoryValues_thenFindAnimalById() {

        Mockito.when(animalRepository.save(animal)).thenReturn(animal);

        assertAll(
            () -> assertEquals(animalService.saveAnimal(animal),animal)
        );
    }

    @Test
    public void emptyRepository_whenFindAll_thenEmptyList() {
        animalRepository.deleteAll();
        List<Animal> animalTest=new LinkedList<>();
        Mockito.when(animalRepository.findAll()).thenReturn(animalTest);
        List<Animal> animals = animalRepository.findAll();
        assertEquals(0, animals.size());
    }

    @Test
    public void ReturnsOneAnimal_whenSaveAnimal_thenFindAll_() {
        animalRepository.deleteAll();
        List<Animal> animalTest=new LinkedList<>();
        animalTest.add(animal);
        Mockito.when(animalRepository.findAll()).thenReturn(animalTest);

        List<Animal> animals = animalRepository.findAll();
        assertEquals(1, animals.size());
    }


}
