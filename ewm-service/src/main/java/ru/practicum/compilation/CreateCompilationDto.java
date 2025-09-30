package ru.practicum.compilation;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class CreateCompilationDto {
    private String title;            // Название подборки
    private Boolean pinned;          // Закрепленная или нет
    private List<Long> events;
}
