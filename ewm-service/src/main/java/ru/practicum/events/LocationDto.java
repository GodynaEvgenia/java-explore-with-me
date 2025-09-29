package ru.practicum.events;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LocationDto {
    @NotNull
    private Float lat;

    @NotNull
    private Float lon;

}
