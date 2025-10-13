package ru.practicum.participationrequest;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.DateTimeUtils;


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
        this.created = request.getCreated().format(DateTimeUtils.DATE_TIME_FORMATTER);

    }

}
