const authService = require('../services/authService');

// 🔹 Validación para REGISTRO
function validateRegister({ username, password, role }) {
  const errors = {};

  if (!username || username.trim().length < 3) {
    errors.username = "Username must be at least 3 characters";
  }

  if (!password || password.length < 6) {
    errors.password = "Password must be at least 6 characters";
  }

  if (!role) {
    errors.role = "Role is required";
  }

  return errors;
}

// 🔹 Validación para LOGIN (MUCHO MÁS SIMPLE)
function validateLogin({ username, password }) {
  const errors = {};

  if (!username || username.trim() === "") {
    errors.username = "Username is required";
  }

  if (!password || password === "") {
    errors.password = "Password is required";
  }

  return errors;
}

const login = async (req, res) => {
    const { username, password } = req.body;

    // 1) Validación básica de campos vacíos
    const errors = validateLogin({ username, password });
    if (Object.keys(errors).length > 0) {
        return res.status(400).json({ errors });
    }

    try {
        // 2) Si pasan la validación básica, ahora sí verificas credenciales
        const user = await authService.verifyCredentials(username, password);
        const token = authService.generateJWT(user);

        res.json({ token });
    } catch (err) {
        // Aquí SIEMPRE respondes genérico por seguridad
        res.status(401).json({ message: "Invalid username or password" });
    }
};

const registro = async (req, res) => {
    const { username, password, role } = req.body;

    // Validación estricta para registro
    const errors = validateRegister({ username, password, role });
    if (Object.keys(errors).length > 0) {
        return res.status(400).json({ errors });
    }

    try {
        const newUser = await authService.registerUser(username, password, role);
        res.status(201).json({ message: 'User registered successfully', user: newUser });
    } catch (err) {
        res.status(400).json({ message: err.message });
    }
};

module.exports = { login, registro };
