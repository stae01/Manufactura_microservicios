require('dotenv').config();

const express = require('express');
const { createProxyMiddleware } = require('http-proxy-middleware');
const cors = require('cors');
const jwt = require('jsonwebtoken');
const connectDB = require('./config/db');
const authRoutes = require('./routes/authRoutes');
const apiRoutes = require('./routes/apiRoutes');

connectDB();

const app = express();
const PORT = process.env.PORT || 3000;

app.use(cors());

app.use(express.json());


// Rutas
app.use('/api/auth', authRoutes);  
app.use('/api', apiRoutes);       


// Gateway Routes
/*app.use('/api/defectos', authenticateJWT, createProxyMiddleware({ 
    target: 'http://localhost:3001', 
    changeOrigin: true,
    pathRewrite: {
        '^/api/defectos': '' // Remove /api/defectos prefix when forwarding
    }
}));

app.use('/api/alertas', authenticateJWT, createProxyMiddleware({ 
    target: 'http://localhost:3003', 
    changeOrigin: true,
    pathRewrite: {
        '^/api/alertas': ''
    }
}));

app.use('/api/divisas', authenticateJWT, createProxyMiddleware({ 
    target: 'http://localhost:3002', 
    changeOrigin: true,
    pathRewrite: {
        '^/api/divisas': ''
    }
}));*/

app.listen(PORT, () => {
    console.log(`Gateway running on port ${PORT}`);
});

