package com.duoc.bffmobile.controller;

import com.duoc.bffmobile.client.BancoBackendClient;
import com.duoc.bffmobile.dto.BackendDtos;
import com.duoc.bffmobile.dto.MobileCuentaResponse;
import com.duoc.bffmobile.dto.MobileMovimientoResponse;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/mobile")
@CrossOrigin(origins = "*")
public class MobileBffController {

    private final BancoBackendClient backendClient;

    public MobileBffController(BancoBackendClient backendClient) {
        this.backendClient = backendClient;
    }

    @GetMapping("/cuentas/{cuentaId}")
    public ResponseEntity<MobileCuentaResponse> obtenerCuenta(@PathVariable Long cuentaId) {
        BackendDtos.Cuenta cuenta = backendClient.obtenerCuenta(cuentaId);
        BackendDtos.Resumen resumen = backendClient.obtenerResumen(cuentaId);
        List<MobileMovimientoResponse> movimientos = backendClient.obtenerUltimas(cuentaId).stream()
                .map(item -> new MobileMovimientoResponse(item.fecha(), item.tipo(), item.monto()))
                .toList();

        MobileCuentaResponse respuesta = new MobileCuentaResponse(
                "MOBILE",
                cuenta.id(),
                cuenta.nombre(),
                cuenta.tipo(),
                cuenta.saldo(),
                cuenta.moneda(),
                resumen.cantidadMovimientos(),
                movimientos
        );

        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore())
                .header("X-BFF-Canal", "MOBILE")
                .body(respuesta);
    }
}
