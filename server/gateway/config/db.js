const mongoose = require('mongoose');

const connectDB = async () => {
    try {
        // Conectar a la base de datos de MongoDB
        await mongoose.connect(process.env.MONGO_URI, {
            useNewUrlParser: true,
            useUnifiedTopology: true,
        });
        console.log('Conectado a MongoDB');
    } catch (err) {
        console.error('Error de conexión a la base de datos:', err);
        process.exit(1);
    }
};

module.exports = connectDB;
