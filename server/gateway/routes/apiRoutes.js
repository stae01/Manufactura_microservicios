const express = require('express');
const { createProxyMiddleware, fixRequestBody } = require('http-proxy-middleware');
const authenticateJWT = require('../middlewares/authenticateJWT'); // Validar JWT
const authorizeRole = require('../middlewares/authorizeRole'); // Validar Rol

const router = express.Router();

// Proxy para los servicios
// Nota: Como este router ya está montado en '/api', las rutas aquí son relativas a eso.

// Importante: createProxyMiddleware atrapa todas las sub-rutas.
// Si usamos router.use('/defectos', ...), Express hace match parcial.
// Pero el proxy necesita saber exactamente qué parte de la URL reescribir.

router.use('/defectos', authenticateJWT, createProxyMiddleware({ 
    target: 'http://localhost:3001', 
    changeOrigin: true,
    // El pathRewrite debe coincidir con lo que llega al proxy.
    // Al estar montado en /api/defectos, el req.url que ve el proxy podría ser '/' si Express ya hizo strip,
    // O podría ser '/api/defectos' si usamos app.use global.
    // En un Router, req.originalUrl es '/api/defectos'.
    pathRewrite: {
        '^/api/defectos': '' 
    },
    // Usar la solución oficial de http-proxy-middleware para body-parser
    onProxyReq: fixRequestBody,
}));

router.use('/divisas', authenticateJWT, createProxyMiddleware({ 
    target: 'http://localhost:3002', 
    changeOrigin: true,
    pathRewrite: {
        '^/api/divisas': ''
    }
}));

// SOLO ADMIN: Proxy para alertas
router.use('/alertas', authenticateJWT, authorizeRole('admin'), createProxyMiddleware({ 
    target: 'http://localhost:3003', 
    changeOrigin: true,
    pathRewrite: {
        '^/api/alertas': ''
    }
}));


module.exports = router;
