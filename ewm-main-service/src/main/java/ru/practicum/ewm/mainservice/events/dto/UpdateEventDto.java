package ru.practicum.ewm.mainservice.events.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.mainservice.model.Locations;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEventDto {
    @Size(min = 20, max = 2000, message = "Длина краткого описания события должна быть от 20 до 2000 символов")
    private String annotation;

    @Positive(message = "Идентификатор категории события должен быть положительным числом")
    private Long category;

    @Size(min = 20, max = 7000, message = "Длина полного описания события должна быть от 20 до 7000 символов")
    private String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    private Locations location;

    private Boolean paid;

    @PositiveOrZero(message = "Количество участников должно быть 0 или положительным числом")
    private Integer participantLimit;

    private Boolean requestModeration;

    private String stateAction;

    @Size(min = 3, max = 120, message = "Заголовок события должен быть от 3 до 120 символов")
    private String title;
}
