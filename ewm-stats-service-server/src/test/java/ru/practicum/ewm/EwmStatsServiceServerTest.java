package ru.practicum.ewm;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Sql({"/test_schema.sql"})
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class EwmStatsServiceServerTest {

    @Test
    void contextLoads() {
    }
}
