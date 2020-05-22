package at.ac.tuwien.sepm.groupphase.backend.entity;

import lombok.*;

import java.util.LinkedList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.*;
import java.io.Serializable;

@Entity
@Getter
@Setter
@EqualsAndHashCode(exclude = {"caretakers"})
@ToString(exclude = {"caretakers"})
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Animal implements Serializable {

    @Column(nullable = false)
    private String name;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String species;

    @ManyToOne
    private Enclosure enclosure;

    @Column
    private String publicInformation;

    @ManyToMany(mappedBy = "assignedAnimals",fetch = FetchType.EAGER)
    private List<Employee> caretakers;
}
