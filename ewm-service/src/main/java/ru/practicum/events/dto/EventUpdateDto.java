package ru.practicum.events.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.events.LocationDto;

@Getter
@Setter
public class EventUpdateDto {
    private String annotation;
    private Long category;
    private String description;
    private String eventDate; // строка в формате "yyyy-MM-dd HH:mm:ss"
    private LocationDto location;
    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;
    private String stateAction; // например, "CANCEL_REVIEW", "SEND_TO_REVIEW"
    private String title;

}
