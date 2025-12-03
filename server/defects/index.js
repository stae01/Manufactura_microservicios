// server/defects/index.js
const express = require('express');
const soap = require('soap');
const cors = require('cors');
const { prisma, rabbitMQService } = require('../shared');

const app = express();
const PORT = 3001;

app.use(cors());
app.use(express.json());

// Initialize RabbitMQ connection
rabbitMQService.connect();

// Helper function to call SOAP service (currency)
const getCurrencyRate = () => {
  return new Promise((resolve, reject) => {
    const url = 'http://localhost:3002/wsdl?wsdl';
    soap.createClient(url, function (err, client) {
      if (err) return reject(err);

      client.getRate({}, function (err, result) {
        if (err) return reject(err);
        resolve(result.rate);
      });
    });
  });
};

app.post('/', async (req, res) => {
  try {
    const {
      tipo,
      severidad,
      descripcion,
      lote,
      costoMXN,
      inspectorId, 
    } = req.body;

    //Validaciones básicas
    if (!tipo || !severidad || !descripcion || !lote) {
      return res.status(400).json({
        message: 'Campos requeridos: tipo, severidad, descripcion, lote',
      });
    }

    const numericCosto = Number(costoMXN);
    if (!costoMXN || Number.isNaN(numericCosto) || numericCosto <= 0) {
      return res.status(400).json({
        message: 'costoMXN debe ser un número mayor a 0',
      });
    }

    //Obtener tipo de cambio vía SOAP
    let rate = 20.5; // fallback por si falla el servicio
    try {
      const rawRate = await getCurrencyRate();
      rate = parseFloat(rawRate);
      if (Number.isNaN(rate) || rate <= 0) {
        rate = 20.5;
      }
      console.log('Currency rate fetched via SOAP:', rate);
    } catch (err) {
      console.error('Error fetching currency rate via SOAP:', err.message);
      // nos quedamos con el fallback
    }

    const costoUSD = numericCosto / rate;

    //Guardar en la base de datos
    const defecto = await prisma.defecto.create({
      data: {
        tipo,
        severidad,
        descripcion,
        lote,
        costoMXN: numericCosto,
        costoUSD,
        // inspeccionId: null // si luego quieres ligar a Inspeccion, aquí lo tocaremos
      },
    });

    //Publicar evento en RabbitMQ
    const eventPayload = {
    defectoId: defecto.id,
    severidad: defecto.severidad,

    // compat con el microservicio de alerts actual
    costoTotal: defecto.costoMXN,

    // extras más bonitos
    costoTotalMXN: defecto.costoMXN,
    costoTotalUSD: defecto.costoUSD,
    lote: defecto.lote,
    inspectorId: inspectorId ?? null,
    };

    await rabbitMQService.publish('quality_events', 'defect.created', eventPayload);


    res.status(201).json(defecto);
  } catch (error) {
    console.error('Error en POST /defectos:', error);
    res.status(500).json({ error: 'Error al crear el defecto' });
  }
});

// Listar defectos (para debug / ver qué se guardó)
app.get('/', async (req, res) => {
  try {
    const defectos = await prisma.defecto.findMany({
      orderBy: { createdAt: 'desc' },
    });
    res.json(defectos);
  } catch (err) {
    console.error('Error en GET /defectos:', err);
    res.status(500).json({ error: 'Error al obtener los defectos' });
  }
});

app.listen(PORT, () => {
  console.log(`Defects Service running on port ${PORT}`);
});
