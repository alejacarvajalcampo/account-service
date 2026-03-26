# Account Service

Microservicio encargado de la gestion de `Cuenta`, `Movimiento` y `Reporte de estado de cuenta` dentro de la prueba tecnica. Expone el CRUD de cuentas y movimientos, genera reportes por cliente y rango de fechas, y consume eventos asincronos publicados por `customer-service`.

## Que hace esta aplicacion

- administra cuentas bancarias
- registra movimientos de deposito y retiro
- actualiza saldo disponible
- genera reportes de estado de cuenta
- consume eventos de clientes desde RabbitMQ
- mantiene una tabla local `clientes_referencia`
- documenta la API con Swagger/OpenAPI
- incluye pruebas unitarias e integracion

## Stack

- Java 17
- Spring Boot 3
- Spring Data JPA
- MySQL
- RabbitMQ
- Spring Retry
- MapStruct
- Gradle
- Docker / Docker Compose

## Puertos y URLs importantes

- API: `http://localhost:8082`
- Swagger UI: `http://localhost:8082/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8082/v3/api-docs`
- MySQL: `localhost:3307`

## Endpoints principales

Cuentas:

- `GET /cuentas?page=0&size=10`
- `GET /cuentas/{numeroCuenta}`
- `POST /cuentas`
- `PUT /cuentas/{numeroCuenta}`
- `DELETE /cuentas/{numeroCuenta}`

Movimientos:

- `GET /movimientos?page=0&size=10`
- `GET /movimientos/{movimientoId}`
- `POST /movimientos`
- `PUT /movimientos/{movimientoId}`
- `DELETE /movimientos/{movimientoId}`

Reportes:

- `GET /reportes?clienteId=2&fechaDesde=2022-02-08&fechaHasta=2022-02-10`

## Como ejecutar

Requisitos:

- tener Docker Desktop levantado
- tener `customer-service` arriba si quieres validar sincronizacion asincrona real

Desde la raiz del proyecto:

```powershell
docker compose down -v
docker compose up --build
```

Si quieres correr pruebas:

```powershell
.\gradlew test
```

## Ejemplos rapidos de prueba

Listar cuentas:

```http
GET http://localhost:8082/cuentas?page=0&size=10
```

Crear cuenta:

```http
POST http://localhost:8082/cuentas
Content-Type: application/json
```

```json
{
  "numeroCuenta": 585545,
  "tipoCuenta": "Corriente",
  "saldoInicial": 1000.00,
  "estado": true,
  "clienteId": 2
}
```

Registrar movimiento:

```http
POST http://localhost:8082/movimientos
Content-Type: application/json
```

```json
{
  "fecha": "2022-02-12",
  "tipoMovimiento": "DEPOSITO",
  "valor": 200.00,
  "numeroCuenta": 225487
}
```

Generar reporte:

```http
GET http://localhost:8082/reportes?clienteId=2&fechaDesde=2022-02-08&fechaHasta=2022-02-10
```

## Resiliencia y consistencia

Este servicio consume eventos publicados por `customer-service` para mantener una referencia local de clientes.

La cola principal usa:

- reintentos del listener
- dead-letter queue para mensajes que fallan repetidamente

Esto permite tolerar errores transitorios sin perder mensajes rapidamente.

## RabbitMQ

- exchange principal: `customer.exchange`
- routing key principal: `customer.sync`
- cola principal: `customer.account.queue`
- dead-letter exchange: `customer.exchange.dlx`
- dead-letter queue: `customer.account.queue.dlq`

## Consistencia eventual

La creacion de cuentas depende de que el cliente exista en `clientes_referencia`.

Flujo esperado:

1. `customer-service` crea o actualiza el cliente.
2. `customer-service` registra y publica el evento mediante outbox.
3. `account-service` consume el evento y sincroniza `clientes_referencia`.
4. La cuenta ya puede crearse usando ese `clienteId`.

## Rendimiento

El recalculo de saldo se realiza en una sola pasada ordenada por cuenta, fecha y `movimiento_id`, evitando una simulacion previa separada. Tambien se dejo un indice compuesto para mejorar consultas y recalcule sobre movimientos.

## Base de datos

El esquema inicial esta en `BaseDatos.sql`.

Tablas principales:

- `clientes_referencia`
- `cuentas`
- `movimientos`

