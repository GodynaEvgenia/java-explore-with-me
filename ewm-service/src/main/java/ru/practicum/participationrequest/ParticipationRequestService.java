package ru.practicum.participationrequest;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.events.Event;
import ru.practicum.events.EventRepository;
import ru.practicum.events.Status;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ParticipationRequestService {

    private final ParticipationRequestRepository requestRepository;
    private final EventRepository eventRepository;

    private static final int MAX_REQUESTS_PER_EVENT = 100; // лимит

    public ParticipationRequestService(ParticipationRequestRepository requestRepository,
                                       EventRepository eventRepository) {
        this.requestRepository = requestRepository;
        this.eventRepository = eventRepository;
    }

    public ParticipationRequest createRequest(Long userId, Long eventId) {
        // Проверка существования события
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Event with id=" + eventId + " was not found"
                ));

        // Проверка, что инициатор не подает заявку на своё событие
        if (event.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Initiator cannot request participation in their own event");
        }

        // Проверка статуса события
        if (!Status.PUBLISHED.equals(event.getState())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Cannot request participation in an unpublished event");
        }

        // Проверка, что заявка ещё не создана
        if (requestRepository.findByEventAndRequester(eventId, userId).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Request already exists");
        }

        // Проверка лимита
        long pendingCount = requestRepository.countByEventAndStatus(eventId, "PENDING");
        if (pendingCount >= MAX_REQUESTS_PER_EVENT) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Maximum requests reached");
        }

        // Создаем заявку
        ParticipationRequest request = new ParticipationRequest();
        request.setEvent(eventId);
        request.setRequester(userId);
        request.setCreated(LocalDateTime.now());
        if (Boolean.TRUE.equals(event.getRequestModeration())) {
            request.setStatus("PENDING");
        } else {
            request.setStatus("CONFIRMED");
        }

        return requestRepository.save(request);
    }

    public ParticipationRequest cancelRequest(Long userId, Long requestId) {
        ParticipationRequest request = requestRepository.findByIdAndRequester(requestId, userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Request not found"
                ));

        // Если заявка уже отменена или подтверждена, можно оставить логику или запретить
        // Допустим, отменяем любую заявку
        request.setStatus("CANCELLED");
        return requestRepository.save(request);
    }

    public List<ParticipationRequest> getRequestsByUser(Long userId) {
        return requestRepository.findByRequester(userId);
    }
}
