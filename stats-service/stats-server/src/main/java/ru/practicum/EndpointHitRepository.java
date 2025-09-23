package ru.practicum;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.dto.ViewStats;
import ru.practicum.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

public interface EndpointHitRepository extends JpaRepository<EndpointHit, Long> {
    @Query("SELECT new ru.practicum.dto.ViewStats(e.app, e.uri, COUNT(e.ip)) " +
            "FROM EndpointHit as e " +
            "WHERE e.timestamp BETWEEN :start AND :end " +
            "GROUP BY e.app, e.uri " +
            "ORDER BY COUNT(e.ip) DESC"
    )
    List<ViewStats> findStatsWithoutUris(LocalDateTime start, LocalDateTime end);

    @Query("SELECT new ru.practicum.dto.ViewStats(e.app, e.uri, COUNT(e.ip)) " +
            "FROM EndpointHit as e " +
            "WHERE e.timestamp BETWEEN :start AND :end " +
            "AND e.uri IN :uris " +
            "GROUP BY e.app, e.uri " +
            "ORDER BY COUNT(e.ip) DESC"
    )
    List<ViewStats> findStats(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query(
            "SELECT new ru.practicum.dto.ViewStats(e.app, e.uri, COUNT(distinct e.ip)) " +
                    "FROM EndpointHit e " +
                    "WHERE e.timestamp BETWEEN :start AND :end " +
                    "AND e.uri IN :uris " +
                    "GROUP BY e.app, e.uri " +
                    "ORDER BY COUNT(e.ip) DESC"
    )
    List<ViewStats> findUniqueStats(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query(
            "SELECT new ru.practicum.dto.ViewStats(e.app, e.uri, COUNT(distinct e.ip)) " +
                    "FROM EndpointHit e " +
                    "WHERE e.timestamp BETWEEN :start AND :end " +
                    "GROUP BY e.app, e.uri " +
                    "ORDER BY COUNT(e.ip) DESC"
    )
    List<ViewStats> findUniqueStatsWithoutUris(LocalDateTime start, LocalDateTime end);
}
