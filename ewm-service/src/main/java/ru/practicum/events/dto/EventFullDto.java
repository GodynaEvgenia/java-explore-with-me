package ru.practicum.events.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.category.CategoryDto;
import ru.practicum.events.LocationDto;
import ru.practicum.users.UserDto;

import java.time.LocalDateTime;

@Getter
@Setter
public class EventFullDto {
    private String annotation;
    private CategoryDto category;
    private int confirmedRequests;
    private String createdOn;
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    // private String eventDate;
    private LocalDateTime eventDate;
    private Long id;
    private UserDto initiator;
    private LocationDto location;
    private boolean paid;
    private int participantLimit;
    private String publishedOn;
    private boolean requestModeration;
    private String state; // предполагается, что есть поле состояния
    private String title;
    private int views;
}



