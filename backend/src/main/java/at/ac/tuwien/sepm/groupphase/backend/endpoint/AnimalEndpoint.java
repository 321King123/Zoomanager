package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.AnimalDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.AnimalMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Animal;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotAuthorisedException;
import at.ac.tuwien.sepm.groupphase.backend.service.AnimalService;
import at.ac.tuwien.sepm.groupphase.backend.service.EmployeeService;
import at.ac.tuwien.sepm.groupphase.backend.service.TaskService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.lang.invoke.MethodHandles;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/animals")
public class AnimalEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final AnimalService animalService;
    private final TaskService taskService;
    private final AnimalMapper animalMapper;
    private final EmployeeService employeeService;

    @Autowired
    public AnimalEndpoint(AnimalService animalService, AnimalMapper animalMapper, TaskService taskService, EmployeeService employeeService) {
        this.taskService = taskService;
        this.animalService = animalService;
        this.animalMapper = animalMapper;
        this.employeeService = employeeService;
    }

    @Secured("ROLE_ADMIN")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public AnimalDto create(@Valid @RequestBody AnimalDto animalDto) {
        LOGGER.info("POST /api/v1/authentication/animal body: {}", animalDto);
        Animal animal1 = animalMapper.AnimalDtoToAnimal(animalDto);
        return animalMapper.animalToAnimalDto(animalService.saveAnimal(animal1));
    }


    @Secured("ROLE_ADMIN")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    @ApiOperation(value = "Get list of animals without details", authorizations = {@Authorization(value = "apiKey")})
    public List<AnimalDto> getAllAnimals() {
        LOGGER.info("GET /api/v1/animals");
        List<Animal> animals = animalService.getAll();
        List<AnimalDto> animalsDto = new LinkedList<>();
        for (Animal a : animals) {
            animalsDto.add(animalMapper.animalToAnimalDto(a));
        }
        return animalsDto;
    }

    @Secured("ROLE_ADMIN")
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void deleteAnimal(@PathVariable("id") Long id) {
        LOGGER.info("DELETE /api/v1/authentication/animal/" + id);
        taskService.deleteAnimalTasksBelongingToAnimal(id);
        animalService.deleteAnimal(id);
    }


    @Secured("ROLE_USER")
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public AnimalDto getAnimalById(@PathVariable("id") Long id, Authentication authentication) {
        LOGGER.info("GET /api/v1/animal/" + id);
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        boolean isAdmin = authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
        String username = (String) authentication.getPrincipal();
        if (!isAdmin) {
            if (!employeeService.isAssignedToAnimal(username, id)) {
                throw new NotAuthorisedException("You are not allowed to see this animals information.");
            }
        }
        return animalMapper.animalToAnimalDto(animalService.findAnimalById(id));
    }


    @Secured("ROLE_ADMIN")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/enclosure/{enclosureId}")
    @ApiOperation(value = "Get Animals assigned to Enclosure",
        authorizations = {@Authorization(value = "apiKey")})
    public List<AnimalDto> getAnimalsByEnclosure(@PathVariable Long enclosureId) {
        LOGGER.info("GET /api/v1/animals/enclosure/{}", enclosureId);
        List<Animal> animals = animalService.findAnimalsByEnclosure(enclosureId);
        List<AnimalDto> animalDtos = new LinkedList<>();
        for(Animal a: animals) {
            animalDtos.add(animalMapper.animalToAnimalDto(a));
        }
        return animalDtos;
    }

    @Secured("ROLE_ADMIN")
    @ResponseStatus(HttpStatus.OK)
    @PutMapping(value = "/removeEnclosure")
    @ApiOperation(value = "Remove animal from enclosure", authorizations = {@Authorization(value = "apiKey")})
    public void removeEnclosure(@RequestBody @Valid AnimalDto animalDto){
        LOGGER.info("PUT /api/v1/animals/removeEnclosure body: {}",animalDto);
        animalService.removeAnimalFromEnclosure(animalMapper.AnimalDtoToAnimal(animalDto));
    }

}
