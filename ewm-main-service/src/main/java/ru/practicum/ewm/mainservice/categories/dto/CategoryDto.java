package ru.practicum.ewm.mainservice.categories.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDto {
    private Long id;

    @NotBlank(message = "Название категории не должно быть пустым или 'null'")
    @Size(max = 50, message = "Название категории не должно состоять более чем из 50 символов")
    private String name;
}
