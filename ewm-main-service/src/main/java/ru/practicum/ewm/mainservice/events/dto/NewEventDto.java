package ru.practicum.ewm.mainservice.events.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import ru.practicum.ewm.mainservice.model.Locations;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewEventDto {
    @NotBlank(message = "Краткое описание события не должно быть null или состоять только из пробелов")
    @Size(min = 20, max = 2000, message = "Длина краткого описания события должна быть от 20 до 2000 символов")
    private String annotation;

    @Positive(message = "Идентификатор категории события должен быть положительным числом больше 0")
    private Long category;

    @NotBlank(message = "Полное описание события не должно быть null или состоять только из пробелов")
    @Size(min = 20, max = 7000, message = "Длина полного описания события должна быть от 20 до 7000 символов")
    private String description;

    @NotNull(message = "Дата и время проведения события не должна быть равна null")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    @NotNull(message = "Место проведения события не должно быть null")
    private Locations location;

    private Boolean paid = false;

    @PositiveOrZero(message = "Количество участников должно быть 0 или положительным числом")
    private Integer participantLimit = 0;

    private Boolean requestModeration = true;

    @NotBlank(message = "Заголовок события не должен быть null или состоять только из пробелов")
    @Size(min = 3, max = 120, message = "Заголовок события должен быть от 3 до 120 символов")
    private String title;

    private String stateAction;
}
