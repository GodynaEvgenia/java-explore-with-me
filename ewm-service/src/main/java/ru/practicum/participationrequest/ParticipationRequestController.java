package ru.practicum.participationrequest;

import org.springframework.web.bind.annotation.*;

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
    public ParticipationRequestFullDto createRequest(@PathVariable Long userId,
                                                     @RequestParam Long eventId) {
        ParticipationRequest request = requestService.createRequest(userId, eventId);
        return toDto(request);
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
}
