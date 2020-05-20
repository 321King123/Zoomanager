package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.types.TaskStatus;
import lombok.*;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnimalTaskDto {

    private Long id;

    @NotNull(message = "Title must not be null")
    @NotBlank(message = "Title must not be empty")
    private String title;

    @NotNull(message = "Description must not be null")
    @NotBlank(message = "Description must not be empty")
    private String description;

    @NotNull(message = "startTime must not be null")
    @FutureOrPresent(message = "Task cant start in the past")
    private LocalDateTime startTime;

    @NotNull(message = "Name must not be null")
    @FutureOrPresent(message = "Task cant end in the past")
    private LocalDateTime endTime;

    @NotNull(message = "Animal ID must not be null")
    private Long animalId;


    private String employeeName;

    @NotNull(message = "Task Status must not be null")
    private TaskStatus status;
}
