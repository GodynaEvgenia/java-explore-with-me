package ru.practicum;

import org.springframework.stereotype.Service;
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

    public void saveHit(EndpointHit hit) {
        repository.save(hit);
    }

    public List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
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
}
