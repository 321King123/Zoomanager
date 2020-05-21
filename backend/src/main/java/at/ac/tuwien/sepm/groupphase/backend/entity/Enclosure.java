package at.ac.tuwien.sepm.groupphase.backend.entity;

import lombok.*;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.*;

@Entity
@Getter
@Setter
@EqualsAndHashCode(exclude = "animals")
@ToString(exclude = "animals")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Enclosure {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Lob
    @Column
    private String description;

    @Lob
    @Column
    private String publicInfo;

    @Lob
    @Column
    private byte[] picture;

    @OneToMany(mappedBy = "enclosure")
    private List<Animal> animals;
}

