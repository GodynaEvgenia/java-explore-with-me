package ru.practicum.comments;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CommentDto {
    private Long id;
    private Long eventId;
    private Long userId;
    private String text;
    private String status;
    private LocalDateTime createdAt;
}
