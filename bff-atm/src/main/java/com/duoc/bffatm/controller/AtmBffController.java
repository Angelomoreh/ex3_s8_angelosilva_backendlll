package com.duoc.bffatm.controller;

import com.duoc.bffatm.client.CuentasBackendClient;
import com.duoc.bffatm.dto.AtmRetiroResponse;
import com.duoc.bffatm.dto.AtmSaldoResponse;
import com.duoc.bffatm.dto.RetiroBackendResponse;
import com.duoc.bffatm.dto.RetiroRequest;
import com.duoc.bffatm.dto.SaldoBackendResponse;
import jakarta.validation.Valid;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/atm")
public class AtmBffController {

    private final CuentasBackendClient backendClient;

    public AtmBffController(CuentasBackendClient backendClient) {
        this.backendClient = backendClient;
    }

    @GetMapping("/cuentas/{cuentaId}/saldo")
    public ResponseEntity<AtmSaldoResponse> consultarSaldo(@PathVariable Long cuentaId) {
        SaldoBackendResponse saldo = backendClient.consultarSaldo(cuentaId);
        AtmSaldoResponse respuesta = new AtmSaldoResponse(
                "ATM",
                saldo.cuentaId(),
                saldo.saldo(),
                saldo.moneda()
        );
        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore())
                .header("X-BFF-Canal", "ATM")
                .body(respuesta);
    }

    @PostMapping("/cuentas/{cuentaId}/retiros")
    public ResponseEntity<AtmRetiroResponse> retirar(
            @PathVariable Long cuentaId,
            @Valid @RequestBody RetiroRequest request
    ) {
        RetiroBackendResponse retiro = backendClient.retirar(cuentaId, request);
        AtmRetiroResponse respuesta = new AtmRetiroResponse(
                "ATM",
                retiro.cuentaId(),
                retiro.montoRetirado(),
                retiro.saldoActual(),
                retiro.moneda(),
                retiro.estado(),
                retiro.fecha()
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .cacheControl(CacheControl.noStore())
                .header("X-BFF-Canal", "ATM")
                .body(respuesta);
    }
}
