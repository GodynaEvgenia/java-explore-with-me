package ru.practicum.compilation;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "compilations")
public class Compilation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private Boolean pinned;

    // @OneToMany(mappedBy = "selection", cascade = CascadeType.ALL, orphanRemoval = true)
    // private List<EventSelectionEvent> eventLinks = new ArrayList<>();


}
