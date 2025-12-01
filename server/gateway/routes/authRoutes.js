const express = require('express');
const { login, registro } = require('../controllers/authControllers');

const router = express.Router();

// Ruta de login, se genera JWT
router.post('/login', login);

// Ruta de registro 
router.post('/registro', registro);

module.exports = router;
