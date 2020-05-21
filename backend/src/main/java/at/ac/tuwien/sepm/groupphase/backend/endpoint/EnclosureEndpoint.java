package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.EnclosureDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.EnclosureMapper;
import at.ac.tuwien.sepm.groupphase.backend.repository.EnclosureRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.EnclosureService;
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
@RequestMapping("/api/v1/enclosure")
public class EnclosureEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final EnclosureService enclosureService;
    private final EnclosureMapper enclosureMapper;
    private final EnclosureRepository enclosureRepository;

    @Autowired
    public EnclosureEndpoint(EnclosureService enclosureService, EnclosureMapper enclosureMapper, EnclosureRepository enclosureRepository){

        this.enclosureService = enclosureService;
        this.enclosureMapper = enclosureMapper;
        this.enclosureRepository = enclosureRepository;
    }

    @Secured("ROLE_ADMIN")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "")
    @ApiOperation(value = "Create new enclosure", authorizations = {@Authorization(value = "apiKey")})
    public EnclosureDto createEnclosure(@RequestBody @Valid EnclosureDto enclosureDto){
        LOGGER.info("POST /api/v1/enclosure body: {}",enclosureDto);

        return enclosureMapper.enclosureToEnclosureDto(enclosureService.create(enclosureMapper.enclosureDtoToEnclosure(enclosureDto)));
    }
}
