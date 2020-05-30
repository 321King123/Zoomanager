package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CombinedTaskDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.AnimalTask;
import at.ac.tuwien.sepm.groupphase.backend.entity.EnclosureTask;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

@Component
public class CombinedTaskMapper {

    public CombinedTaskDto animalTaskToCombinedTaskDto(AnimalTask animalTask){
        if(animalTask == null) return null;

        return CombinedTaskDto.builder()
            .id(animalTask.getId())
            .title(animalTask.getTask().getTitle())
            .description(animalTask.getTask().getDescription())
            .startTime(animalTask.getTask().getStartTime())
            .endTime(animalTask.getTask().getEndTime())
            .assignedEmployeeUsername(animalTask.getTask().getAssignedEmployee()==null?null:animalTask.getTask().getAssignedEmployee().getUsername())
            .status(animalTask.getTask().getStatus())
            .subjectName(animalTask.getSubject().getName())
            .subjectId(animalTask.getSubject().getId())
            .isAnimalTask(true)
            .priority(animalTask.getTask().isPriority())
            .build();
    }

    public List<CombinedTaskDto> animalTaskListToCombinedTaskDtoList(List<AnimalTask> animalTasks){
        List<CombinedTaskDto> combinedTaskDtos = new LinkedList<>();
        for(AnimalTask a: animalTasks){
            combinedTaskDtos.add(animalTaskToCombinedTaskDto(a));
        }
        return combinedTaskDtos;
    }

    public CombinedTaskDto enclosureTaskToCombinedTaskDto(EnclosureTask enclosureTask) {
        if (enclosureTask == null) return null;

        return CombinedTaskDto.builder()
            .id(enclosureTask.getId())
            .title(enclosureTask.getTask() != null ? enclosureTask.getTask().getTitle() : null)
            .description(enclosureTask.getTask().getDescription())
            .startTime(enclosureTask.getTask().getStartTime())
            .endTime(enclosureTask.getTask().getEndTime())
            .assignedEmployeeUsername(enclosureTask.getTask().getAssignedEmployee() == null ? null : enclosureTask.getTask().getAssignedEmployee().getUsername())
            .status(enclosureTask.getTask().getStatus())
            .subjectName(enclosureTask.getSubject() != null ? enclosureTask.getSubject().getName() : null)
            .subjectId(enclosureTask.getSubject() != null ? enclosureTask.getSubject().getId() : null)
            .isAnimalTask(false)
            .priority(enclosureTask.getTask().isPriority())
            .build();
    }

    public List<CombinedTaskDto> enclosureTaskListToCombinedTaskDtoList(List<EnclosureTask> enclosureTasks){
        List<CombinedTaskDto> combinedTaskDtos = new LinkedList<>();
        for(EnclosureTask e: enclosureTasks){
            combinedTaskDtos.add(enclosureTaskToCombinedTaskDto(e));
        }
        return combinedTaskDtos;
    }

    public List<CombinedTaskDto> sortedEnclosureTaskListAndAnimalTaskListToSortedCombinedTaskDtoList(List<EnclosureTask> enclosureTasks, List<AnimalTask> animalTasks){
        List<CombinedTaskDto> combinedTaskDtos = new LinkedList<>();

        List<CombinedTaskDto> fromEnclosureTasks = enclosureTaskListToCombinedTaskDtoList(enclosureTasks);
        List<CombinedTaskDto> fromAnimalTasks = animalTaskListToCombinedTaskDtoList(animalTasks);

        if(fromEnclosureTasks.size() == 0)
            return fromAnimalTasks;

        if(fromAnimalTasks.size() == 0)
            return fromEnclosureTasks;

        Iterator<CombinedTaskDto> combinedTaskDtoListIterator1 = fromEnclosureTasks.listIterator();
        Iterator<CombinedTaskDto> combinedTaskDtoListIterator2 = fromAnimalTasks.listIterator();

        CombinedTaskDto task1 = combinedTaskDtoListIterator1.next();
        CombinedTaskDto task2 = combinedTaskDtoListIterator2.next();

        while(task1 != null && task2 != null){
            if(task1.getStartTime().isBefore(task2.getStartTime())){
                combinedTaskDtos.add(task1);
                if(combinedTaskDtoListIterator1.hasNext()) {
                    task1 = combinedTaskDtoListIterator1.next();
                }else{
                    task1 = null;
                }
            }else{
                combinedTaskDtos.add(task2);
                if(combinedTaskDtoListIterator2.hasNext()) {
                    task2 = combinedTaskDtoListIterator2.next();
                }else{
                    task2 = null;
                }
            }
        }

        while(task1 != null){
            combinedTaskDtos.add(task1);
            if(combinedTaskDtoListIterator1.hasNext()) {
                task1 = combinedTaskDtoListIterator1.next();
            }else{
                task1 = null;
            }
        }

        while(task2 != null){
            combinedTaskDtos.add(task2);
            if(combinedTaskDtoListIterator2.hasNext()) {
                task2 = combinedTaskDtoListIterator2.next();
            }else{
                task2 = null;
            }
        }
        return combinedTaskDtos;
    }
}
