const express = require('express');
const { createProxyMiddleware } = require('http-proxy-middleware');
const cors = require('cors');

const app = express();
const PORT = 3000;

app.use(cors());

// Gateway Routes
app.use('/api/defectos', createProxyMiddleware({ 
    target: 'http://localhost:3001', 
    changeOrigin: true,
    pathRewrite: {
        '^/api/defectos': '' // Remove /api/defectos prefix when forwarding
    }
}));

app.use('/api/alertas', createProxyMiddleware({ 
    target: 'http://localhost:3003', 
    changeOrigin: true,
    pathRewrite: {
        '^/api/alertas': ''
    }
}));

app.use('/api/divisas', createProxyMiddleware({ 
    target: 'http://localhost:3002', 
    changeOrigin: true,
    pathRewrite: {
        '^/api/divisas': ''
    }
}));

app.listen(PORT, () => {
    console.log(`Gateway running on port ${PORT}`);
});

