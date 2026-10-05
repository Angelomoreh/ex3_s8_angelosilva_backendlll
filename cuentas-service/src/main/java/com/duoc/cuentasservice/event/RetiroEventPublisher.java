package com.duoc.cuentasservice.event;

import com.duoc.cuentasservice.exception.OperacionCuentaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RetiroEventPublisher {

    private static final Logger LOGGER = LoggerFactory.getLogger(RetiroEventPublisher.class);

    private final KafkaTemplate<String, RetiroEvent> kafkaTemplate;
    private final String topic;

    public RetiroEventPublisher(
            KafkaTemplate<String, RetiroEvent> kafkaTemplate,
            @Value("${banco.kafka.topic.retiros}") String topic
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    public void publicar(RetiroEvent evento) {
        try {
            kafkaTemplate.send(topic, evento.cuentaId().toString(), evento).get(5, TimeUnit.SECONDS);
            LOGGER.info("Evento Kafka publicado: eventoId={}, cuentaId={}, monto={}",
                    evento.eventoId(), evento.cuentaId(), evento.monto());
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new OperacionCuentaException("No fue posible registrar el retiro en la arquitectura de eventos");
        } catch (Exception exception) {
            throw new OperacionCuentaException("No fue posible registrar el retiro en la arquitectura de eventos");
        }
    }
}
