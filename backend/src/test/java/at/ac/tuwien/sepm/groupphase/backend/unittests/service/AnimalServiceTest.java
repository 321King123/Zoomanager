package at.ac.tuwien.sepm.groupphase.backend.unittests.service;
import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.entity.Animal;
import at.ac.tuwien.sepm.groupphase.backend.entity.Enclosure;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.AnimalRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.EnclosureRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.AnimalService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
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

    @MockBean
    EnclosureRepository enclosureRepository;

    private Animal animal = Animal.builder()
        .id(2L)
        .name("Brandy")
        .description("racing Horce")
        .enclosure(null)
        .species("race")
        .publicInformation(null)
        .build();

    private Enclosure enclosureDetailed = Enclosure.builder()
        .name(NAME_LION_ENCLOSURE)
        .description(DESCRIPTION_LION_ENCLOSURE)
        .publicInfo(PUBLIC_INFO_LION_ENCLOSURE)
        .picture(PICTURE_LION_ENCLOSURE)
        .build();

    @Test
    public void saveAnimalbyGivingOnlyMandatoryValues_returnsAnimal() {

        Mockito.when(animalRepository.save(animal)).thenReturn(animal);

        assertAll(
            () -> assertEquals(animalService.saveAnimal(animal),animal)
        );
    }

    @Test
    public void findAnimalById_returnsAnimal() {

        Mockito.when(animalRepository.findById((Long)1L)).thenReturn(java.util.Optional.ofNullable(animal));

        assertAll(
            () -> assertEquals(animal, animalService.findAnimalById(1L))
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

    @Test
    public void whenDeletingNotExistingAnimal_thenNotFoundException(){
        animalRepository.deleteAll();
        Mockito.when(animalRepository.findById(1L)).thenReturn(null);
        assertThrows(NotFoundException.class, ()->{animalService.deleteAnimal(1L);});
    }

    @Test
    public void whenAddAnimalToEnclosure_returnAnimalWithNewAttributes() {
        Mockito.when(animalRepository.findById(2L)).thenReturn(animal);
        Mockito.when(enclosureRepository.findById(1L)).thenReturn(enclosureDetailed);

        Animal newAnimal = Animal.builder()
            .id(animal.getId())
            .name(animal.getName())
            .description(animal.getDescription())
            .enclosure(enclosureDetailed)
            .species(animal.getSpecies())
            .publicInformation(animal.getPublicInformation())
            .build();

        Mockito.when(animalRepository.save(animal)).thenReturn(newAnimal);

        Animal serviceAnimal = animalService.addAnimalToEnclosure(animal, 1L);
        assertAll(
            () -> assertEquals(animal, serviceAnimal),
            () -> assertEquals(enclosureDetailed, serviceAnimal.getEnclosure())
        );
    }

    @Test
    public void addAnimalToEnclosure_whenNoAnimalExists_returnNotFoundException() {
        Mockito.when(animalRepository.findById(2L)).thenReturn(null);
        Mockito.when(enclosureRepository.findById(1L)).thenReturn(enclosureDetailed);

        assertThrows(NotFoundException.class, () -> animalService.addAnimalToEnclosure(animal, 1L));
    }

    @Test
    public void addAnimalToEnclosure_whenNoEnclosureExists_returnNotFoundException() {
        Mockito.when(animalRepository.findById(2L)).thenReturn(animal);
        Mockito.when(enclosureRepository.findById(1L)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> animalService.addAnimalToEnclosure(animal, 1L));
    }

    @Test
    public void removeAnimalFromEnclosure_whenNoAnimalExists_returnNotFoundException() {
        Mockito.when(animalRepository.findById(2L)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> animalService.removeAnimalFromEnclosure(animal));
    }

    @Test
    public void removeAnimalFromEnclosure_returnAnimalWithoutEnclosure() {
        Animal newAnimal = Animal.builder()
            .id(animal.getId())
            .name(animal.getName())
            .description(animal.getDescription())
            .enclosure(enclosureDetailed)
            .species(animal.getSpecies())
            .publicInformation(animal.getPublicInformation())
            .build();

        Mockito.when(animalRepository.findById(2L)).thenReturn(newAnimal);
        Mockito.when(animalRepository.save(animal)).thenReturn(animal);

        Animal serviceAnimal = animalService.removeAnimalFromEnclosure(newAnimal);
        assertAll(
            () -> assertEquals(animal, serviceAnimal),
            () -> assertNull(serviceAnimal.getEnclosure())
        );
    }

    @Test
    public void findAnimalsByEnclosure_whenNoEnclosure_returnNotFoundException() {
        Mockito.when(enclosureRepository.findById(1L)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> animalService.findAnimalsByEnclosure(1L));
    }

    @Test
    public void findAnimalsByEnclosure_returnAnimalsInList() {
        Mockito.when(enclosureRepository.findById(1L)).thenReturn(enclosureDetailed);

        Mockito.when(animalRepository.findAllByEnclosure(enclosureDetailed)).thenReturn(Collections.singletonList(animal));

        assertTrue(animalService.findAnimalsByEnclosure(1L).contains(animal));
    }
}
