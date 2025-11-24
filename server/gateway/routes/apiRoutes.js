const express = require('express');
const { createProxyMiddleware } = require('http-proxy-middleware');
const authenticateJWT = require('../middlewares/authenticateJWT'); // Validar JWT

const router = express.Router();

// Proxy para los servicios

router.use('/api/defectos', authenticateJWT, createProxyMiddleware({ 
    target: 'http://localhost:3001', 
    changeOrigin: true,
    pathRewrite: {
        '^/api/defectos': '' 
    }
}));

router.use('/api/divisas', authenticateJWT, createProxyMiddleware({ 
    target: 'http://localhost:3002', 
    changeOrigin: true,
    pathRewrite: {
        '^/api/divisas': ''
    }
}));

router.use('/api/alertas', authenticateJWT, createProxyMiddleware({ 
    target: 'http://localhost:3003', 
    changeOrigin: true,
    pathRewrite: {
        '^/api/alertas': ''
    }
}));


module.exports = router;
