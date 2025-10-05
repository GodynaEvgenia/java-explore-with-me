package ru.practicum.events.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.events.LocationDto;

@Getter
@Setter
public class EventUpdateDto {
    @NotBlank
    @Size(min = 20, max = 2000)
    private String annotation;
    private Long category;
    @NotBlank
    @Size(min = 20, max = 7000)
    private String description;
    private String eventDate; // строка в формате "yyyy-MM-dd HH:mm:ss"
    private LocationDto location;
    private Boolean paid;
    @Positive
    private Integer participantLimit;
    private Boolean requestModeration;
    private String stateAction;
    @NotBlank
    @Size(min = 3, max = 120)
    private String title;

}
