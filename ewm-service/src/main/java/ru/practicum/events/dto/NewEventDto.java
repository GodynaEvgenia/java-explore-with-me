package ru.practicum.events.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.events.LocationDto;

@Getter
@Setter
public class NewEventDto {

    @NotBlank
    @Size(min = 20, max = 2000)
    private String annotation; // Краткое описание события

    @NotNull
    private Long category; // id категории

    @NotBlank
    @Size(min = 20, max = 7000)
    private String description; // Полное описание события

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate; // Дата и время события

    @NotNull
    private LocationDto location; // Место проведения

    private Boolean paid = false; // Нужно ли оплачивать участие

    @Min(0)
    private Integer participantLimit = 0; // Ограничение на количество участников

    private Boolean requestModeration = true; // Нужна ли пре-модерация заявок

    @NotBlank
    @Size(min = 3, max = 120)
    private String title; // Заголовок события

}
