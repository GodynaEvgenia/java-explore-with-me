package ru.practicum;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStats;
import ru.practicum.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StatsService {
    private final EndpointHitRepository repository;

    public StatsService(EndpointHitRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void saveHit(EndpointHitDto hit) {
        EndpointHit h = toEntity(hit);
        repository.save(h);
    }

    public List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        if (start.isAfter(end)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Дата начала не может быть позже даты окончания");
        }
        if (unique) {
            if (uris == null || uris.isEmpty()) {
                return repository.findUniqueStatsWithoutUris(start, end);
            } else {
                return repository.findUniqueStats(start, end, uris);
            }
        } else {
            if (uris == null || uris.isEmpty()) {
                return repository.findStatsWithoutUris(start, end);
            } else {
                return repository.findStats(start, end, uris);
            }
        }
    }

    public static EndpointHitDto toDto(EndpointHit entity) {
        if (entity == null) {
            return null;
        }
        return new EndpointHitDto(
                entity.getId(),
                entity.getApp(),
                entity.getUri(),
                entity.getIp(),
                entity.getTimestamp()
        );
    }

    public static EndpointHit toEntity(EndpointHitDto dto) {
        if (dto == null) {
            return null;
        }
        EndpointHit entity = new EndpointHit();
        entity.setId(dto.getId());
        entity.setApp(dto.getApp());
        entity.setUri(dto.getUri());
        entity.setIp(dto.getIp());
        entity.setTimestamp(dto.getTimestamp());
        return entity;
    }
}
