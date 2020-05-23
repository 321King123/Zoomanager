package at.ac.tuwien.sepm.groupphase.backend.entity;

import lombok.*;

import javax.persistence.*;
import java.util.List;


@Getter
@Setter
@EqualsAndHashCode
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class EnclosureTask {

    @Id
    private Long id;

    @Column
    private boolean priority;

    @OneToOne
    @PrimaryKeyJoinColumn
    private Task task;

    @NonNull
    @ManyToOne(fetch= FetchType.LAZY)
    private Enclosure subject;

}
