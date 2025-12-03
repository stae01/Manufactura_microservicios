// server/gateway/middlewares/authorizeRole.js
const authorizeRole = (...allowedRoles) => {
  return (req, res, next) => {
    if (!req.user) {
      return res.status(401).json({ message: 'No autenticado' });
    }

    if (!allowedRoles.includes(req.user.role)) {
      return res.status(403).json({ message: 'Acceso denegado: Rol insuficiente' });
    }

    next();
  };
};

module.exports = authorizeRole;
