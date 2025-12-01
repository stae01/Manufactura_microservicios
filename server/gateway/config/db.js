const mongoose = require('mongoose');

const connectDB = async () => {
    try {
        // Conectar a la base de datos de MongoDB
        // Las opciones useNewUrlParser y useUnifiedTopology ya son default en versiones nuevas de Mongoose y causan error si se pasan explícitamente.
        await mongoose.connect(process.env.MONGO_URI);
        console.log('Conectado a MongoDB');
    } catch (err) {
        console.error('Error de conexión a la base de datos:', err);
        process.exit(1);
    }
};

module.exports = connectDB;
