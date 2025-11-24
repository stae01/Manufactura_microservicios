const jwt = require('jsonwebtoken');
const bcrypt = require('bcryptjs');
const User = require('../models/User'); 
const { JWT_SECRET } = require('../config/env');

const login = async (req, res) => {
    const { username, password } = req.body;

    const user = await User.findOne({ username });

    if (!user) {
        return res.status(401).json({ message: 'Usuario no encontrado' });
    }

    const isMatch = await user.comparePassword(password);
    if (!isMatch) {
        return res.status(401).json({ message: 'Contraseña incorrecta' });
    }

    // Generar el token JWT
    const token = jwt.sign({ userId: user._id, username }, JWT_SECRET, { expiresIn: '1h' });

    // Devolver el token al cliente
    res.json({ token });
};

const registro = async (req, res) => {
    const { username, password } = req.body;

    const userExists = await User.findOne({ username });
    if (userExists) {
        return res.status(400).json({ message: 'El usuario ya existe' });
    }

    const passwordHash = await bcrypt.hash(password, 10);

    const newUser = new User({
        username,
        passwordHash,
    });

    await newUser.save();

    res.status(201).json({ message: 'Usuario registrado con éxito' });
};

module.exports = { login, registro };
