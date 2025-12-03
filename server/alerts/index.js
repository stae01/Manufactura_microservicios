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

  await rabbitMQService.subscribe(
    'alerts_queue',
    'defect.created',
    async (data) => {
      console.log('Received event:', data);

      const {
        defectoId,
        severidad,
        costoTotal,
        costoTotalMXN,
        costoMXN,
      } = data;

      // Resolver el costo desde el evento (varios nombres posibles)
      let costo = costoTotal ?? costoTotalMXN ?? costoMXN;

      // Si sigue indefinido lo buscamos en la BD
      if (costo == null && defectoId != null) {
        try {
          const defecto = await prisma.defecto.findUnique({
            where: { id: defectoId },
          });

          if (defecto) {
            costo = defecto.costoMXN;
          }
        } catch (err) {
          console.error('Error buscando defecto en BD desde alerts:', err);
        }
      }

      // Si aun asi no hay costo, lo dejamos en 0 para no romper nada
      if (costo == null) {
        costo = 0;
      }

      // Aseguramos numero
      const costoNumber = Number(costo) || 0;

      // Logica de disparo de alerta
      if (severidad === 'CRITICA' || costoNumber > 1000) {
        console.log('Generating Alert...');

        const mensaje = `Alerta generada para defecto #${defectoId}: ` +
          `Severidad ${severidad}, Costo ${costoNumber.toFixed(2)} MXN`;

        await prisma.alerta.create({
          data: {
            mensaje,
            severidad: severidad,
            defectoId: defectoId ?? null,
          },
        });

        console.log('Procesando evento de defecto... Alerta generada');
      } else {
        console.log('Event processed, no alert needed.');
      }
    }
  );
};

startConsumer();

// Endpoint para consultar alertas
app.get('/', async (req, res) => {
  const alertas = await prisma.alerta.findMany({
    include: { defecto: true },
    orderBy: { fecha: 'desc' },
  });
  res.json(alertas);
});

app.listen(PORT, () => {
  console.log(`Alerts Service running on port ${PORT}`);
});
