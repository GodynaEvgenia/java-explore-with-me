package ru.practicum.participationrequest;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class RequestDetailsDto {
    private Long id;
    private Long event;
    private Long requester;
    private String status;
    private String created;

    public RequestDetailsDto(ParticipationRequest request) {
        this.id = request.getId();
        this.event = request.getEvent();
        this.requester = request.getRequester();
        this.status = request.getStatus();
        this.created = request.getCreated().toString().substring(0, 26); // dateStr.substring(0, 26); или форматировать дату по необходимости
    }

}
