package ru.practicum.comments;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewCommentDto {
    @Size(max = 2000)
    private String text;
}
