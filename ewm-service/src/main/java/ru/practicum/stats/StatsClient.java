package ru.practicum.stats;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.practicum.dto.EndpointHitDto;

import java.time.format.DateTimeFormatter;

@Component
public class StatsClient {
    private final RestTemplate restTemplate;
    private final String baseUrl; // например, http://localhost:8080, либо адрес вашего сервера
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public StatsClient(RestTemplate restTemplate,
                       @Value("${stats-service.url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    public void sendHit(EndpointHitDto hit) {
        String url = baseUrl + "/hit";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<EndpointHitDto> request = new HttpEntity<>(hit, headers);

        restTemplate.postForEntity(url, request, Void.class);
    }


}
