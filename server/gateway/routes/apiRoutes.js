const express = require('express');
const { createProxyMiddleware, fixRequestBody } = require('http-proxy-middleware');
const authenticateJWT = require('../middlewares/authenticateJWT'); // Validar JWT
const authorizeRole = require('../middlewares/authorizeRole'); // Validar Rol

const router = express.Router();

// Proxy para los servicios
// Nota: Como este router ya está montado en '/api', las rutas aquí son relativas a eso.

//  Defectos
router.use(
  '/defectos',
  authenticateJWT,
  createProxyMiddleware({
    target: 'http://localhost:3001',
    changeOrigin: true,
    // El pathRewrite debe coincidir con lo que llega al proxy.
    // En un Router montado en /api, la URL original es /api/defectos...
    pathRewrite: {
      '^/api/defectos': '',
    },
    // Usar la solución oficial de http-proxy-middleware para body-parser
    onProxyReq: fixRequestBody,
  })
);

//  Divisas (currency SOAP)
router.use(
  '/divisas',
  authenticateJWT,
  createProxyMiddleware({
    target: 'http://localhost:3002',
    changeOrigin: true,
    pathRewrite: {
      '^/api/divisas': '',
    },
  })
);

//  Alertas (solo admin)
router.use(
  '/alertas',
  authenticateJWT,
  authorizeRole('admin'),
  createProxyMiddleware({
    target: 'http://localhost:3003',
    changeOrigin: true,
    pathRewrite: {
      '^/api/alertas': '',
    },
  })
);

//  Reportes (solo admin)
router.use(
  '/reportes',
  authenticateJWT,
  authorizeRole('admin'),
  createProxyMiddleware({
    target: 'http://localhost:3004',
    changeOrigin: true,
    pathRewrite: {
      '^/api/reportes': '',
    },
  })
);

module.exports = router;
