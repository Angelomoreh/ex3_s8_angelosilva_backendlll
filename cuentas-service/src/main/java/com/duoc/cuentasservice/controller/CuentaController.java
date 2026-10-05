package com.duoc.cuentasservice.controller;

import com.duoc.cuentasservice.dto.CuentaResponse;
import com.duoc.cuentasservice.dto.RetiroRequest;
import com.duoc.cuentasservice.dto.RetiroResponse;
import com.duoc.cuentasservice.dto.SaldoResponse;
import com.duoc.cuentasservice.service.CuentaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/cuentas")
public class CuentaController {

    private final CuentaService cuentaService;

    public CuentaController(CuentaService cuentaService) {
        this.cuentaService = cuentaService;
    }

    @GetMapping
    public List<CuentaResponse> listar() {
        return cuentaService.listar();
    }

    @GetMapping("/{id}")
    public CuentaResponse buscar(@PathVariable Long id) {
        return cuentaService.buscar(id);
    }

    @GetMapping("/{id}/saldo")
    public SaldoResponse consultarSaldo(@PathVariable Long id) {
        return cuentaService.consultarSaldo(id);
    }

    @PostMapping("/{id}/retiros")
    @ResponseStatus(HttpStatus.CREATED)
    public RetiroResponse retirar(@PathVariable Long id, @Valid @RequestBody RetiroRequest request) {
        return cuentaService.retirar(id, request);
    }
}
