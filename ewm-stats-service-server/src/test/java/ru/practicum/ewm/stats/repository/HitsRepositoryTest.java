package ru.practicum.ewm.stats.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.ewm.stats.model.Apps;
import ru.practicum.ewm.stats.model.Hits;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Rollback
@Sql({
        "/test_schema.sql",
        "/import_apps_data.sql",
        "/import_hits_data.sql"
})
class HitsRepositoryTest {

    @Autowired
    private final TestEntityManager em;

    @Autowired
    private final AppsRepository appsRepository;

    @Autowired
    private final HitsRepository hitsRepository;

    private List<Apps> appsTestList;
    private List<Hits> hitsTestList;

    @Order(1)
    @Test
    void contextLoads() {
        Assertions.assertNotNull(em);
        Assertions.assertNotNull(appsRepository);
        Assertions.assertNotNull(hitsRepository);
    }

    @Order(2)
    @Test
    void checkTestData() {
        fillAppsTestList();
        fillHitsTestList();

        Assertions.assertEquals(2, appsTestList.size());
        Assertions.assertEquals(10, hitsTestList.size());
    }

    @Order(3)
    @Test
    void createHitStandardBehavior() {
        fillAppsTestList();
        fillHitsTestList();

        Hits newHit = Hits.builder()
                .id(null)
                .app(appsTestList.get(1))
                .uri("/events/101")
                .ipAddress("10.10.10.10")
                .created(LocalDateTime.now())
                .build();

        Assertions.assertNull(newHit.getId());
        hitsRepository.save(newHit);
        Assertions.assertNotNull(newHit.getId());
    }

    @Order(4)
    @Test
    void checkUrisListExistStandardBehavior() {
        List<String> uris = Arrays.asList(
                "/events",
                "/events/1"
        );

        Long recordCount = hitsRepository.checkUrisListExist(uris);

        Assertions.assertEquals(2, recordCount);
    }

    @Order(5)
    @Test
    void checkUrisListNotExistBehavior() {
        List<String> uris = Arrays.asList(
                "/events",
                "/events/100"
        );

        Long recordCount = hitsRepository.checkUrisListExist(uris);

        Assertions.assertEquals(1, recordCount);
    }

    @Order(6)
    @Test
    void findAllByCreatedBetweenTimesUniqueIpAddressUrisNotEmptyStandardBehavior() {
        LocalDateTime startTime = LocalDateTime.of(2020, 1, 1, 0, 0, 0);
        LocalDateTime endTime = LocalDateTime.of(2030, 1, 1, 0, 0, 0);

        List<String> uris = Arrays.asList(
                "/events",
                "/events/1"
        );

        var recordList = hitsRepository.findAllByCreatedBetweenTimesUniqueIpAddressUrisNotEmpty(startTime, endTime, uris);

        Assertions.assertEquals(3, recordList.size());
        Assertions.assertEquals(3, recordList.get(0).getHits());
        Assertions.assertEquals(1, recordList.get(1).getHits());
        Assertions.assertEquals(1, recordList.get(2).getHits());
    }

    @Order(7)
    @Test
    void findAllByCreatedBetweenTimesNonUniqueIpAddressUrisNotEmptyStandardBehavior() {
        LocalDateTime startTime = LocalDateTime.of(2020, 1, 1, 0, 0, 0);
        LocalDateTime endTime = LocalDateTime.of(2030, 1, 1, 0, 0, 0);

        List<String> uris = Arrays.asList(
                "/events",
                "/events/1"
        );

        var recordList = hitsRepository.findAllByCreatedBetweenTimesNonUniqueIpAddressUrisNotEmpty(startTime, endTime, uris);

        Assertions.assertEquals(3, recordList.size());
        Assertions.assertEquals(4, recordList.get(0).getHits());
        Assertions.assertEquals(1, recordList.get(1).getHits());
        Assertions.assertEquals(1, recordList.get(2).getHits());
    }

    @Order(8)
    @Test
    void findAllByCreatedBetweenTimesUniqueIpAddressUrisIsEmptyStandardBehavior() {
        LocalDateTime startTime = LocalDateTime.of(2020, 1, 1, 0, 0, 0);
        LocalDateTime endTime = LocalDateTime.of(2030, 1, 1, 0, 0, 0);

        var recordList = hitsRepository.findAllByCreatedBetweenTimesUniqueIpAddressUrisIsEmpty(startTime, endTime);

        Assertions.assertEquals(6, recordList.size());
        Assertions.assertEquals(3, recordList.get(0).getHits());
        Assertions.assertEquals(2, recordList.get(1).getHits());
        Assertions.assertEquals(1, recordList.get(2).getHits());
        Assertions.assertEquals(1, recordList.get(2).getHits());
        Assertions.assertEquals(1, recordList.get(2).getHits());
        Assertions.assertEquals(1, recordList.get(2).getHits());
    }

    @Order(9)
    @Test
    void findAllByCreatedBetweenTimesNonUniqueIpAddressUrisIsEmptyStandardBehavior() {
        LocalDateTime startTime = LocalDateTime.of(2020, 1, 1, 0, 0, 0);
        LocalDateTime endTime = LocalDateTime.of(2030, 1, 1, 0, 0, 0);

        var recordList = hitsRepository.findAllByCreatedBetweenTimesNonUniqueIpAddressUrisIsEmpty(startTime, endTime);

        Assertions.assertEquals(6, recordList.size());
        Assertions.assertEquals(4, recordList.get(0).getHits());
        Assertions.assertEquals(2, recordList.get(1).getHits());
        Assertions.assertEquals(1, recordList.get(2).getHits());
        Assertions.assertEquals(1, recordList.get(2).getHits());
        Assertions.assertEquals(1, recordList.get(2).getHits());
        Assertions.assertEquals(1, recordList.get(2).getHits());
    }

    private void fillAppsTestList() {
        appsTestList = appsRepository.findAll();
        Assertions.assertTrue(!appsTestList.isEmpty(), "Количество тестовых записей в 'apps' должно быть больше 0");
    }

    private void fillHitsTestList() {
        hitsTestList = hitsRepository.findAll();
        Assertions.assertTrue(!hitsTestList.isEmpty(), "Количество тестовых записей в 'hits' должно быть больше 0");
    }
}