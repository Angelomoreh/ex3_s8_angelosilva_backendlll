package com.duoc.transaccionesservice.repository;

import com.duoc.transaccionesservice.model.Transaccion;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransaccionRepository extends JpaRepository<Transaccion, Long> {

    List<Transaccion> findByCuentaIdOrderByFechaDescIdDesc(Long cuentaId);

    List<Transaccion> findByCuentaIdOrderByFechaDescIdDesc(Long cuentaId, Pageable pageable);
}
