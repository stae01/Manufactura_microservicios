const mongoose = require('mongoose');
const bcrypt = require('bcryptjs');

const userSchema = new mongoose.Schema({
    username: {
        type: String,
        required: true,
        unique: true,
    },
    passwordHash: {
        type: String,
        required: true,
    },
    role: {
        type: String,
        enum: ['user', 'admin'],
        default: 'user'
    }
});

userSchema.methods.comparePassword = async function (password) {
    return await bcrypt.compare(password, this.passwordHash);
};

// Crear el modelo de usuario
const User = mongoose.model('User', userSchema);

module.exports = User;
