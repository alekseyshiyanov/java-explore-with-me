package ru.practicum.ewm.mainservice.events.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventRequestStatusUpdateDto {
    @NotNull(message = "Список идентификаторов запросов событий не должен быть равен 'null'")
    @Size(min = 1, message = "Список идентификаторов запросов событий не должен быть пустым")
    private List<Long> requestIds;

    @NotNull(message = "Поле 'status' не может быть равна 'null'")
    private String status;
}
