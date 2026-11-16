package io.github.songuj.meetingroom.controller;

import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.github.songuj.meetingroom.dto.ErrorResDTO;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResDTO conflict(IllegalStateException e) {
        return new ErrorResDTO(e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResDTO badRequest(IllegalArgumentException e) {
        return new ErrorResDTO(e.getMessage());
    }

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResDTO notFound(NoSuchElementException e) {
        return new ErrorResDTO("예약을 찾을 수 없습니다.");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResDTO invalid(MethodArgumentNotValidException e) {
        String field = e.getBindingResult().getFieldErrors().get(0).getField();
        return new ErrorResDTO(field + " 값이 비어 있습니다.");
    }
}
