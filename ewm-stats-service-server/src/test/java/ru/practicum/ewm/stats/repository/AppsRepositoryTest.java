package ru.practicum.ewm.stats.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.ewm.stats.model.Apps;

import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Rollback
@Sql({
        "/test_schema.sql",
        "/import_apps_data.sql"
})
class AppsRepositoryTest {

    @Autowired
    private final TestEntityManager em;

    @Autowired
    private final AppsRepository appsRepository;

    private List<Apps> appsTestList;

    @Order(1)
    @Test
    void contextLoads() {
        Assertions.assertNotNull(em);
        Assertions.assertNotNull(appsRepository);
    }

    @Order(2)
    @Test
    void checkTestData() {
        fillAppsTestList();

        Assertions.assertEquals(2, appsTestList.size());
    }

    @Order(3)
    @Test
    void getAppsByNameTestStandardBehavior() {
        var ret = appsRepository.getAppsByName("ewm-main-service");

        Assertions.assertTrue(ret.isPresent());
        Assertions.assertEquals(1000, ret.get().getId());
    }

    @Order(4)
    @Test
    void getAppsByNameTestNoServiceBehavior() {
        var ret = appsRepository.getAppsByName("ewm-main-service_2");

        Assertions.assertFalse(ret.isPresent());
    }

    private void fillAppsTestList() {
        appsTestList = appsRepository.findAll();
        Assertions.assertTrue(!appsTestList.isEmpty(), "Количество тестовых записей в 'apps' должно быть больше 0");
    }
}
