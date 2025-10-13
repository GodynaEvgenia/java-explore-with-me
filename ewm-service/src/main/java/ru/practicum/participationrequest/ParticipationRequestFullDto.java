package ru.practicum.participationrequest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class ParticipationRequestFullDto {
    private String created;
    private Long event;
    private Long id;
    private Long requester;
    private String status;

}
