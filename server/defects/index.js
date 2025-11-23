const express = require('express');
const axios = require('axios');
const cors = require('cors');
const { prisma, rabbitMQService } = require('../shared');

const app = express();
const PORT = 3001;

app.use(cors());
app.use(express.json());

// Initialize RabbitMQ connection
rabbitMQService.connect();

app.post('/', async (req, res) => {
    try {
        const { tipo, severidad, descripcion, inspectorId } = req.body;

        // 1. Call Currency Service
        let rate = 20.50; // Default fallback
        try {
            const response = await axios.get('http://localhost:3002/rate');
            rate = response.data.rate;
        } catch (err) {
            console.error('Error fetching currency rate:', err.message);
        }

        // Calculate costs (Mock logic: arbitrary base cost * multiplier based on severity)
        const baseCostMXN = 100;
        const multiplier = severidad === 'CRITICA' ? 10 : severidad === 'ALTA' ? 5 : 1;
        const costoMXN = baseCostMXN * multiplier;
        const costoUSD = costoMXN / rate;

        // 2. Save to DB
        const defecto = await prisma.defecto.create({
            data: {
                tipo,
                severidad,
                descripcion,
                costoMXN,
                costoUSD,
                // Assuming inspector exists or ignoring relation for simplicity if not strictly enforced by foreign keys logic in input
                // For this academic example, we might not have inspectorId or it might be optional
            }
        });

        // 3. Publish Event
        const eventPayload = {
            defectoId: defecto.id,
            severidad: defecto.severidad,
            costoTotal: defecto.costoMXN
        };
        
        await rabbitMQService.publish('quality_events', 'defect.created', eventPayload);

        res.status(201).json(defecto);
    } catch (error) {
        console.error(error);
        res.status(500).json({ error: error.message });
    }
});

app.get('/', async (req, res) => {
    const defectos = await prisma.defecto.findMany();
    res.json(defectos);
});

app.listen(PORT, () => {
    console.log(`Defects Service running on port ${PORT}`);
});

