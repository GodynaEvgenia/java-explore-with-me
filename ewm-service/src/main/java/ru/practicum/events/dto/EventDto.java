package ru.practicum.events.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.category.CategoryDto;
import ru.practicum.events.LocationDto;
import ru.practicum.users.UserDto;

import java.time.LocalDateTime;

@Getter
@Setter
public class EventDto {
    private Long id;
    @NotBlank
    @Size(max = 2000)
    private String annotation;

    @NotNull
    private CategoryDto category;

    @NotNull
    private UserDto user;

    @NotBlank
    @Size(max = 7000)
    private String description;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    @NotNull
    private LocationDto location;

    @NotNull
    private Boolean paid = false;

    @NotNull
    @Min(0)
    private Integer participantLimit = 0;

    @NotNull
    private Boolean requestModeration = true;

    @NotBlank
    @Size(max = 120)
    private String title;

}
