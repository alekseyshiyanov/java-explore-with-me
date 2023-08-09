package ru.practicum.ewm.stats;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriUtils;
import ru.practicum.ewm.stats.handlers.RestApiExceptionHandler;
import ru.practicum.statsclient.hits.HitDto;
import ru.practicum.statsclient.stats.StatsDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class StatsControllerMockMvcTest {
    @Mock
    private StatsService service;

    @InjectMocks
    private StatsController controller;

    private final ObjectMapper mapper = new ObjectMapper();

    private MockMvc mvc;


    private final String startTime = "2020-01-01 00:00:00";
    private final String currentTime = "2023-01-01 00:00:00";
    private final String endTime = "2030-01-01 00:00:00";

    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private List<StatsDto> testStatsDtoList;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(RestApiExceptionHandler.class)
                .build();

        mapper.findAndRegisterModules();

        testStatsDtoList = Arrays.asList(
                new StatsDto("test_app_service", "test_app_uri", 6L),
                new StatsDto("test_app_service_0", "test_app_uri_0", 5L),
                new StatsDto("test_app_service_1", "test_app_uri_1", 4L)
        );
    }

    @Test
    void createHit() throws Exception {
        HitDto testHitDto = new HitDto(
                "test_app_service",
                "test_app_uri",
                "10.10.10.10",
                LocalDateTime.parse(currentTime, dtf)
                );

        when(service.createHitRecord(any(HitDto.class))).thenReturn(testHitDto);

        mvc.perform(
                        post("/hit")
                                .content(mapper.writeValueAsString(testHitDto))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.app", is(testHitDto.getApp())))
                .andExpect(jsonPath("$.uri", is(testHitDto.getUri())))
                .andExpect(jsonPath("$.ip", is(testHitDto.getIp())));
    }

    @Test
    void getStatsWithoutUrisAndUnique() throws Exception {
        MultiValueMap<String, String> reqParam = new LinkedMultiValueMap<>();

        reqParam.add("start", UriUtils.encodePath(startTime, "UTF-8"));
        reqParam.add("end", UriUtils.encodePath(endTime, "UTF-8"));

        when(service.getStats(anyString(), anyString(), any(), any())).thenReturn(testStatsDtoList);

        mvc.perform(
                        get("/stats")
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .params(reqParam)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].app", is(testStatsDtoList.get(0).getApp())))
                .andExpect(jsonPath("$[0].uri", is(testStatsDtoList.get(0).getUri())))
                .andExpect(jsonPath("$[0].hits", is(testStatsDtoList.get(0).getHits()), Long.class))
                .andExpect(jsonPath("$[1].app", is(testStatsDtoList.get(1).getApp())))
                .andExpect(jsonPath("$[1].uri", is(testStatsDtoList.get(1).getUri())))
                .andExpect(jsonPath("$[1].hits", is(testStatsDtoList.get(1).getHits()), Long.class))
                .andExpect(jsonPath("$[2].app", is(testStatsDtoList.get(2).getApp())))
                .andExpect(jsonPath("$[2].uri", is(testStatsDtoList.get(2).getUri())))
                .andExpect(jsonPath("$[2].hits", is(testStatsDtoList.get(2).getHits()), Long.class));
    }

    @Test
    void getStatsWithUrisAndUnique() throws Exception {
        MultiValueMap<String, String> reqParam = new LinkedMultiValueMap<>();

        reqParam.add("start", UriUtils.encodePath(startTime, "UTF-8"));
        reqParam.add("end", UriUtils.encodePath(endTime, "UTF-8"));
        reqParam.add("uris", "test_app_uri");
        reqParam.add("unique", "true");

        when(service.getStats(anyString(), anyString(), any(), any())).thenReturn(testStatsDtoList);

        mvc.perform(
                        get("/stats")
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .params(reqParam)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].app", is(testStatsDtoList.get(0).getApp())))
                .andExpect(jsonPath("$[0].uri", is(testStatsDtoList.get(0).getUri())))
                .andExpect(jsonPath("$[0].hits", is(testStatsDtoList.get(0).getHits()), Long.class))
                .andExpect(jsonPath("$[1].app", is(testStatsDtoList.get(1).getApp())))
                .andExpect(jsonPath("$[1].uri", is(testStatsDtoList.get(1).getUri())))
                .andExpect(jsonPath("$[1].hits", is(testStatsDtoList.get(1).getHits()), Long.class))
                .andExpect(jsonPath("$[2].app", is(testStatsDtoList.get(2).getApp())))
                .andExpect(jsonPath("$[2].uri", is(testStatsDtoList.get(2).getUri())))
                .andExpect(jsonPath("$[2].hits", is(testStatsDtoList.get(2).getHits()), Long.class));
    }

    @Test
    void getStatsWithoutStartTime() throws Exception {
        MultiValueMap<String, String> reqParam = new LinkedMultiValueMap<>();

        reqParam.add("end", UriUtils.encodePath(endTime, "UTF-8"));

        mvc.perform(
                        get("/stats")
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .params(reqParam)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void getStatsWithoutEndTime() throws Exception {
        MultiValueMap<String, String> reqParam = new LinkedMultiValueMap<>();

        reqParam.add("start", UriUtils.encodePath(startTime, "UTF-8"));

        mvc.perform(
                        get("/stats")
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .params(reqParam)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void getStatsWithoutStartAndEndTime() throws Exception {
        MultiValueMap<String, String> reqParam = new LinkedMultiValueMap<>();

        mvc.perform(
                        get("/stats")
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .params(reqParam)
                )
                .andExpect(status().isBadRequest());
    }
}