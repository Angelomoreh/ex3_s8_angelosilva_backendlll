package com.duoc.transaccionesservice.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class RetiroEventConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(RetiroEventConsumer.class);

    private final ObjectMapper objectMapper;
    private final JdbcTemplate jdbcTemplate;

    public RetiroEventConsumer(ObjectMapper objectMapper, JdbcTemplate jdbcTemplate) {
        this.objectMapper = objectMapper;
        this.jdbcTemplate = jdbcTemplate;
    }

    @KafkaListener(topics = "${banco.kafka.topic.retiros}")
    @Transactional
    public void procesar(String payload) throws JsonProcessingException {
        RetiroEvent evento = objectMapper.readValue(payload, RetiroEvent.class);

        int nuevoEvento = jdbcTemplate.update(
                """
                INSERT INTO eventos_procesados (evento_id, tipo_evento, procesado_en)
                VALUES (?, 'RETIRO_REALIZADO', CURRENT_TIMESTAMP)
                ON CONFLICT (evento_id) DO NOTHING
                """,
                evento.eventoId()
        );

        if (nuevoEvento == 0) {
            LOGGER.info("Evento Kafka duplicado ignorado: eventoId={}", evento.eventoId());
            return;
        }

        jdbcTemplate.update(
                """
                INSERT INTO transacciones
                    (cuenta_id, fecha, tipo, monto, descripcion, canal, anomalia)
                VALUES (?, ?, 'DEBITO', ?, 'Retiro procesado mediante evento Kafka', ?, FALSE)
                """,
                evento.cuentaId(),
                evento.fecha().toLocalDate(),
                evento.monto(),
                evento.canal()
        );

        LOGGER.info("Evento Kafka procesado: eventoId={}, cuentaId={}, monto={}",
                evento.eventoId(), evento.cuentaId(), evento.monto());
    }
}
