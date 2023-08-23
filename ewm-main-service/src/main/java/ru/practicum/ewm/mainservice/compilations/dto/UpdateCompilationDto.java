package ru.practicum.ewm.mainservice.compilations.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCompilationDto {
    private Boolean pinned;

    @Size(min = 1, max = 50, message = "Длина строки названия подборки должна быть от 1 до 50 символов")
    private String title;

    private List<Long> events;
}