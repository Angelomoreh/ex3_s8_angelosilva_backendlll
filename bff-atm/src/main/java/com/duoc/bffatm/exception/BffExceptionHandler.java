package com.duoc.bffatm.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class BffExceptionHandler {

    @ExceptionHandler(BackendException.class)
    public ResponseEntity<Map<String, Object>> manejarBackend(
            BackendException exception,
            HttpServletRequest request
    ) {
        return respuesta(exception.getStatus(), exception.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> manejarValidacion(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {
        String mensaje = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return respuesta(HttpStatus.BAD_REQUEST.value(), mensaje, request.getRequestURI());
    }

    private ResponseEntity<Map<String, Object>> respuesta(int status, String mensaje, String ruta) {
        return ResponseEntity.status(status).body(Map.of(
                "estado", status,
                "mensaje", mensaje,
                "ruta", ruta,
                "fecha", OffsetDateTime.now().toString()
        ));
    }
}
