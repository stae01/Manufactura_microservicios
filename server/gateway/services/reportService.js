const axios = require('axios');

const BASE_URL = process.env.MICROSERVICE_REPORTES_URL || 'http://microservicio-reportes';  

const generateReports = async () => {
    try {
        // Realizamos la solicitud GET al microservicio de reportes
        const response = await axios.get(`${BASE_URL}/api/reportes`);
        
        // Devolvemos los datos del reporte recibido
        return response.data;
    } catch (error) {
        console.error('Error al generar los reportes:', error.response?.data || error.message);
        throw new Error('No se pudo generar el reporte');
    }
};

module.exports = { generateReports };
