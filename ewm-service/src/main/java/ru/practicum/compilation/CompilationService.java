package ru.practicum.compilation;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.category.CategoryDto;
import ru.practicum.category.CategoryRepository;
import ru.practicum.events.Event;
import ru.practicum.events.EventRepository;
import ru.practicum.events.dto.EventDto;
import ru.practicum.exceptions.EntityNotFoundException;
import ru.practicum.users.UserDto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CompilationService {
    private final CompilationRepository compilationRepository;
    private final CompilationEventRepository compilationEventRepository;
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    public CompilationService(CompilationRepository selectionRepository,
                              CompilationEventRepository selectionEventRepository,
                              CategoryRepository categoryRepository,
                              EventRepository eventRepository) {
        this.compilationRepository = selectionRepository;
        this.compilationEventRepository = selectionEventRepository;
        this.categoryRepository = categoryRepository;
        this.eventRepository = eventRepository;
    }

    public CompilationResponseDto createCompilation(CreateCompilationDto dto) {
        Compilation compilation = new Compilation();
        compilation.setTitle(dto.getTitle());
        compilation.setPinned(dto.getPinned());
        compilation = compilationRepository.save(compilation);

        // Связываем события, если есть
        if (dto.getEvents() != null && !dto.getEvents().isEmpty()) {
            Compilation finalCompilation = compilation;
            List<CompilationEvent> links = dto.getEvents().stream()
                    .map(eventId -> {
                        CompilationEvent link = new CompilationEvent();
                        //link.setCompilation_id(finalCompilation.getId());
                        link.setCompilation(finalCompilation);
                        link.setEvent_id(eventId);
                        compilationEventRepository.save(link);
                        return link;
                    }).collect(Collectors.toList());

        }

        // Получаем события для ответа (по их ID)
        List<Event> events = new ArrayList<>();
        if (dto.getEvents() != null && !dto.getEvents().isEmpty()) {
            // Предполагается, что есть репозиторий Event
            events = eventRepository.findAllById(dto.getEvents());
        }
        return toResponseDto(compilation, events);
    }

    // метод для получения данных о подборке


    public CompilationResponseDto toResponseDto(Compilation compilation, List<Event> eventList) {

        List<EventDto> eventDtos = eventList.stream()
                .map(event -> {
                    EventDto dto = new EventDto();
                     dto.setId(event.getId());
                    dto.setTitle(event.getTitle());
                    dto.setAnnotation(event.getAnnotation());
                    dto.setCategory(new CategoryDto(event.getCategoryId(), null));
                    dto.setUser(new UserDto(event.getUserId(), null));
                    dto.setPaid(event.getPaid());
                    dto.setEventDate(event.getEventDate()/*.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))*/);
                    // dto.(event.getConfirmedRequests());
                    // dto.setViews(event.getViews());
                    return dto;
                }).collect(Collectors.toList());

        CompilationResponseDto responseDto = new CompilationResponseDto();
        responseDto.setId(compilation.getId());
        responseDto.setTitle(compilation.getTitle());
        responseDto.setPinned(compilation.getPinned());
        responseDto.setEvents(eventDtos);

        return responseDto;
    }

    /**
     * Удаляет подборку событий по её ID.
     *
     * @param compilationId ID подборки
     * @throws EntityNotFoundException если подборка не найдена
     */
    @Transactional
    public void deleteCompilations(Long compilationId) {
        if (!compilationRepository.existsById(compilationId)) {
            throw new EntityNotFoundException("Подборка с ID " + compilationId + " не найдена");
        }
        compilationRepository.deleteById(compilationId);
    }

    /**
     * Обновление подборки по ID.
     */
    @Transactional
    public CompilationResponseDto updateSelection(Long id, UpdateCompilationDto dto) {
        Compilation compilation = compilationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Подборка с ID " + id + " не найдена"));

        if (dto.getTitle() != null) {
            compilation.setTitle(dto.getTitle());
        }
        if (dto.getPinned() != null) {
            compilation.setPinned(dto.getPinned());
        }
        List<Event> events = new ArrayList<>();
        if (dto.getEvents() != null) {
            // по списку id пройтись удалить/сохранить события?
            events = eventRepository.findAllById(dto.getEvents());

            compilationEventRepository.deleteAllByCompilation_id(compilation.getId());
            dto.getEvents().stream().forEach(e->{
                CompilationEvent ce = new CompilationEvent();
                ce.setCompilation(compilation);
                ce.setEvent_id(e);
                compilationEventRepository.save(ce);
            });
        }
        Compilation updatedCompilation = compilationRepository.save(compilation);


        return toResponseDto(updatedCompilation, events);
    }

    public List<CompilationResponseDto> getCompilations(Boolean pinned, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        Page<Compilation> page;

        if (pinned != null) {
            page = compilationRepository.findByPinned(pinned, pageable);
        } else {
            page = compilationRepository.findAll(pageable);
        }

        // return page.getContent();  // Возвращает список, даже если он пустой
        return page.stream().map(compilation -> {
            List<CompilationEvent> cm = compilationEventRepository.findByCompilation_Id(compilation.getId());
            List<Event> events = cm.stream()
                    .map(c -> {
                        return eventRepository.findById(c.getEvent_id()).get();
                    }).toList();
            return toResponseDto(compilation, events);
        }).toList();
        // return toResponseDto(page.getContent(), new ArrayList<>());
    }

    /**
     * Получить подборку по ID.
     *
     * @param id ID подборки
     * @return подборка событий
     * @throws EntityNotFoundException если подборка не найдена
     */
    public CompilationResponseDto getCompilationById(Long id) {

        Compilation compilation = compilationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Подборка с ID " + id + " не найдена"));

        List<CompilationEvent> ce = compilationEventRepository.findByCompilation_Id(compilation.getId());
        List<Event> events = ce.stream()
                .map(c -> {
                    return eventRepository.findById(c.getEvent_id()).get();
                }).toList();
        return toResponseDto(compilation, events);
    }
}
