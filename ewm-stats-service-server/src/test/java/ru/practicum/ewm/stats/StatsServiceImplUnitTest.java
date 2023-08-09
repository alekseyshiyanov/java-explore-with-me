package ru.practicum.ewm.stats;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.stats.repository.AppsRepository;
import ru.practicum.statsclient.hits.HitDto;
import ru.practicum.statsclient.exceptions.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Sql({
        "/test_schema.sql",
        "/import_apps_data.sql",
        "/import_hits_data.sql"
})
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class StatsServiceImplUnitTest {

    private final StatsService service;

    private final AppsRepository appsRepository;

    private final String startTime = "2020-01-01 00:00:00";
    private final String currentTime = "2023-01-01 00:00:00";
    private final String endTime = "2030-01-01 00:00:00";

    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Test
    @Order(1)
    void contextLoads() {
        Assertions.assertNotNull(service);
        Assertions.assertNotNull(appsRepository);
    }

    @Test
    @Order(2)
    public void createHitRecordTestAppNotPresentBehavior() {
        var ldtNow = LocalDateTime.parse(currentTime, dtf);

        HitDto hitDto = HitDto.builder()
                .app("test_app_name")
                .uri("test_uri")
                .ip("192.168.1.1")
                .timestamp(ldtNow)
                .build();

        var ret = service.createHitRecord(hitDto);
        var appDataFromDB = appsRepository.getAppsByName("test_app_name");

        Assertions.assertEquals("test_app_name", ret.getApp());
        Assertions.assertEquals("test_uri", ret.getUri());
        Assertions.assertEquals("192.168.1.1", ret.getIp());
        Assertions.assertTrue(appDataFromDB.isPresent());
    }

    @Test
    @Order(3)
    public void createHitRecordTestAppIsPresentBehavior() {
        var ldtNow = LocalDateTime.parse(currentTime, dtf);

        HitDto hitDto = HitDto.builder()
                .app("ewm-main-service")
                .uri("test_uri")
                .ip("192.168.1.2")
                .timestamp(ldtNow)
                .build();

        var ret = service.createHitRecord(hitDto);

        Assertions.assertEquals("ewm-main-service", ret.getApp());
        Assertions.assertEquals("test_uri", ret.getUri());
        Assertions.assertEquals("192.168.1.2", ret.getIp());
    }

    @Test
    @Order(4)
    void getStatsNonUniqueIpAddressUrisIsEmptyBehavior() {
        var recordList = service.getStats(startTime, endTime, null, false);

        Assertions.assertEquals(6, recordList.size());
        Assertions.assertEquals(4, recordList.get(0).getHits());
        Assertions.assertEquals(2, recordList.get(1).getHits());
        Assertions.assertEquals(1, recordList.get(2).getHits());
        Assertions.assertEquals(1, recordList.get(2).getHits());
        Assertions.assertEquals(1, recordList.get(2).getHits());
        Assertions.assertEquals(1, recordList.get(2).getHits());
    }

    @Order(5)
    @Test
    void getStatsUniqueIpAddressUrisIsEmptyBehavior() {
        var recordList = service.getStats(startTime, endTime, null, true);

        Assertions.assertEquals(6, recordList.size());
        Assertions.assertEquals(3, recordList.get(0).getHits());
        Assertions.assertEquals(2, recordList.get(1).getHits());
        Assertions.assertEquals(1, recordList.get(2).getHits());
        Assertions.assertEquals(1, recordList.get(2).getHits());
        Assertions.assertEquals(1, recordList.get(2).getHits());
        Assertions.assertEquals(1, recordList.get(2).getHits());
    }

    @Order(6)
    @Test
    void getStatsUniqueIpAddressUrisNotEmptyBehavior() {
        List<String> uris = Arrays.asList(
                "/events",
                "/events/1"
        );

        var recordList = service.getStats(startTime, endTime, uris, true);

        Assertions.assertEquals(3, recordList.size());
        Assertions.assertEquals(3, recordList.get(0).getHits());
        Assertions.assertEquals(1, recordList.get(1).getHits());
        Assertions.assertEquals(1, recordList.get(2).getHits());
    }

    @Order(7)
    @Test
    void getStatsNonUniqueIpAddressUrisNotEmptyBehavior() {
        List<String> uris = Arrays.asList(
                "/events",
                "/events/1"
        );

        var recordList = service.getStats(startTime, endTime, uris, false);

        Assertions.assertEquals(3, recordList.size());
        Assertions.assertEquals(4, recordList.get(0).getHits());
        Assertions.assertEquals(1, recordList.get(1).getHits());
        Assertions.assertEquals(1, recordList.get(2).getHits());
    }

    @Order(8)
    @Test
    void getStatsWrongUrisListBehavior() {
        List<String> uris = Arrays.asList(
                "/events",
                "/events/100"
        );

        ApiErrorException ex = Assertions.assertThrows(ApiErrorException.class, () ->
                service.getStats(startTime, endTime, uris, false));

        Assertions.assertTrue(ex.getMessage().contains("Одно из имен сервиса отсутствует в базе данных"));
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }
}