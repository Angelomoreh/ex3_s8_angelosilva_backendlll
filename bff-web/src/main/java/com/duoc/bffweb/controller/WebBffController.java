package com.duoc.bffweb.controller;

import com.duoc.bffweb.client.BancoBackendClient;
import com.duoc.bffweb.dto.WebCuentaResponse;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;

@RestController
@RequestMapping("/api/web")
@CrossOrigin(origins = "*")
public class WebBffController {

    private final BancoBackendClient backendClient;

    public WebBffController(BancoBackendClient backendClient) {
        this.backendClient = backendClient;
    }

    @GetMapping("/cuentas/{cuentaId}")
    public ResponseEntity<WebCuentaResponse> obtenerCuenta(@PathVariable Long cuentaId) {
        WebCuentaResponse respuesta = new WebCuentaResponse(
                "WEB",
                backendClient.obtenerCuenta(cuentaId),
                backendClient.obtenerResumen(cuentaId),
                backendClient.obtenerTransacciones(cuentaId),
                OffsetDateTime.now()
        );

        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore())
                .header("X-BFF-Canal", "WEB")
                .body(respuesta);
    }
}
