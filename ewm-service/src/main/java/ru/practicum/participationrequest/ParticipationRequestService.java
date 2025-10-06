package ru.practicum.participationrequest;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.events.Event;
import ru.practicum.events.EventRepository;
import ru.practicum.events.Status;
import ru.practicum.exceptions.ResourceConflictException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Event with id=" + eventId + " was not found"
                ));
        long confirmedCount = requestRepository.countByEventAndStatus(eventId, "CONFIRMED");
        if ((long) event.getParticipantLimit() == confirmedCount && event.getParticipantLimit() > 0) {
            throw new ResourceConflictException("Превышен лимит участников");
        }
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
        /*if (pendingCount == 0) {
            request.setStatus("CONFIRMED");
        }*/
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
        request.setStatus("CANCELED");
        return requestRepository.save(request);
    }

    public List<ParticipationRequest> getRequestsByUser(Long userId) {
        return requestRepository.findByRequester(userId);
    }

    public List<ParticipationRequestFullDto> getRequestsByUserAndEvent(Long userId, Long eventId) {
        // Можно расширить, добавив фильтр по userId и eventId
        List<ParticipationRequest> requests = requestRepository.findByRequesterAndEvent(userId, eventId);
        return requests.stream()
                .map(req -> new ParticipationRequestFullDto(req.getStatus(), req.getEvent(), req.getId(), req.getRequester(), req.getStatus()))
                .collect(Collectors.toList());
    }

    public UpdateRequestsResponse updateRequestStatus(Long userId, Long eventId, List<Long> requestIds, String newStatus) {
        if (requestIds == null || requestIds.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request IDs обязательны");
        }

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Событие не найдено"));

        boolean moderationRequired = event.getRequestModeration();
        int participantLimit = event.getParticipantLimit();

        List<ParticipationRequest> requests = requestRepository.findAllById(requestIds);
        List<ParticipationRequest> requestedForUpdate = new ArrayList<>();

        // Проверка заявок
        for (ParticipationRequest request : requests) {
            /*if (!request.getRequester().equals(userId) || !request.getEvent().equals(eventId)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Заявка не принадлежит пользователю или событию");
            }*/
            if (!request.getStatus().equals("PENDING")) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Можно изменять только заявки в статусе Ожидается");
            }
            requestedForUpdate.add(request);
        }

        List<ParticipationRequest> confirmedRequests = new ArrayList<>();
        List<ParticipationRequest> rejectedRequests = new ArrayList<>();

        // Обработка заявок
        for (ParticipationRequest request : requestedForUpdate) {
            if (newStatus.equalsIgnoreCase("CONFIRMED")) {
                // Проверка лимита
                if (participantLimit > 0 && moderationRequired) {
                    long confirmedCount = requestRepository.countByEventAndStatus(eventId, "CONFIRMED");
                    if (confirmedCount >= participantLimit) {
                        throw new ResponseStatusException(HttpStatus.CONFLICT, "Достигнут лимит участников");
                    }
                }
                request.setStatus("CONFIRMED");
                confirmedRequests.add(request);
            } else if (newStatus.equalsIgnoreCase("REJECTED")) {
                request.setStatus("REJECTED");
                rejectedRequests.add(request);
            } else if (newStatus.equalsIgnoreCase("CANCELED")) {
                request.setStatus("CANCELED");
                rejectedRequests.add(request);
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Недопустимый статус");
            }
        }

        requestRepository.saveAll(requestedForUpdate);

        // После подтверждения, обработка лимита
        if (newStatus.equalsIgnoreCase("CONFIRMED")) {
            long confirmedCount = requestRepository.countByEventAndStatus(eventId, "CONFIRMED");
            if (participantLimit > 0 && confirmedCount >= participantLimit) {
                List<ParticipationRequest> pendingRequests = requestRepository
                        .findByEventAndStatus(eventId, "PENDING");
                for (ParticipationRequest pending : pendingRequests) {
                    pending.setStatus("CANCELED");
                }
                requestRepository.saveAll(pendingRequests);
            }
        }

        // Создаем и возвращаем ответ
        UpdateRequestsResponse response = new UpdateRequestsResponse();

        response.setConfirmedRequests(
                confirmedRequests.stream()
                        .map(RequestDetailsDto::new)
                        .collect(Collectors.toList())
        );

        response.setRejectedRequests(
                rejectedRequests.stream()
                        .map(RequestDetailsDto::new)
                        .collect(Collectors.toList())
        );

        return response;

    }

    private ParticipationRequestFullDto toDto(ParticipationRequest request) {
        ParticipationRequestFullDto dto = new ParticipationRequestFullDto();
        dto.setCreated(request.getCreated().toString());
        dto.setEvent(request.getEvent());
        dto.setId(request.getId());
        dto.setRequester(request.getRequester());
        dto.setStatus(request.getStatus());
        return dto;
    }

}
