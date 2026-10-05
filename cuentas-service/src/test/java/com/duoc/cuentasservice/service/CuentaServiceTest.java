package com.duoc.cuentasservice.service;

import com.duoc.cuentasservice.dto.RetiroRequest;
import com.duoc.cuentasservice.exception.OperacionCuentaException;
import com.duoc.cuentasservice.event.RetiroEventPublisher;
import com.duoc.cuentasservice.model.Cuenta;
import com.duoc.cuentasservice.repository.CuentaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CuentaServiceTest {

    @Mock
    private CuentaRepository cuentaRepository;

    @Mock
    private RetiroEventPublisher retiroEventPublisher;

    @Test
    void rechazaRetiroCuandoElSaldoEsInsuficiente() throws Exception {
        Cuenta cuenta = new Cuenta();
        asignar(cuenta, "id", 101L);
        asignar(cuenta, "saldo", new BigDecimal("1000.00"));
        asignar(cuenta, "activa", true);
        when(cuentaRepository.buscarPorIdParaActualizar(101L)).thenReturn(Optional.of(cuenta));

        CuentaService service = new CuentaService(cuentaRepository, retiroEventPublisher);

        assertThrows(
                OperacionCuentaException.class,
                () -> service.retirar(101L, new RetiroRequest(new BigDecimal("1500.00")))
        );
    }

    private void asignar(Cuenta cuenta, String campo, Object valor) throws Exception {
        Field field = Cuenta.class.getDeclaredField(campo);
        field.setAccessible(true);
        field.set(cuenta, valor);
    }
}
