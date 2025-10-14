package ru.practicum.comments;

import org.springframework.stereotype.Service;
import ru.practicum.events.Event;
import ru.practicum.events.EventRepository;
import ru.practicum.events.Status;
import ru.practicum.exceptions.EntityNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final EventRepository eventRepository;

    public CommentService(CommentRepository commentRepository, EventRepository eventRepository) {
        this.commentRepository = commentRepository;
        this.eventRepository = eventRepository;
    }

    public Comment addComment(Long eventId, Long userId, String text) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event not found"));

        Comment comment = new Comment();
        comment.setEventId(eventId);
        comment.setUserId(userId);
        comment.setText(text);
        comment.setStatus(Status.PENDING);
        return commentRepository.save(comment);
    }

    public List<CommentDto> getApprovedCommentsForEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event not found"));
        //return commentRepository.findAllByEventAndStatus(event, Status.PUBLISHED);
        List<Comment> comments = commentRepository.findAllByEventIdAndStatus(event, Status.PUBLISHED);
        return comments.stream().map(this::toDto).collect(Collectors.toList());
    }


    public List<CommentDto> getCommentsForModeration() {

        return commentRepository.findAllByStatus(Status.PENDING).stream().map(this::toDto).collect(Collectors.toList());
    }

    public CommentDto moderateComment(Long commentId, boolean approve) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found"));

        comment.setStatus(approve ? Status.PUBLISHED : Status.REJECTED);
        return toDto(commentRepository.save(comment));
    }

    public CommentDto toDto(Comment comment) {
        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setEventId(comment.getEventId());
        dto.setUserId(comment.getUserId());
        dto.setText(comment.getText());
        dto.setStatus(comment.getStatus().name());
        dto.setCreatedAt(comment.getCreatedAt());
        return dto;
    }
}
