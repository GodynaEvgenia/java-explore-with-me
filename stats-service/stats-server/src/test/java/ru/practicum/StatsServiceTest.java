package ru.practicum;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import ru.practicum.*;
import ru.practicum.dto.ViewStats;
import ru.practicum.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StatsServiceTest {

    private EndpointHitRepository repository;
    private StatsService service;

    @BeforeEach
    void setUp() {
        repository = mock(EndpointHitRepository.class);
        service = new StatsService(repository);
    }

    @Test
    void testSaveHitCallsRepositorySave() {
        EndpointHit hit = new EndpointHit();
        service.saveHit(hit);

        verify(repository, times(1)).save(hit);
    }

    @Test
    void testGetStats_UniqueTrue_UrisNull_CallsFindUniqueStatsWithoutUris() {
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now();

        List<ViewStats> expected = List.of(new ViewStats("app", "/uri", 5L));

        when(repository.findUniqueStatsWithoutUris(start, end)).thenReturn(expected);

        List<ViewStats> actual = service.getStats(start, end, null, true);

        assertEquals(expected, actual);
        verify(repository, times(1)).findUniqueStatsWithoutUris(start, end);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void testGetStats_UniqueTrue_UrisNotEmpty_CallsFindUniqueStats() {
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now();
        List<String> uris = List.of("/uri1", "/uri2");

        List<ViewStats> expected = List.of(new ViewStats("app", "/uri1", 3L));

        when(repository.findUniqueStats(start, end, uris)).thenReturn(expected);

        List<ViewStats> actual = service.getStats(start, end, uris, true);

        assertEquals(expected, actual);
        verify(repository, times(1)).findUniqueStats(start, end, uris);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void testGetStats_UniqueFalse_UrisNull_CallsFindStatsWithoutUris() {
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now();

        List<ViewStats> expected = List.of(new ViewStats("app", "/uri", 10L));

        when(repository.findStatsWithoutUris(start, end)).thenReturn(expected);

        List<ViewStats> actual = service.getStats(start, end, null, false);

        assertEquals(expected, actual);
        verify(repository, times(1)).findStatsWithoutUris(start, end);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void testGetStats_UniqueFalse_UrisNotEmpty_CallsFindStats() {
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now();
        List<String> uris = List.of("/uri1");

        List<ViewStats> expected = List.of(new ViewStats("app", "/uri1", 7L));

        when(repository.findStats(start, end, uris)).thenReturn(expected);

        List<ViewStats> actual = service.getStats(start, end, uris, false);

        assertEquals(expected, actual);
        verify(repository, times(1)).findStats(start, end, uris);
        verifyNoMoreInteractions(repository);
    }
}