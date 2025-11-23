# Manufactura QA - Client

Frontend para el Sistema de Control de Calidad de Manufactura. Desarrollado con **Vue 3** y **TailwindCSS**.

## 🌟 Características
- **Reporte de Defectos:** Formulario para registrar nuevos defectos. Se comunica con el API Gateway.
- **Monitoreo de Alertas:** Lista en tiempo real (Polling) de alertas críticas generadas por el sistema.

## 🛠 Setup

### Prerrequisitos
- Node.js (v18+)
- El backend debe estar corriendo (Gateway en puerto `3000`).

### Instalación
Desde la raíz del proyecto (`client/`):
```bash
npm install
```

### Ejecución (Desarrollo)
```bash
npm run dev
```
La aplicación estará disponible en [http://localhost:5173](http://localhost:5173).

## ⚙️ Configuración
El frontend está configurado para usar un proxy en desarrollo (`vite.config.js`) que redirige las peticiones `/api` al Gateway:

```javascript
server: {
  proxy: {
    '/api': {
      target: 'http://localhost:3000', // Gateway URL
      changeOrigin: true,
    }
  }
}
```

## 📦 Scripts
- `npm run dev`: Inicia servidor de desarrollo.
- `npm run build`: Construye la aplicación para producción.
- `npm run preview`: Previsualiza el build de producción.
