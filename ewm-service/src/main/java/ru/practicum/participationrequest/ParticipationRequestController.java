package ru.practicum.participationrequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.exceptions.ErrorResponse;
import ru.practicum.exceptions.ResourceConflictException;

import java.util.List;

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
                    "CONFLICT"
            );
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        }
    }

    @PatchMapping("/users/{userId}/requests/{requestId}/cancel")
    public RequestDetailsDto cancelRequest(@PathVariable Long userId,
                                           @PathVariable Long requestId) {
        return requestService.cancelRequest(userId, requestId);
    }


    @GetMapping("/users/{userId}/requests")
    public List<RequestDetailsDto> getUserRequests(@PathVariable Long userId) {
        return requestService.getRequestsByUser(userId);
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
            return ResponseEntity.ok(request);
        } catch (ResourceConflictException ex) {
            ErrorResponse errorResponse = new ErrorResponse(
                    "CONFLICT",
                    "Conflict",
                    "Conflict"
            );
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        }
    }
}
