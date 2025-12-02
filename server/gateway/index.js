require('dotenv').config();

const express = require('express');
const { createProxyMiddleware } = require('http-proxy-middleware');
const cors = require('cors');
const connectDB = require('./config/db');
const authRoutes = require('./routes/authRoutes');
const apiRoutes = require('./routes/apiRoutes');

connectDB();

const app = express();
const PORT = process.env.PORT || 3000;

app.use(cors());

// MOVE API ROUTES BEFORE BODY PARSER
// Proxy routes must handle their own body parsing or streaming.
// If we use express.json() globally before the proxy, it consumes the stream.
// authRoutes NEEDS body parser, so we can mount it specifically for auth, 
// OR apply express.json() only to routes that are NOT proxied.

// Option A: Apply express.json() ONLY to /api/auth
// app.use('/api/auth', express.json(), authRoutes);

// Option B (Cleaner for this structure): Move global parser below proxy routes? 
// No, because authRoutes is also under /api.

// Solution: Use a router for Auth that has the middleware built-in, 
// and keep API routes raw for the proxy to handle.

// 1. Auth Routes (Need JSON parsing)
app.use('/api/auth', express.json(), authRoutes);

// 2. API Routes (Proxy - Do NOT parse JSON here globally)
app.use('/api', apiRoutes);

// If you have other non-proxy routes that need JSON:
// app.use(express.json()); // Global fallback for subsequent routes if any

app.listen(PORT, () => {
    console.log(`Gateway running on port ${PORT}`);
});
