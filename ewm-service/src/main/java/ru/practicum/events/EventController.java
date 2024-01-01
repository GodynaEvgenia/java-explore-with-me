package ru.practicum.events;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.events.dto.EventDto;
import ru.practicum.events.dto.EventFullDto;
import ru.practicum.events.dto.EventUpdateDto;
import ru.practicum.events.dto.NewEventDto;
import ru.practicum.exceptions.EntityNotFoundException;
import ru.practicum.stats.StatsClient;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping()
public class EventController {

    private final EventService eventService;
    private final StatsClient statsClient;

    public EventController(EventService eventService,
                           StatsClient statsClient) {
        this.eventService = eventService;
        this.statsClient = statsClient;
    }

    @PostMapping("/users/{userId}/events")
    public ResponseEntity<EventDto> addEvent(
            @PathVariable Long userId,
            @Valid @RequestBody NewEventDto eventDto) {
        EventDto createdEvent = eventService.addEvent(userId, eventDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEvent);


    }

    @GetMapping("/users/{userId}/events")
    public List<EventDto> getUserEvents(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {
        log.info("/users/{userId}/events");
        statsClient.sendHit(new EndpointHitDto(null, "/users/{userId}/events", request.getRequestURI(),
                request.getRemoteAddr(), LocalDateTime.now()));
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
        return eventService.updateEvent(userId, eventId, updateDto, false);
        //return convertToDto(updatedEvent);
    }

    @PatchMapping("/admin/events/{eventId}")
    public EventFullDto updateEventAdmin(//@PathVariable Long userId,
                                         @PathVariable Long eventId,
                                         @RequestBody EventUpdateDto updateDto) {
        return eventService.updateEvent(null, eventId, updateDto, true);

    }

    @GetMapping("/admin/events")
    public List<EventFullDto> getEvents(
            @RequestParam(value = "users", required = false) List<Long> users,
            @RequestParam(value = "states", required = false) List<String> states,
            @RequestParam(value = "categories", required = false) List<Long> categories,
            @RequestParam(value = "rangeStart", required = false) String rangeStart,
            @RequestParam(value = "rangeEnd", required = false) String rangeEnd,
            @RequestParam(value = "from", defaultValue = "0") Integer from,
            @RequestParam(value = "size", defaultValue = "10") Integer size
    ) {
        return eventService.getEvents(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @GetMapping("/events")
    public List<EventFullDto> getEvents_(
            @RequestParam(value = "users", required = false) List<Long> users,
            @RequestParam(value = "states", required = false) List<String> states,
            @RequestParam(value = "categories", required = false) List<Long> categories,
            @RequestParam(value = "rangeStart", required = false) String rangeStart,
            @RequestParam(value = "rangeEnd", required = false) String rangeEnd,
            @RequestParam(value = "from", defaultValue = "0") Integer from,
            @RequestParam(value = "size", defaultValue = "10") Integer size
    ) {
        return eventService.getEvents(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @GetMapping("/events/{id}")
    public ResponseEntity<EventFullDto> getEventById(@PathVariable Long id) {
        try {
            EventFullDto eventDetails = eventService.getPublishedEventDetails(id);
            return ResponseEntity.ok(eventDetails);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

}
