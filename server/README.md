# Manufactura Microservices - Backend

Este repositorio contiene la arquitectura de microservicios para el Sistema de Control de Calidad de Manufactura.

## 🏗 Arquitectura

El sistema sigue un patrón de **Microservicios** comunicados de forma híbrida:
1. **Síncrona (HTTP/REST):** Para peticiones directas entre servicios (ej. obtener tipo de cambio).
2. **Asíncrona (Eventos):** Usando **RabbitMQ** para desacoplar productores y consumidores (ej. creación de defectos -> generación de alertas).

### Servicios

| Servicio | Puerto | Descripción | Tecnologías |
|----------|--------|-------------|-------------|
| **Gateway** | `3000` | Punto de entrada único. Redirige tráfico al servicio correspondiente. | Express, http-proxy-middleware |
| **Defects** | `3001` | **Productor**. Registra defectos y publica eventos `defect.created`. | Express, Prisma, RabbitMQ |
| **Currency** | `3002` | Provee tipo de cambio (Mock). | Express |
| **Alerts** | `3003` | **Consumidor**. Escucha `defect.created` y genera alertas si cumplen condiciones. | Express, Prisma, RabbitMQ |
| **Reports** | `3004` | Servicio de reportes (Placeholder). | Express |
| **Shared** | N/A | Librería compartida (Cliente Prisma + Wrapper RabbitMQ). | Prisma, amqplib |

## 🚀 Cómo correr el proyecto

### Prerrequisitos
- Node.js (v18+)
- Docker & Docker Compose (para RabbitMQ)

### 1. Levantar Infraestructura
En la raíz del monorepo (`Manufactura_microservicios/`), inicia RabbitMQ:
```bash
docker-compose up -d
```
Verifica que la consola de administración esté accesible en [http://localhost:15672](http://localhost:15672) (user: `guest`, pass: `guest`).

### 2. Instalar Dependencias
Desde la raíz del monorepo:
```bash
npm install
```
Esto instalará las dependencias de todos los workspaces (`server/*` y `client`).

### 3. Ejecutar Servicios
Puedes levantar todo el ecosistema (Backend + Frontend) con un solo comando desde la raíz:
```bash
npm run start-all
```
Esto usará `concurrently` para iniciar todos los servicios.

### Ejecución Individual (Opcional)
Si deseas correr un servicio específico, navega a su carpeta y ejecuta `node index.js`.
Ejemplo:
```bash
cd server/defects
node index.js
```

## 📂 Estructura de Base de Datos
El proyecto usa **SQLite** gestionado por **Prisma** en el módulo `shared`.
- Archivo DB: `server/shared/prisma/dev.db`
- Esquema: `server/shared/prisma/schema.prisma`

Si modificas el esquema, regenera el cliente:
```bash
cd server/shared
npx prisma generate
npx prisma db push
```

## 📡 Eventos
- **Exchange:** `quality_events` (Topic)
- **Evento:** `defect.created`
- **Payload:** `{ defectoId, severidad, costoTotal }`

