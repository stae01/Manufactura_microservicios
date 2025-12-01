const authService = require('../services/authService'); 

const login = async (req, res) => {
    const { username, password } = req.body;

    try {
        const user = await authService.verifyCredentials(username, password);

        const token = authService.generateJWT(user);

        res.json({ token });  // Devolver el JWT al cliente
    } catch (err) {
        res.status(401).json({ message: err.message });
    }
};

const registro = async (req, res) => {
    const { username, password, role } = req.body;

    try {
        const newUser = await authService.registerUser(username, password, role);

        res.status(201).json({ message: 'Usuario registrado con éxito', user: newUser });
    } catch (err) {
        res.status(400).json({ message: err.message });
    }
};

module.exports = { login, registro };
