package ru.practicum.events;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.category.Category;
import ru.practicum.category.CategoryDto;
import ru.practicum.category.CategoryRepository;
import ru.practicum.events.dto.EventDto;
import ru.practicum.events.dto.EventFullDto;
import ru.practicum.events.dto.EventUpdateDto;
import ru.practicum.events.dto.NewEventDto;
import ru.practicum.exceptions.EntityNotFoundException;
import ru.practicum.users.User;
import ru.practicum.users.UserDto;
import ru.practicum.users.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final EventMapper mapper;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public EventService(EventRepository eventRepository,
                        EventMapper eventMapper,
                        UserRepository userRepository,
                        CategoryRepository categoryRepository) {
        this.eventRepository = eventRepository;
        this.mapper = eventMapper;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    public Event addEvent(Long userId, NewEventDto dto) {
        Event event = mapper.NewEventDtoToEntity(dto, userId);
        return eventRepository.save(event);
    }

    public List<EventDto> getEventsByUser(Long userId, int from, int size) {
        int page = from / size; // Вычисляем номер страницы
        Pageable pageable = PageRequest.of(page, size);

        Page<Event> eventsPage = eventRepository.findByUserId(userId, pageable);
        List<Event> events = eventsPage.getContent();

        // Преобразование в DTO для исключения лишней информации (если нужно)
        return events.stream()
                .map(this::toDto)
                //.collect(Collectors.toList());
                .toList();
    }

    public EventDto toDto(Event event) {
        EventDto dto = new EventDto();

        Category category = categoryRepository.findById(event.getCategoryId()).get();
        User user = userRepository.findById(event.getId()).get();
        dto.setAnnotation(event.getAnnotation());
        dto.setEventDate(event.getEventDate()/*.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))*/);
        dto.setTitle(event.getTitle());
        dto.setPaid(event.getPaid());
        dto.setCategory(new CategoryDto(category.getId(), category.getName()));
        dto.setUser(new UserDto(user.getId(), user.getName()));

        return dto;
    }

    public EventFullDto getEventDetails(Long userId, Long eventId) {
        Event event = eventRepository.findByIdAndUserId(eventId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Event not found"));

        return convertToFullDto(event);
    }

    private EventFullDto convertToFullDto(Event event) {
        EventFullDto dto = new EventFullDto();
        dto.setId(event.getId());
        dto.setAnnotation(event.getAnnotation());
        dto.setConfirmedRequests(0);//TODO
        dto.setCreatedOn(event.getCreatedOn().toString());
        dto.setDescription(event.getDescription());
        dto.setEventDate(event.getEventDate().toString());
        dto.setPaid(event.getPaid());
        dto.setParticipantLimit(event.getParticipantLimit());
        if (event.getPublishedOn() != null){
            dto.setPublishedOn(event.getPublishedOn().toString());
        }

        dto.setRequestModeration(event.getRequestModeration());
        dto.setState(event.getState().name()); // предполагается, что есть enum State
        dto.setTitle(event.getTitle());
        dto.setViews(0);//TODO;

        Category category = categoryRepository.findById(event.getCategoryId()).get();
        User user = userRepository.findById(event.getId()).get();

        dto.setCategory(new CategoryDto(category.getId(), category.getName()));
        dto.setInitiator(new UserDto(user.getId(), user.getName()));

        // Location

        LocationDto locationDto = new LocationDto();
        locationDto.setLat(event.getLocationLat());
        locationDto.setLon(event.getLocationLon());
        dto.setLocation(locationDto);

        return dto;
    }

    public EventFullDto updateEvent(Long userId, Long eventId, EventUpdateDto updateDto) {
        Event event = eventRepository.findByIdAndUserId(eventId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Event not found"));

        // Проверка статуса события
        Status status = event.getState(); // предполагается enum
        if (!(status == Status.CANCELED || status == Status.PENDING)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Only cancellations or pending events can be modified");
        }

        // Проверка даты
        if (updateDto.getEventDate() != null) {
            LocalDateTime nowPlusTwoHours = LocalDateTime.now().plusHours(2);
            LocalDateTime eventDate = LocalDateTime.parse(updateDto.getEventDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            if (eventDate.isBefore(nowPlusTwoHours)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Event date cannot be earlier than two hours from now");
            }
            event.setEventDate(eventDate);//?????????
        }


        // Обновление данных
        if (updateDto.getAnnotation() != null){
            event.setAnnotation(updateDto.getAnnotation());
        }
        if (updateDto.getCategory() != null){
            event.setCategoryId(updateDto.getCategory());
        }
        if (updateDto.getDescription() != null){
            event.setDescription(updateDto.getDescription());
        }


        if (updateDto.getPaid() != null){
            event.setPaid(updateDto.getPaid());
        }
        if (updateDto.getParticipantLimit() != null){
            event.setParticipantLimit(updateDto.getParticipantLimit());
        }
        if (updateDto.getRequestModeration() != null){
            event.setRequestModeration(updateDto.getRequestModeration());
        }
        if (updateDto.getTitle() != null){
            event.setTitle(updateDto.getTitle());
        }
        if (updateDto.getLocation() != null) {
            event.setLocationLat(updateDto.getLocation().getLat());
            event.setLocationLon(updateDto.getLocation().getLon());
        }
        if (updateDto.getStateAction() != null){
            String action = updateDto.getStateAction();
            if ("CANCEL_REVIEW".equals(action)) {
                event.setState(Status.CANCELED);
            } else if ("SEND_TO_REVIEW".equals(action)) {
                event.setState(Status.PENDING);
            }
        }

        Event savedEvent = eventRepository.save(event);

        return convertToFullDto(savedEvent);
    }
}