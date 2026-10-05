package com.duoc.transaccionesservice.controller;

import com.duoc.transaccionesservice.dto.ResumenTransaccionesResponse;
import com.duoc.transaccionesservice.dto.TransaccionResponse;
import com.duoc.transaccionesservice.service.TransaccionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/transacciones")
public class TransaccionController {

    private final TransaccionService transaccionService;

    public TransaccionController(TransaccionService transaccionService) {
        this.transaccionService = transaccionService;
    }

    @GetMapping("/cuenta/{cuentaId}")
    public List<TransaccionResponse> listarPorCuenta(@PathVariable Long cuentaId) {
        return transaccionService.listarPorCuenta(cuentaId);
    }

    @GetMapping("/cuenta/{cuentaId}/ultimas")
    public List<TransaccionResponse> listarUltimas(
            @PathVariable Long cuentaId,
            @RequestParam(defaultValue = "5") int limite
    ) {
        return transaccionService.listarUltimas(cuentaId, limite);
    }

    @GetMapping("/cuenta/{cuentaId}/resumen")
    public ResumenTransaccionesResponse obtenerResumen(@PathVariable Long cuentaId) {
        return transaccionService.obtenerResumen(cuentaId);
    }
}
