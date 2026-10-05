package com.duoc.bffweb.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;
import java.util.Map;

@RestControllerAdvice
public class BffExceptionHandler {

    @ExceptionHandler(BackendException.class)
    public ResponseEntity<Map<String, Object>> manejarBackend(
            BackendException exception,
            HttpServletRequest request
    ) {
        return ResponseEntity.status(exception.getStatus()).body(Map.of(
                "estado", exception.getStatus(),
                "mensaje", exception.getMessage(),
                "ruta", request.getRequestURI(),
                "fecha", OffsetDateTime.now().toString()
        ));
    }
}
