package ru.practicum.compilation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class CreateCompilationDto {
    @NotBlank
    @NotNull
    @Size(max = 50)
    private String title;
    private Boolean pinned;
    private List<Long> events;
}
