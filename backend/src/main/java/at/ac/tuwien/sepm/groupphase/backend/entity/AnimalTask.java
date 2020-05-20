package at.ac.tuwien.sepm.groupphase.backend.entity;

import at.ac.tuwien.sepm.groupphase.backend.types.TaskStatus;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@EqualsAndHashCode
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnimalTask {

    @Id
    private Long id;

    @OneToOne
    @PrimaryKeyJoinColumn
    private Task task;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @NonNull
    @ManyToOne(fetch=FetchType.LAZY)
    private Animal subject;

    @ManyToOne(fetch=FetchType.LAZY)
    private Employee assignedEmployee;

    @Column(nullable = false)
    private TaskStatus status;
}
