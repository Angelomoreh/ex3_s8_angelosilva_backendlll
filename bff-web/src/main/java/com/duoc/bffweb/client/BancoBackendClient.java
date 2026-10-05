package com.duoc.bffweb.client;

import com.duoc.bffweb.dto.BackendDtos;
import com.duoc.bffweb.exception.BackendException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.List;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Component
public class BancoBackendClient {

    private final RestClient cuentasClient;
    private final RestClient transaccionesClient;

    public BancoBackendClient(
            @Qualifier("cuentasRestClient") RestClient cuentasClient,
            @Qualifier("transaccionesRestClient") RestClient transaccionesClient
    ) {
        this.cuentasClient = cuentasClient;
        this.transaccionesClient = transaccionesClient;
    }

    @CircuitBreaker(name = "cuentas", fallbackMethod = "fallbackCuenta")
    public BackendDtos.Cuenta obtenerCuenta(Long cuentaId) {
        return ejecutar(() -> cuentasClient.get()
                .uri("/api/cuentas/{id}", cuentaId)
                .retrieve()
                .body(BackendDtos.Cuenta.class));
    }

    @CircuitBreaker(name = "transacciones", fallbackMethod = "fallbackTransacciones")
    public List<BackendDtos.Transaccion> obtenerTransacciones(Long cuentaId) {
        return ejecutar(() -> transaccionesClient.get()
                .uri("/api/transacciones/cuenta/{id}", cuentaId)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                }));
    }

    @CircuitBreaker(name = "transacciones", fallbackMethod = "fallbackResumen")
    public BackendDtos.Resumen obtenerResumen(Long cuentaId) {
        return ejecutar(() -> transaccionesClient.get()
                .uri("/api/transacciones/cuenta/{id}/resumen", cuentaId)
                .retrieve()
                .body(BackendDtos.Resumen.class));
    }

    public BackendDtos.Cuenta fallbackCuenta(Long cuentaId, Throwable error) {
        return new BackendDtos.Cuenta(
                cuentaId,
                "SERVICIO DE CUENTAS NO DISPONIBLE",
                BigDecimal.ZERO,
                0,
                "NO_DISPONIBLE",
                false,
                "CLP",
                OffsetDateTime.now()
        );
    }

    public List<BackendDtos.Transaccion> fallbackTransacciones(Long cuentaId, Throwable error) {
        return List.of();
    }

    public BackendDtos.Resumen fallbackResumen(Long cuentaId, Throwable error) {
        return new BackendDtos.Resumen(cuentaId, 0, BigDecimal.ZERO, BigDecimal.ZERO, 0);
    }

    private <T> T ejecutar(Operacion<T> operacion) {
        try {
            T respuesta = operacion.ejecutar();
            if (respuesta == null) {
                throw new BackendException(502, "El servicio backend respondió sin contenido");
            }
            return respuesta;
        } catch (RestClientResponseException exception) {
            throw new BackendException(exception.getStatusCode().value(), "No fue posible obtener la información solicitada");
        } catch (ResourceAccessException exception) {
            throw new BackendException(503, "Uno de los servicios del banco no se encuentra disponible");
        }
    }

    @FunctionalInterface
    private interface Operacion<T> {
        T ejecutar();
    }
}
