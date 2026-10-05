package com.duoc.transaccionesservice.service;

import com.duoc.transaccionesservice.dto.ResumenTransaccionesResponse;
import com.duoc.transaccionesservice.repository.TransaccionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransaccionServiceTest {

    @Mock
    private TransaccionRepository transaccionRepository;

    @Test
    void entregaResumenVacioCuandoLaCuentaNoTieneMovimientos() {
        when(transaccionRepository.findByCuentaIdOrderByFechaDescIdDesc(999L)).thenReturn(List.of());
        TransaccionService service = new TransaccionService(transaccionRepository);

        ResumenTransaccionesResponse respuesta = service.obtenerResumen(999L);

        assertEquals(0, respuesta.cantidadMovimientos());
        assertEquals(0, respuesta.totalCreditos().signum());
        assertEquals(0, respuesta.totalDebitos().signum());
    }
}
