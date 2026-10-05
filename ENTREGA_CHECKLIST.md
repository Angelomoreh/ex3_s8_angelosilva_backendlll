# Checklist de entrega Semana 8

## Implementacion evaluada

- [x] OAuth2 funcional mediante Keycloak y JWT.
- [x] Gateway protegido y respuesta HTTP 401 sin token.
- [x] Imagen Docker independiente para cada microservicio.
- [x] Archivo `compose.yaml` con todos los componentes.
- [x] Circuit Breaker y fallback con Resilience4j.
- [x] Kafka integrado entre cuentas y transacciones.
- [x] Config Server con repositorio de configuracion nativo.
- [x] Eureka con registro y descubrimiento de servicios.
- [x] PostgreSQL con datos bancarios legacy.
- [x] Codigo fuente y pruebas unitarias.
- [x] README con instrucciones completas.
- [x] Guia para reunir evidencias.

## Archivos que se deben entregar

- [ ] Repositorio GitHub publico actualizado.
- [ ] Capturas copiadas a la carpeta `evidencias`.
- [ ] Salidas generadas por `probar-apis.cmd`.
- [ ] ZIP final llamado `Exp3_S8_Angelo_Silva.zip`.

## Distribucion de la pauta

| Criterio | Puntaje maximo | Evidencia principal |
|---|---:|---|
| OAuth2 | 20 | Pruebas 01 y 02 |
| Imagenes Docker | 20 | `docker compose images` y Dockerfile |
| Docker Compose | 20 | `compose.yaml` y `docker compose ps` |
| Resilience4j | 20 | Prueba 13 |
| Kafka | 15 | Pruebas 09 a 12 |
| Codigo, documentacion y evidencias | 5 | Repositorio, README y carpeta evidencias |
| **Total** | **100** | |
