package ru.practicum;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.ViewStats;
import ru.practicum.model.EndpointHit;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping
public class StatsController {

    private final StatsService statsService;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void hit(@RequestBody EndpointHit hit) {
        statsService.saveHit(hit);
    }

    @GetMapping("/stats")
    public List<ViewStats> getStats(
            @RequestParam("start") String start,
            @RequestParam("end") String end,
            @RequestParam(name = "uris", required = false) List<String> uris,
            @RequestParam(name = "unique", required = false, defaultValue = "false") boolean unique
    ) {
        LocalDateTime startDate = LocalDateTime.parse(start, formatter);
        LocalDateTime endDate = LocalDateTime.parse(end, formatter);

        return statsService.getStats(startDate, endDate, uris, unique);
    }
}