package ru.practicum.participationrequest;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParticipationRequestFullDto {
    private String created;
    private Long event;
    private Long id;
    private Long requester;
    private String status;

}
