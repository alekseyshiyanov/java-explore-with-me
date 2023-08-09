package ru.practicum.statsclient.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.util.UriUtils;
import ru.practicum.statsclient.exceptions.ClientErrorException;
import ru.practicum.statsclient.hits.HitDto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StatsClient extends BaseClient {
    private static final String HITS_API_PREFIX = "/hit";
    private static final String STATS_API_PREFIX = "/stats";

    @Autowired
    public StatsClient(@Value("${stats-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> createHitRecord(HitDto hitDto) {
        return post(HITS_API_PREFIX, hitDto);
    }

    public ResponseEntity<Object> getStats(String startTime, String endTime) {
        return getStats(startTime, endTime, null, null);
    }

    public ResponseEntity<Object> getStats(String startTime, String endTime, List<String> uris) {
        return getStats(startTime, endTime, uris, null);
    }

    public ResponseEntity<Object> getStats(String startTime, String endTime, Boolean unique) {
        return getStats(startTime, endTime, null, unique);
    }

    public ResponseEntity<Object> getStats(String startTime, String endTime, List<String> uris, Boolean unique) {
        StringBuilder path = new StringBuilder(STATS_API_PREFIX + "?start={start}&end={end}");

        if (startTime == null) {
            throw new ClientErrorException("Параметр 'startTime' не может быть null");
        }
        if (endTime == null) {
            throw new ClientErrorException("Параметр 'endTime' не может быть null");
        }
        if (startTime.isBlank()) {
            throw new ClientErrorException("Параметр 'startTime' не может быть пустым или состоять из пробелов");
        }
        if (endTime.isBlank()) {
            throw new ClientErrorException("Параметр 'endTime' не может быть пустым или состоять из пробелов");
        }

        String encodedStartTime = UriUtils.encodePath(startTime, "UTF-8");
        String encodedEndTime = UriUtils.encodePath(endTime, "UTF-8");

        Map<String, Object> parameters = new HashMap<>();

        parameters.put("start", encodedStartTime);
        parameters.put("end", encodedEndTime);

        if (uris != null) {
            for (int i = 0; i < uris.size(); i++) {
                parameters.put("uris" + i, uris.get(i));
                path.append("&uris={uris" + i + "}");
            }
        }

        if (unique != null) {
            parameters.put("unique", unique);
            path.append("&unique={unique}");
        }

        return get(path.toString(), parameters);
    }
}
