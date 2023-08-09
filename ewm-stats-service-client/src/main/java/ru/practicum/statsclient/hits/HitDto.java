package ru.practicum.statsclient.hits;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonFormat;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HitDto {

    @NotBlank(message = "Имя сервиса не может быть пустым или равным null")
    String app;

    @NotBlank(message = "Строка URI не может быть пустым или равным null")
    String uri;

    @NotBlank(message = "Строка IP адреса не может быть пустым или равным null")
    String ip;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime timestamp;
}
