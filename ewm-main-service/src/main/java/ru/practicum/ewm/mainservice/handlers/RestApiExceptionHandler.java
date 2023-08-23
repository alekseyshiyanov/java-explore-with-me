package ru.practicum.ewm.mainservice.handlers;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.statsclient.exceptions.ApiErrorException;
import ru.practicum.statsclient.exceptions.ErrorMessage;
import org.springframework.dao.DataIntegrityViolationException;

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class RestApiExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<String> errorMap = new ArrayList<>();

        for (FieldError err : e.getBindingResult().getFieldErrors()) {
            errorMap.add(err.getDefaultMessage());
        }

        var msg = ErrorMessage.buildRestApiErrorResponse(HttpStatus.BAD_REQUEST, errorMap);

        return new ResponseEntity<>(msg, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> handleConstraintViolationException(ConstraintViolationException e) {
        List<String> errorMap = new ArrayList<>();

        for (var cv : e.getConstraintViolations()) {
            errorMap.add(cv.getMessage());
        }

        var msg = ErrorMessage.buildRestApiErrorResponse(HttpStatus.BAD_REQUEST, errorMap);

        return new ResponseEntity<>(msg, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> conflict(DataIntegrityViolationException e) {
        var message = ErrorMessage.buildRestApiErrorResponse(HttpStatus.CONFLICT, e.getMessage());
        return new ResponseEntity<>(message, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ApiErrorException.class)
    public ResponseEntity<?> handleApiErrorException(ApiErrorException e) {
        var errMsg = ErrorMessage.buildRestApiErrorResponse(e.getStatusCode(), e.getMessage());
        return new ResponseEntity<>(errMsg, new HttpHeaders(), e.getStatusCode());
    }
}
