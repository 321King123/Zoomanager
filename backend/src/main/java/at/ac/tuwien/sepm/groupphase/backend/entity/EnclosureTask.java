package at.ac.tuwien.sepm.groupphase.backend.entity;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@Entity
@Getter
@Setter
@EqualsAndHashCode
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class EnclosureTask {

    @Id
    private Long id;

    @Column
    private boolean priority;

    @OneToOne
    @PrimaryKeyJoinColumn
    private Task task;


    @NonNull
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Enclosure subject;

}
