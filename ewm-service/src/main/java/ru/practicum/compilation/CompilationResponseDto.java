package ru.practicum.compilation;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ru.practicum.events.dto.EventDto;

import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class CompilationResponseDto {
    private Long id;
    private String title;
    private Boolean pinned;
    private List<EventDto> events;
}
