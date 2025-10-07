package ru.practicum.participationrequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.exceptions.ErrorResponse;
import ru.practicum.exceptions.ResourceConflictException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping()
public class ParticipationRequestController {

    private final ParticipationRequestService requestService;

    public ParticipationRequestController(ParticipationRequestService requestService) {
        this.requestService = requestService;
    }

    @PostMapping("/users/{userId}/requests")
    public ResponseEntity<?> createRequest(@PathVariable Long userId,
                                           @RequestParam Long eventId) {
        try {
            RequestDetailsDto request = requestService.createRequest(userId, eventId);
            return ResponseEntity.status(HttpStatus.CREATED).body(request);
        } catch (ResourceConflictException ex) {

            ErrorResponse errorResponse = new ErrorResponse(
                    "CONFLICT",
                    "Incorrectly made request.",
                    " "
            );
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        }

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

    @PatchMapping("/users/{userId}/requests/{requestId}/cancel")
    public ParticipationRequestFullDto cancelRequest(@PathVariable Long userId,
                                                     @PathVariable Long requestId) {
        ParticipationRequest request = requestService.cancelRequest(userId, requestId);
        return toDto(request);
    }


    @GetMapping("/users/{userId}/requests")
    public List<ParticipationRequestFullDto> getUserRequests(@PathVariable Long userId) {
        List<ParticipationRequest> requests = requestService.getRequestsByUser(userId);
        return requests.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/users/{userId}/events/{eventId}/requests")
    public ResponseEntity<List<ParticipationRequestFullDto>> getRequests(
            @PathVariable Long userId,
            @PathVariable Long eventId) {
        List<ParticipationRequestFullDto> requests = requestService.getRequestsByUserAndEvent(userId, eventId);
        return ResponseEntity.ok(requests);
    }

    @PatchMapping("/users/{userId}/events/{eventId}/requests")
    public ResponseEntity<?> updateRequestStatus(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @RequestBody UpdateRequestDto dto) {
        try {
            UpdateRequestsResponse request = requestService.updateRequestStatus(userId, eventId, dto.getRequestIds(), dto.getStatus());
            return ResponseEntity.ok(request);//.build();
        } catch (ResourceConflictException ex) {
            ErrorResponse errorResponse = new ErrorResponse(
                    "BAD_REQUEST",
                    "Incorrectly made request.",
                    "Failed to convert value of type java.lang.String to required type int;"
            );
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        }
    }
}
