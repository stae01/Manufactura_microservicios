const express = require('express');
const cors = require('cors');
const { prisma, rabbitMQService } = require('../shared');

const app = express();
const PORT = 3003;

app.use(cors());
app.use(express.json());

// Subscribe to events
const startConsumer = async () => {
    await rabbitMQService.connect();
    await rabbitMQService.subscribe('alerts_queue', 'defect.created', async (data) => {
        console.log('Received event:', data);
        const { defectoId, severidad, costoTotal } = data;

        if (severidad === 'CRITICA' || costoTotal > 1000) {
            console.log('Generating Alert...');
            await prisma.alerta.create({
                data: {
                    mensaje: `Alerta generada para defecto #${defectoId}: Severidad ${severidad}, Costo ${costoTotal}`,
                    severidad: severidad,
                    defectoId: defectoId
                }
            });
            console.log('Procesando evento de defecto... Alerta generada');
        } else {
            console.log('Event processed, no alert needed.');
        }
    });
};

startConsumer();

app.get('/', async (req, res) => {
    const alertas = await prisma.alerta.findMany({
        include: { defecto: true }
    });
    res.json(alertas);
});

app.listen(PORT, () => {
    console.log(`Alerts Service running on port ${PORT}`);
});

