package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.AnimalDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.DetailedMessageDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.MessageInquiryDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.AnimalMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.MessageMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Animal;
import at.ac.tuwien.sepm.groupphase.backend.service.AnimalService;
import at.ac.tuwien.sepm.groupphase.backend.service.MessageService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.lang.invoke.MethodHandles;

@RestController
@RequestMapping(value = "/api/v1/animals")
public class AnimalEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final AnimalService animalService;
    private final AnimalMapper animalMapper;

    @Autowired
    public AnimalEndpoint(AnimalService animalService, AnimalMapper animalMapper) {
        this.animalService = animalService;
        this.animalMapper = animalMapper;
    }


    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public AnimalDto create(@Valid @RequestBody AnimalDto animalDto) {
        LOGGER.info("POST /api/v1/authentication/animal body: {}", animalDto);
        Animal animal1 = animalMapper.AnimalDtoToAnimal(animalDto);
        return animalMapper.animalToAnimalDto(animalService.saveAnimal(animal1));
    }
}
