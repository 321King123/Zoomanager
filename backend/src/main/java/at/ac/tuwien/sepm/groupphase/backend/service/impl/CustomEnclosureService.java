package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.Enclosure;
import at.ac.tuwien.sepm.groupphase.backend.repository.AnimalRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.EnclosureRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.EnclosureService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.List;

@Service
public class CustomEnclosureService implements EnclosureService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private EnclosureRepository enclosureRepository;
    private AnimalRepository animalRepository;

    @Autowired
    public CustomEnclosureService(EnclosureRepository enclosureRepository, AnimalRepository animalRepository) {
        this.enclosureRepository = enclosureRepository;
        this.animalRepository = animalRepository;
    }

    @Override
    public List<Enclosure> getAll() {
        LOGGER.debug("Get List of Enclosures");
        return enclosureRepository.findAll();
    }

    @Override
    public Enclosure findById(long id) {
        LOGGER.debug("Getting specific enclosure with id: {}", id);
        return enclosureRepository.findById(id);
    }

    @Override
    public Enclosure create(Enclosure enclosure) {
        LOGGER.debug("Creating new Enclosure");
        if(enclosure == null) {
            throw new IllegalArgumentException("Enclosure must not be null");
        } else if(enclosure.getName() == null || enclosure.getName().isBlank()) {
            throw new IllegalArgumentException("Name of Enclosure must not be empty");
        } else if(enclosure.getPicture() != null &&
            !new String(enclosure.getPicture()).matches("^data:image/(jpeg|png);base64,([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)?$")) {
            throw new IllegalArgumentException("Picture has to be valid jpeg or png image");
        }
        return enclosureRepository.save(enclosure);
    }

    @Override
    public Enclosure findByAnimalId(long animalId) {
        LOGGER.debug("Find Enclosure of Animal with Id: {}", animalId);
        return animalRepository.findById(animalId).getEnclosure();
    }
}
