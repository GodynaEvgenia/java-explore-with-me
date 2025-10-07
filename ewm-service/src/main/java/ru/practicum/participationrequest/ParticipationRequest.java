package ru.practicum.participationrequest;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.DateTimeUtils;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "participation_request")
public class ParticipationRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
    private LocalDateTime created;

    private Long event;

    private Long requester;

    private String status;

}
