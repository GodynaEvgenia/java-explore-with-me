package ru.practicum.participationrequest;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long> {

    Optional<ParticipationRequest> findByEventAndRequester(Long eventId, Long requesterId);

    long countByEventAndStatus(Long eventId, String status);

    Optional<ParticipationRequest> findByIdAndRequester(Long id, Long requester);

    List<ParticipationRequest> findByRequester(Long requester);
}
