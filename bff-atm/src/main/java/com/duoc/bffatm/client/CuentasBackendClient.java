package com.duoc.bffatm.client;

import com.duoc.bffatm.dto.RetiroBackendResponse;
import com.duoc.bffatm.dto.RetiroRequest;
import com.duoc.bffatm.dto.SaldoBackendResponse;
import com.duoc.bffatm.exception.BackendException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Component
public class CuentasBackendClient {

    private final RestClient cuentasClient;

    public CuentasBackendClient(@Qualifier("cuentasRestClient") RestClient cuentasClient) {
        this.cuentasClient = cuentasClient;
    }

    @CircuitBreaker(name = "cuentas", fallbackMethod = "fallbackSaldo")
    public SaldoBackendResponse consultarSaldo(Long cuentaId) {
        return ejecutar(() -> cuentasClient.get()
                .uri("/api/cuentas/{id}/saldo", cuentaId)
                .retrieve()
                .body(SaldoBackendResponse.class));
    }

    @CircuitBreaker(name = "cuentas", fallbackMethod = "fallbackRetiro")
    public RetiroBackendResponse retirar(Long cuentaId, RetiroRequest request) {
        return ejecutar(() -> cuentasClient.post()
                .uri("/api/cuentas/{id}/retiros", cuentaId)
                .body(request)
                .retrieve()
                .body(RetiroBackendResponse.class));
    }

    public SaldoBackendResponse fallbackSaldo(Long cuentaId, Throwable error) {
        return new SaldoBackendResponse(cuentaId, BigDecimal.ZERO, "CLP", OffsetDateTime.now());
    }

    public RetiroBackendResponse fallbackRetiro(Long cuentaId, RetiroRequest request, Throwable error) {
        return new RetiroBackendResponse(
                cuentaId,
                request.monto(),
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                "CLP",
                "SERVICIO_NO_DISPONIBLE",
                OffsetDateTime.now()
        );
    }

    private <T> T ejecutar(Operacion<T> operacion) {
        try {
            T respuesta = operacion.ejecutar();
            if (respuesta == null) {
                throw new BackendException(502, "El servicio backend respondió sin contenido");
            }
            return respuesta;
        } catch (RestClientResponseException exception) {
            String mensaje = exception.getStatusCode().value() == 400
                    ? "El retiro no pudo completarse. Revisa el monto y el saldo disponible"
                    : "No fue posible completar la operación solicitada";
            throw new BackendException(exception.getStatusCode().value(), mensaje);
        } catch (ResourceAccessException exception) {
            throw new BackendException(503, "El servicio de cuentas no se encuentra disponible");
        }
    }

    @FunctionalInterface
    private interface Operacion<T> {
        T ejecutar();
    }
}
