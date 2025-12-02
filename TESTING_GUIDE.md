# 🧪 Guía de Pruebas - Sistema de Control de Calidad

Esta guía detalla los pasos para verificar el funcionamiento completo del sistema, cubriendo los roles de usuario, la generación de alertas en tiempo real y la arquitectura subyacente (SOAP/RabbitMQ).

## 📋 Prerrequisitos

1. **Infraestructura Base:** Asegúrate de que Docker esté corriendo (RabbitMQ + MongoDB).
   ```bash
   docker-compose up -d
   ```
2. **Iniciar Servicios:**
   ```bash
   npm run start-all
   ```
3. **Acceso:** Abre tu navegador en [http://localhost:5173](http://localhost:5173).

---

## 🎭 Escenario 1: Flujo de Administrador (Admin)
*Objetivo: Verificar acceso total, reporte de defectos y recepción de alertas críticas.*

1. **Registro de Admin:**
   - En la pantalla de inicio, selecciona "Crear nueva cuenta".
   - **Usuario:** `admin_demo`
   - **Contraseña:** `123456`
   - **Rol:** Selecciona **Administrador**.
   - Click en "Crear Cuenta" e inicia sesión con las credenciales.

2. **Verificación de UI:**
   - Observa que en la barra superior aparece tu usuario y el rol `admin`.
   - A la derecha, debes ver el panel **"Alertas de Calidad"**.

3. **Generar Alerta Crítica:**
   - En el formulario "Reportar Defecto":
     - **Tipo:** `Fisura en motor`
     - **Severidad:** Selecciona **CRÍTICA** (Esto disparará la alerta).
     - **Descripción:** `Prueba de alerta en tiempo real`.
   - Click en "Registrar Defecto".

4. **Resultado Esperado:**
   - El formulario se limpia y muestra éxito.
   - **¡Importante!** En el panel derecho de Alertas, debería aparecer automáticamente (en 5s o menos) una nueva tarjeta roja indicando la alerta crítica.

---

## 👤 Escenario 2: Flujo de Operador (Usuario)
*Objetivo: Verificar restricciones de seguridad y operación normal.*

1. **Cambio de Usuario:**
   - Click en el botón "Salir" (arriba a la derecha).

2. **Registro de Operador:**
   - Selecciona "Crear nueva cuenta".
   - **Usuario:** `operador_demo`
   - **Contraseña:** `123456`
   - **Rol:** Selecciona **Usuario (Operador)**.
   - Click en "Crear Cuenta" e inicia sesión.

3. **Verificación de Restricciones:**
   - Observa que el rol dice `user`.
   - **Verificación:** El panel derecho NO muestra la lista de alertas. En su lugar, muestra un mensaje de **"Acceso Restringido"** con un candado. Esto confirma que la seguridad por roles funciona.

4. **Reporte Estándar:**
   - Reporta un defecto con Severidad **BAJA**.
   - El defecto se registra correctamente, pero como operador no ves si generó alerta o no.

---

## ⚙️ Verificación Técnica (Behind the Scenes)
*Para mostrar que la arquitectura funciona.*

Mientras realizas las pruebas anteriores, se muestra la terminal donde corre `npm run start-all`:

1. **Invocación Remota SOAP:**
   - Cuando registras un defecto, busca en la terminal del servicio **Defects** (etiqueta `[1]`):
     ```text
     Currency rate fetched via SOAP: 20.5
     ```
   - Esto confirma que el microservicio consumió el Web Service SOAP de Divisas.

2. **Colas de Mensajería (RabbitMQ):**
   - Cuando registraste el defecto CRÍTICO, busca en la terminal del servicio **Alerts** (etiqueta `[3]`):
     ```text
     Received event: { defectoId: '...', severidad: 'CRITICA', ... }
     Generating Alert...
     ```
   - Esto confirma que el mensaje viajó asíncronamente por RabbitMQ.

3. **Seguridad JWT:**
   - En el navegador, abre Herramientas de Desarrollador (F12) -> Aplicación -> Local Storage.
   - Verifica que existe una key `token`. Este es el **JWT** que autentica todas las peticiones.

