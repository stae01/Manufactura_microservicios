const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');
const User = require('../models/User');
const { JWT_SECRET } = require('../config/env');

const verifyCredentials = async (username, password) => {
    const user = await User.findOne({ username });
    if (!user) {
        throw new Error('Usuario no encontrado');
    }

    const isMatch = await bcrypt.compare(password, user.passwordHash);
    if (!isMatch) {
        throw new Error('Contraseña incorrecta');
    }

    return user;  
};

const generateJWT = (user) => {
    return jwt.sign({ userId: user._id, username: user.username }, JWT_SECRET, { expiresIn: '1h' });
};

const registerUser = async (username, password) => {
    const userExists = await User.findOne({ username });
    if (userExists) {
        throw new Error('El usuario ya existe');
    }

    const passwordHash = await bcrypt.hash(password, 10);

    const newUser = new User({ username, passwordHash });
    await newUser.save();
    return newUser;  
};

module.exports = { verifyCredentials, generateJWT, registerUser };
