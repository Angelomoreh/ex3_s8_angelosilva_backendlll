package com.duoc.transaccionesservice.service;

import com.duoc.transaccionesservice.dto.ResumenTransaccionesResponse;
import com.duoc.transaccionesservice.dto.TransaccionResponse;
import com.duoc.transaccionesservice.model.Transaccion;
import com.duoc.transaccionesservice.repository.TransaccionRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TransaccionService {

    private final TransaccionRepository transaccionRepository;

    public TransaccionService(TransaccionRepository transaccionRepository) {
        this.transaccionRepository = transaccionRepository;
    }

    @Transactional(readOnly = true)
    public List<TransaccionResponse> listarPorCuenta(Long cuentaId) {
        return transaccionRepository.findByCuentaIdOrderByFechaDescIdDesc(cuentaId).stream()
                .map(this::aRespuesta)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TransaccionResponse> listarUltimas(Long cuentaId, int limite) {
        int limiteSeguro = Math.max(1, Math.min(limite, 20));
        return transaccionRepository
                .findByCuentaIdOrderByFechaDescIdDesc(cuentaId, PageRequest.of(0, limiteSeguro))
                .stream()
                .map(this::aRespuesta)
                .toList();
    }

    @Transactional(readOnly = true)
    public ResumenTransaccionesResponse obtenerResumen(Long cuentaId) {
        List<Transaccion> transacciones = transaccionRepository.findByCuentaIdOrderByFechaDescIdDesc(cuentaId);
        BigDecimal creditos = sumarPorTipo(transacciones, "CREDITO");
        BigDecimal debitos = sumarPorTipo(transacciones, "DEBITO");
        long anomalias = transacciones.stream().filter(Transaccion::getAnomalia).count();
        return new ResumenTransaccionesResponse(
                cuentaId,
                transacciones.size(),
                creditos,
                debitos,
                anomalias
        );
    }

    private BigDecimal sumarPorTipo(List<Transaccion> transacciones, String tipo) {
        return transacciones.stream()
                .filter(transaccion -> tipo.equals(transaccion.getTipo()))
                .map(Transaccion::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private TransaccionResponse aRespuesta(Transaccion transaccion) {
        return new TransaccionResponse(
                transaccion.getId(),
                transaccion.getCuentaId(),
                transaccion.getFecha(),
                transaccion.getTipo(),
                transaccion.getMonto(),
                transaccion.getDescripcion(),
                transaccion.getCanal(),
                transaccion.getAnomalia()
        );
    }
}
