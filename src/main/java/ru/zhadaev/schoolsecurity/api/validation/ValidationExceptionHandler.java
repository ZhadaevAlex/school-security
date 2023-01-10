package ru.zhadaev.schoolsecurity.api.validation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.sql.Timestamp;

@RestControllerAdvice
@Slf4j
public class ValidationExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ValidationError> onConstraintViolationException(ConstraintViolationException ex) {
        log.error(ex.getMessage(), ex);
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ValidationError response = new ValidationError();
        response.setTimestamp(new Timestamp(System.currentTimeMillis()));
        response.setStatus(status.getReasonPhrase());
        ex.getConstraintViolations().forEach(violation ->
                response.getViolations().add(new Violation(
                        violation.getMessage(),
                        violation.getPropertyPath().toString(),
                        violation.getInvalidValue())));
        return ResponseEntity.status(status)
                .body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationError> onMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.error(ex.getMessage(), ex);
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ValidationError response = new ValidationError();
        response.setTimestamp(new Timestamp(System.currentTimeMillis()));
        response.setStatus(status.getReasonPhrase());
        ex.getBindingResult().getFieldErrors().forEach(fieldError ->
                response.getViolations().add(new Violation(
                        fieldError.getDefaultMessage(),
                        fieldError.getObjectName(),
                        fieldError.getRejectedValue())));
        return ResponseEntity.status(status)
                .body(response);
    }
}
