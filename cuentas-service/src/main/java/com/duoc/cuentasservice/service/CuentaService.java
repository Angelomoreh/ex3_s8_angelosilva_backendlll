package com.duoc.cuentasservice.service;

import com.duoc.cuentasservice.dto.CuentaResponse;
import com.duoc.cuentasservice.dto.RetiroRequest;
import com.duoc.cuentasservice.dto.RetiroResponse;
import com.duoc.cuentasservice.dto.SaldoResponse;
import com.duoc.cuentasservice.exception.OperacionCuentaException;
import com.duoc.cuentasservice.exception.RecursoNoEncontradoException;
import com.duoc.cuentasservice.event.RetiroEvent;
import com.duoc.cuentasservice.event.RetiroEventPublisher;
import com.duoc.cuentasservice.model.Cuenta;
import com.duoc.cuentasservice.repository.CuentaRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class CuentaService {

    private final CuentaRepository cuentaRepository;
    private final RetiroEventPublisher retiroEventPublisher;

    public CuentaService(CuentaRepository cuentaRepository, RetiroEventPublisher retiroEventPublisher) {
        this.cuentaRepository = cuentaRepository;
        this.retiroEventPublisher = retiroEventPublisher;
    }

    @Transactional(readOnly = true)
    public List<CuentaResponse> listar() {
        return cuentaRepository.findAll(Sort.by("id")).stream()
                .map(this::aRespuesta)
                .toList();
    }

    @Transactional(readOnly = true)
    public CuentaResponse buscar(Long id) {
        return aRespuesta(buscarEntidad(id));
    }

    @Transactional(readOnly = true)
    public SaldoResponse consultarSaldo(Long id) {
        Cuenta cuenta = buscarEntidad(id);
        return new SaldoResponse(cuenta.getId(), cuenta.getSaldo(), cuenta.getMoneda(), OffsetDateTime.now());
    }

    @Transactional
    public RetiroResponse retirar(Long id, RetiroRequest request) {
        Cuenta cuenta = cuentaRepository.buscarPorIdParaActualizar(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la cuenta " + id));

        if (!cuenta.getActiva()) {
            throw new OperacionCuentaException("La cuenta se encuentra inactiva");
        }
        if (request.monto().compareTo(cuenta.getSaldo()) > 0) {
            throw new OperacionCuentaException("Saldo insuficiente para realizar el retiro");
        }

        BigDecimal saldoAnterior = cuenta.getSaldo();
        BigDecimal saldoActual = saldoAnterior.subtract(request.monto());
        cuenta.setSaldo(saldoActual);

        retiroEventPublisher.publicar(new RetiroEvent(
                UUID.randomUUID().toString(),
                id,
                request.monto(),
                "CAJERO",
                OffsetDateTime.now()
        ));

        return new RetiroResponse(
                id,
                request.monto(),
                saldoAnterior,
                saldoActual,
                cuenta.getMoneda(),
                "APROBADO",
                OffsetDateTime.now()
        );
    }

    private Cuenta buscarEntidad(Long id) {
        return cuentaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la cuenta " + id));
    }

    private CuentaResponse aRespuesta(Cuenta cuenta) {
        return new CuentaResponse(
                cuenta.getId(),
                cuenta.getNombre(),
                cuenta.getSaldo(),
                cuenta.getEdad(),
                cuenta.getTipo(),
                cuenta.getActiva(),
                cuenta.getMoneda(),
                cuenta.getFechaActualizacion()
        );
    }
}
