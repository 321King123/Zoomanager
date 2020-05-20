package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.AnimalTaskDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.AnimalTask;
import at.ac.tuwien.sepm.groupphase.backend.entity.Task;
import org.mapstruct.Mapper;

@Mapper
public interface TaskMapper {

    AnimalTaskDto animalTaskToAnimalTaskDto(AnimalTask animalTask);

    AnimalTask animalTaskDtoToAnimalTask(AnimalTaskDto animalTask);

    Task animalTaskToTask(AnimalTask animalTask);
}
