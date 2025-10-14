package ru.practicum.comments;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping()
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/event/{eventId}/comments/{userId}")
    public ResponseEntity<CommentDto> addComment(@PathVariable Long eventId,
                                                 @PathVariable Long userId,
                                                 @RequestBody NewCommentDto request) {
        Comment saved = commentService.addComment(eventId, userId, request.getText());
        return ResponseEntity.status(HttpStatus.CREATED).body(commentService.toDto(saved));
    }

    // Получить одобренные комментарии к событию
    @GetMapping("/event/{eventId}")
    public List<CommentDto> getApprovedComments(@PathVariable Long eventId) {
        List<CommentDto> comments = commentService.getApprovedCommentsForEvent(eventId);
        return comments;//comments.stream().map(this::commentService.toDto).collect(Collectors.toList());
    }

    @GetMapping("/moderation")
    public List<CommentDto> getCommentsForModeration() {
        List<CommentDto> comments = commentService.getCommentsForModeration();
        return comments;
    }

    @PostMapping("/moderation/{commentId}")
    public ResponseEntity<CommentDto> moderateComment(@PathVariable Long commentId,
                                                      @RequestParam boolean approve) {
        return ResponseEntity.ok(commentService.moderateComment(commentId, approve));

    }
}
