# Guia de pruebas Semana 8

## Preparacion

1. Iniciar Docker Desktop.
2. Abrir la carpeta del proyecto en Visual Studio Code.
3. Abrir una terminal PowerShell en la raiz del proyecto.
4. Ejecutar `./iniciar-todo.cmd`.
5. Esperar el mensaje `ARQUITECTURA BANCO XYZ DISPONIBLE`.

La primera construccion puede tardar varios minutos. No se debe cerrar la terminal mientras Docker se encuentre descargando o construyendo imagenes.

## Pruebas automaticas

Ejecutar:

```powershell
.\probar-apis.cmd
```

El script realiza las siguientes comprobaciones:

1. Rechazo con HTTP 401 al consultar el Gateway sin token.
2. Obtencion de token OAuth2 desde Keycloak.
3. Lectura de configuracion desde Config Server.
4. Consulta de servicios registrados en Eureka.
5. Respuestas de BFF Web, Mobile y ATM mediante el Gateway.
6. Rechazo del ATM cuando falta `X-ATM-KEY`.
7. Retiro bancario y publicacion de evento Kafka.
8. Consumo del evento y registro en PostgreSQL.
9. Detencion temporal de transacciones y respuesta fallback con Resilience4j.

Las salidas se guardan en `evidencias/salidas`.

## Capturas recomendadas

1. `docker compose ps` con todos los componentes activos.
2. Panel de Eureka con los microservicios registrados.
3. Respuesta de Config Server para `bff-web`.
4. Prueba HTTP 401 sin token y confirmacion de token obtenido.
5. Respuesta del BFF Web mediante el Gateway.
6. Respuesta resumida del BFF Mobile.
7. Saldo y retiro del BFF ATM.
8. Logs de Kafka mostrando el evento publicado y consumido.
9. Consulta PostgreSQL con el evento y la transaccion.
10. Respuesta fallback mientras `transacciones-service` se encuentra detenido.

## URLs para evidencias

| Evidencia | URL |
|---|---|
| Eureka | `http://localhost:8761` |
| Config Server | `http://localhost:8888/bff-web/default` |
| Keycloak | `http://localhost:8180` |
| Salud del Gateway | `http://localhost:8080/actuator/health` |

Las rutas `/api/**` del Gateway requieren un token, por lo que se recomienda usar el script o Postman.

## Detener el proyecto

Al finalizar las capturas:

```powershell
.\detener-todo.cmd
```

Este comando detiene y elimina los contenedores, pero conserva el volumen de PostgreSQL. Para comenzar nuevamente con los datos originales se puede ejecutar `scripts/reiniciar-base-datos.ps1`.
