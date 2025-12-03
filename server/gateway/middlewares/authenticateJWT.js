const jwt = require('jsonwebtoken');
const { JWT_SECRET } = require('../config/env');

const authenticateJWT = (req, res, next) => {
  const authHeader = req.header('Authorization');

  if (!authHeader || !authHeader.startsWith('Bearer ')) {
    return res.status(401).json({ message: 'No autorizado. Token no proporcionado.' });
  }

  const token = authHeader.replace('Bearer ', '');

  try {
    const decoded = jwt.verify(token, JWT_SECRET);
    // decoded = { userId, username, role, iat, exp }
    req.user = decoded;
    next();
  } catch (err) {
    return res.status(401).json({ message: 'Token inválido o expirado.' });
  }
};

module.exports = authenticateJWT;
