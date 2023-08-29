package ru.practicum.statsclient.exceptions;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class ErrorMessage {

    public static Map<String, Object> buildRestApiErrorResponse(HttpStatus httpStatus, Object msg) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        Map<String, Object> errorMsg = new HashMap<>();

        errorMsg.put("status", String.valueOf(httpStatus.value()));
        errorMsg.put("reason", httpStatus.getReasonPhrase());
        errorMsg.put("message", msg);
        errorMsg.put("timestamp", LocalDateTime.now().format(dtf));

        return errorMsg;
    }
}
