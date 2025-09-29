package ru.practicum.events;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import ru.practicum.events.dto.EventDto;
import ru.practicum.events.dto.EventFullDto;
import ru.practicum.events.dto.EventUpdateDto;
import ru.practicum.events.dto.NewEventDto;
import ru.practicum.exceptions.EntityNotFoundException;

import java.util.List;

@RestController
@RequestMapping()
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping("/users/{userId}/events")
    public ResponseEntity<Event> addEvent(
            @PathVariable Long userId,
            @Valid @RequestBody NewEventDto eventDto) {
            Event createdEvent = eventService.addEvent(userId, eventDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdEvent);


    }

    @GetMapping("/users/{userId}/events")
    public List<EventDto> getUserEvents(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size) {
        return eventService.getEventsByUser(userId, from, size);
    }

    @GetMapping("/users/{userId}/events/{eventId}")
    public ResponseEntity<EventFullDto> getEventDetails(@PathVariable Long userId,
                                                        @PathVariable Long eventId) {
        try {
            EventFullDto eventDto = eventService.getEventDetails(userId, eventId);
            return ResponseEntity.ok(eventDto);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PatchMapping("/users/{userId}/events/{eventId}")
    public EventFullDto updateEvent(@PathVariable Long userId,
                                    @PathVariable Long eventId,
                                    @RequestBody EventUpdateDto updateDto) {
        return eventService.updateEvent(userId, eventId, updateDto);
        //return convertToDto(updatedEvent);
    }
}
