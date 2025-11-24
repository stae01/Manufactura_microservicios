const axios = require('axios');  

const BASE_URL = process.env.MICROSERVICE_DEFECTOS_URL || 'http://microservicio-defectos';

const saveDefect = async (defectData) => {
    try {
        // Realizar la solicitud POST al microservicio de defectos
        const response = await axios.post(`${BASE_URL}/api/defectos`, defectData);

        return response.data;
    } catch (error) {
        console.error('Error al guardar el defecto:', error.response?.data || error.message);
        throw new Error('No se pudo guardar el defecto');
    }
};

module.exports = { saveDefect };
