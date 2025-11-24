const defectService = require('../services/defectService'); 

const captureDefect = async (req, res) => {
    const defectData = req.body;

    try {
        await defectService.saveDefect(defectData);
        res.status(201).json({ message: 'Defecto registrado con éxito' });
    } catch (err) {
        res.status(500).json({ message: 'Error al registrar defecto' });
    }
};

module.exports = { captureDefect };
