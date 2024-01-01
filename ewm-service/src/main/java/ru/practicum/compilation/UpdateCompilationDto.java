package ru.practicum.compilation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UpdateCompilationDto {
    @NotNull
    @NotBlank
    @Size(max = 50)
    private String title;
    private Boolean pinned;
    private List<Long> events;

}
