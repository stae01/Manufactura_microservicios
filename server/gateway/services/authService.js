const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');
const User = require('../models/User');
const { JWT_SECRET } = require('../config/env');

const verifyCredentials = async (username, password) => {
  // Buscar usuario por username
  const user = await User.findOne({ username });
  if (!user) {
    // No decimos si fue usuario o contraseña por seguridad
    throw new Error('Usuario o contraseña inválidos');
  }

  // Puedes usar el método del schema:
  const isMatch = await user.comparePassword(password);
  // O bien: const isMatch = await bcrypt.compare(password, user.passwordHash);

  if (!isMatch) {
    throw new Error('Usuario o contraseña inválidos');
  }

  // devolvemos un objeto limpio, no el doc completo
  return {
    id: user._id.toString(),
    username: user.username,
    role: user.role,
  };
};

const generateJWT = (user) => {
  // user aquí ya es el objeto limpio que regresamos arriba
  return jwt.sign(
    {
      userId: user.id,
      username: user.username,
      role: user.role,
    },
    JWT_SECRET,
    { expiresIn: '1m' }
  );
};

const registerUser = async (username, password, role) => {
  const userExists = await User.findOne({ username });
  if (userExists) {
    throw new Error('El usuario ya existe');
  }

  const passwordHash = await bcrypt.hash(password, 10);

  const newUser = new User({
    username,
    passwordHash,
    role,
  });

  await newUser.save();

  // igual, regresamos versión limpia
  return {
    id: newUser._id.toString(),
    username: newUser.username,
    role: newUser.role,
  };
};

module.exports = { verifyCredentials, generateJWT, registerUser };
