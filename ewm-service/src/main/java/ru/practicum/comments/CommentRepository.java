package ru.practicum.comments;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.events.Status;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByEventIdAndStatus(Long eventId, Status status);

    List<Comment> findAllByStatus(Status status);
}
