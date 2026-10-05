package com.duoc.cuentasservice.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ApiError> manejarNoEncontrado(
            RecursoNoEncontradoException exception,
            HttpServletRequest request
    ) {
        return respuesta(HttpStatus.NOT_FOUND, exception.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(OperacionCuentaException.class)
    public ResponseEntity<ApiError> manejarOperacion(
            OperacionCuentaException exception,
            HttpServletRequest request
    ) {
        return respuesta(HttpStatus.BAD_REQUEST, exception.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> manejarValidacion(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {
        String mensaje = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return respuesta(HttpStatus.BAD_REQUEST, mensaje, request.getRequestURI());
    }

    private ResponseEntity<ApiError> respuesta(HttpStatus status, String mensaje, String ruta) {
        return ResponseEntity.status(status).body(new ApiError(
                status.value(), status.getReasonPhrase(), mensaje, ruta, OffsetDateTime.now()
        ));
    }
}
