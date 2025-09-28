package ru.practicum;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import ru.practicum.dto.ViewStats;
import ru.practicum.model.EndpointHit;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@Component
public class StatsClient {
    private final RestTemplate restTemplate;
    private final String baseUrl; // например, http://localhost:8080, либо адрес вашего сервера
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public StatsClient(RestTemplate restTemplate,
                       @Value("${stats.service.base-url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    public void sendHit(EndpointHit hit) {
        String url = baseUrl + "/hit";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<EndpointHit> request = new HttpEntity<>(hit, headers);

        restTemplate.postForEntity(url, request, Void.class);
    }

    public List<ViewStats> getStats(
            String start,
            String end,
            List<String> uris,
            boolean unique
    ) {
        String url = baseUrl + "/stats";

        // Формируем параметры URL с учетом списка uris
        StringBuilder urlWithParams = new StringBuilder(url)
                .append("?start=").append(start)
                .append("&end=").append(end)
                .append("&unique=").append(unique);

        if (uris != null && !uris.isEmpty()) {
            for (String uri : uris) {
                urlWithParams.append("&uris=").append(uri);
            }
        }

        ResponseEntity<ViewStats[]> response = restTemplate.getForEntity(urlWithParams.toString(), ViewStats[].class);

        return Arrays.asList(response.getBody());
    }
}
