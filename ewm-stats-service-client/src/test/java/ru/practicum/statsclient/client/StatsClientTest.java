package ru.practicum.statsclient.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.matchers.Times;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.MediaType;
import org.mockserver.verify.VerificationTimes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.util.UriUtils;
import ru.practicum.statsclient.exceptions.ClientErrorException;
import ru.practicum.statsclient.hits.HitDto;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockserver.model.HttpRequest.request;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ExtendWith(MockitoExtension.class)
class StatsClientTest {

    private ClientAndServer mockServer;

    @Autowired
    private final StatsClient statsClient;

    private final String startTime = "2020.01.01 00:00:00";
    private final String endTime = "2035.01.01 00:00:00";

    private final String encodedStartTime = UriUtils.encode(startTime, "UTF-8");
    private final String encodedEndTime = UriUtils.encode(endTime, "UTF-8");

    @BeforeEach
    public void start() {
        mockServer = ClientAndServer.startClientAndServer(8000);
    }

    @AfterEach
    public void stop() {
        mockServer.stop();
    }

    @Test
    @Order(1)
    void contextLoads() {
        Assertions.assertNotNull(statsClient);
        Assertions.assertNotNull(mockServer);
    }

    @Test
    @Order(2)
    public void createHitRecordTest() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        var ldtNow = LocalDateTime.now();

        HitDto hitDto = HitDto.builder()
                .app("test_app_name")
                .uri("test_uri")
                .ip("192.168.1.1")
                .timestamp(ldtNow)
                .build();

        String hitDtoJson = objectMapper.writeValueAsString(hitDto);

        mockServer.when(
                        request()
                                .withMethod("POST")
                                .withPath("/hit")
                                .withContentType(MediaType.APPLICATION_JSON)
                                .withBody(hitDtoJson),
                        Times.exactly(1))
                .respond(HttpResponse.response().withStatusCode(200));

        statsClient.createHitRecord(hitDto);

        new MockServerClient("localhost", 8000).verify(
                request()
                        .withMethod("POST")
                        .withPath("/hit")
                        .withBody(hitDtoJson),
                VerificationTimes.exactly(1)
        );
    }

    @Test
    @Order(3)
    public void getStatsWithoutUrisAndUnique() {
        mockServer.when(
                request()
                        .withMethod("GET")
                        .withPath("/stats")
                        .withQueryStringParameter("start", encodedStartTime)
                        .withQueryStringParameter("end", encodedEndTime),
                        Times.exactly(1))
                .respond(HttpResponse.response().withStatusCode(200));

        statsClient.getStats(startTime, endTime);

        new MockServerClient("localhost", 8000).verify(
                request()
                        .withMethod("GET")
                        .withPath("/stats")
                        .withQueryStringParameter("start", encodedStartTime)
                        .withQueryStringParameter("end", encodedEndTime),
                VerificationTimes.exactly(1)
        );
    }

    @Test
    @Order(4)
    public void getStatsWithoutUrisUniqueIsTrue() {
        mockServer.when(
                        request()
                                .withMethod("GET")
                                .withPath("/stats")
                                .withQueryStringParameter("start", encodedStartTime)
                                .withQueryStringParameter("end", encodedEndTime)
                                .withQueryStringParameter("unique", "true"),
                        Times.exactly(1))
                .respond(HttpResponse.response().withStatusCode(200));

        statsClient.getStats(startTime, endTime, true);

        new MockServerClient("localhost", 8000).verify(
                request()
                        .withMethod("GET")
                        .withPath("/stats")
                        .withQueryStringParameter("start", encodedStartTime)
                        .withQueryStringParameter("end", encodedEndTime)
                        .withQueryStringParameter("unique", "true"),
                VerificationTimes.exactly(1)
        );
    }

    @Test
    @Order(5)
    public void getStatsWithUrisUniqueIsTrue() {
        List<String> uris = Arrays.asList(
             "/events",
             "/events/2",
             "/events/1"
        );

        mockServer.when(
                        request()
                                .withMethod("GET")
                                .withPath("/stats")
                                .withQueryStringParameter("start", encodedStartTime)
                                .withQueryStringParameter("end", encodedEndTime)
                                .withQueryStringParameter("uris", uris.get(0))
                                .withQueryStringParameter("uris", uris.get(1))
                                .withQueryStringParameter("uris", uris.get(2))
                                .withQueryStringParameter("unique", "true"),
                        Times.exactly(1))
                .respond(HttpResponse.response().withStatusCode(200));

        statsClient.getStats(startTime, endTime, uris,true);

        new MockServerClient("localhost", 8000).verify(
                request()
                        .withMethod("GET")
                        .withPath("/stats")
                        .withQueryStringParameter("start", encodedStartTime)
                        .withQueryStringParameter("end", encodedEndTime)
                        .withQueryStringParameter("uris", uris.get(0))
                        .withQueryStringParameter("uris", uris.get(1))
                        .withQueryStringParameter("uris", uris.get(2))
                        .withQueryStringParameter("unique", "true"),
                VerificationTimes.exactly(1)
        );
    }

    @Test
    @Order(6)
    public void getStatsWithUrisWithoutUnique() {
        List<String> uris = Arrays.asList(
                "/events",
                "/events/2",
                "/events/1"
        );

        mockServer.when(
                        request()
                                .withMethod("GET")
                                .withPath("/stats")
                                .withQueryStringParameter("start", encodedStartTime)
                                .withQueryStringParameter("end", encodedEndTime)
                                .withQueryStringParameter("uris", uris.get(0))
                                .withQueryStringParameter("uris", uris.get(1))
                                .withQueryStringParameter("uris", uris.get(2)),
                        Times.exactly(1))
                .respond(HttpResponse.response().withStatusCode(200));

        statsClient.getStats(startTime, endTime, uris);

        new MockServerClient("localhost", 8000).verify(
                request()
                        .withMethod("GET")
                        .withPath("/stats")
                        .withQueryStringParameter("start", encodedStartTime)
                        .withQueryStringParameter("end", encodedEndTime)
                        .withQueryStringParameter("uris", uris.get(0))
                        .withQueryStringParameter("uris", uris.get(1))
                        .withQueryStringParameter("uris", uris.get(2)),
                VerificationTimes.exactly(1)
        );
    }

    @Test
    @Order(7)
    public void getStatsWithUrisWithoutUniqueStatus300() {
        List<String> uris = Arrays.asList(
                "/events",
                "/events/2",
                "/events/1"
        );

        mockServer.when(
                        request()
                                .withMethod("GET")
                                .withPath("/stats")
                                .withQueryStringParameter("start", encodedStartTime)
                                .withQueryStringParameter("end", encodedEndTime)
                                .withQueryStringParameter("uris", uris.get(0))
                                .withQueryStringParameter("uris", uris.get(1))
                                .withQueryStringParameter("uris", uris.get(2)),
                        Times.exactly(1))
                .respond(HttpResponse.response().withStatusCode(300));

        statsClient.getStats(startTime, endTime, uris);

        new MockServerClient("localhost", 8000).verify(
                request()
                        .withMethod("GET")
                        .withPath("/stats")
                        .withQueryStringParameter("start", encodedStartTime)
                        .withQueryStringParameter("end", encodedEndTime)
                        .withQueryStringParameter("uris", uris.get(0))
                        .withQueryStringParameter("uris", uris.get(1))
                        .withQueryStringParameter("uris", uris.get(2)),
                VerificationTimes.exactly(1)
        );
    }

    @Test
    @Order(8)
    public void getStatsWithUrisWithoutUniqueStatus400() {
        List<String> uris = Arrays.asList(
                "/events",
                "/events/2",
                "/events/1"
        );

        mockServer.when(
                        request()
                                .withMethod("GET")
                                .withPath("/stats")
                                .withQueryStringParameter("start", encodedStartTime)
                                .withQueryStringParameter("end", encodedEndTime)
                                .withQueryStringParameter("uris", uris.get(0))
                                .withQueryStringParameter("uris", uris.get(1))
                                .withQueryStringParameter("uris", uris.get(2)),
                        Times.exactly(1))
                .respond(HttpResponse.response().withStatusCode(400));

        statsClient.getStats(startTime, endTime, uris);

        new MockServerClient("localhost", 8000).verify(
                request()
                        .withMethod("GET")
                        .withPath("/stats")
                        .withQueryStringParameter("start", encodedStartTime)
                        .withQueryStringParameter("end", encodedEndTime)
                        .withQueryStringParameter("uris", uris.get(0))
                        .withQueryStringParameter("uris", uris.get(1))
                        .withQueryStringParameter("uris", uris.get(2)),
                VerificationTimes.exactly(1)
        );
    }

    @Test
    @Order(9)
    public void getStatsWithoutStartTime() {
        ClientErrorException ex = Assertions.assertThrows(ClientErrorException.class, () ->
                statsClient.getStats(null, endTime));
        Assertions.assertTrue(ex.getMessage().contains("Параметр 'startTime' не может быть null"));
    }

    @Test
    @Order(10)
    public void getStatsWithoutEndTime() {
        ClientErrorException ex = Assertions.assertThrows(ClientErrorException.class, () ->
                statsClient.getStats(startTime, null));
        Assertions.assertTrue(ex.getMessage().contains("Параметр 'endTime' не может быть null"));
    }

    @Test
    @Order(11)
    public void getStatsWithEmptyStartTime() {
        ClientErrorException ex = Assertions.assertThrows(ClientErrorException.class, () ->
                statsClient.getStats("", endTime));
        Assertions.assertTrue(ex.getMessage().contains("Параметр 'startTime' не может быть пустым или состоять из пробелов"));
    }

    @Test
    @Order(12)
    public void getStatsWithBlankStartTime() {
        ClientErrorException ex = Assertions.assertThrows(ClientErrorException.class, () ->
                statsClient.getStats("   ", endTime));
        Assertions.assertTrue(ex.getMessage().contains("Параметр 'startTime' не может быть пустым или состоять из пробелов"));
    }

    @Test
    @Order(13)
    public void getStatsWithEmptyEndTime() {
        ClientErrorException ex = Assertions.assertThrows(ClientErrorException.class, () ->
                statsClient.getStats(startTime, ""));
        Assertions.assertTrue(ex.getMessage().contains("Параметр 'endTime' не может быть пустым или состоять из пробелов"));
    }

    @Test
    @Order(14)
    public void getStatsWithBlankEndTime() {
        ClientErrorException ex = Assertions.assertThrows(ClientErrorException.class, () ->
                statsClient.getStats(startTime, "  "));
        Assertions.assertTrue(ex.getMessage().contains("Параметр 'endTime' не может быть пустым или состоять из пробелов"));
    }
}