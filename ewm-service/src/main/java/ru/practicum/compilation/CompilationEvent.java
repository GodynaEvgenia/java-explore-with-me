package ru.practicum.compilation;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "compilation_event")
public class CompilationEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // @ManyToOne
    // @JoinColumn(name = "selection_id")
    // private EventSelection selection;

    @ManyToOne
    @JoinColumn(name = "compilation_id")
    private Compilation compilation;

    //private Long compilation_id;
    private Long eventId;
}
