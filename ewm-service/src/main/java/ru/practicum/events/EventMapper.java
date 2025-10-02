package ru.practicum.events;

import org.springframework.stereotype.Component;
import ru.practicum.category.CategoryDto;
import ru.practicum.events.dto.EventDto;
import ru.practicum.events.dto.NewEventDto;
import ru.practicum.users.UserDto;

import java.time.LocalDateTime;

@Component
public class EventMapper {
    public EventDto toDto(Event event, CategoryDto categoryDto, UserDto userDto) {
        EventDto dto = new EventDto();

        dto.setAnnotation(event.getAnnotation());
        dto.setEventDate(event.getEventDate()/*.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))*/);
        dto.setTitle(event.getTitle());
        dto.setPaid(event.getPaid());
        dto.setCategory(categoryDto);
        dto.setUser(userDto);

        return dto;
    }

    public Event newEventDtoToEntity(NewEventDto dto, Long userId) {
        Event event = new Event();
        event.setUserId(userId);
        event.setAnnotation(dto.getAnnotation());
        event.setCategoryId(dto.getCategory());
        event.setDescription(dto.getDescription());
        event.setEventDate(dto.getEventDate());
        event.setLocationLat(dto.getLocation().getLat());
        event.setLocationLon(dto.getLocation().getLon());
        event.setPaid(dto.getPaid());
        event.setParticipantLimit(dto.getParticipantLimit());
        event.setRequestModeration(dto.getRequestModeration());
        event.setTitle(dto.getTitle());

        event.setCreatedOn(LocalDateTime.now());
        event.setPublishedOn(null);
        event.setState(Status.PENDING);
        return event;
    }
}
