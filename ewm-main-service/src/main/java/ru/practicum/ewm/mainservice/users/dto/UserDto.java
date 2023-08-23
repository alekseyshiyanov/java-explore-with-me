package ru.practicum.ewm.mainservice.users.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;

    @NotNull(message = "Поле 'email' не должно быть равно null")
    @Size(min = 6, max = 254, message = "Поле 'email' должно содержать не менее 6 и не более 254 символов")
    @Email(message = "Проверьте правильность ввода адреса электронной почты")
    private String email;

    @NotBlank(message = "Поле 'name' не должно быть равно null или быть пустым")
    @Size(min = 2, max = 250, message = "Поле 'name' должно содержать не менее 2 и не более 250 символов")
    private String name;
}
