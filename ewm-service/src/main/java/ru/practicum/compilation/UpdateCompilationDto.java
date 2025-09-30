package ru.practicum.compilation;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UpdateCompilationDto {
    private String title;
    private Boolean pinned;
    private List<Long> events;

}
