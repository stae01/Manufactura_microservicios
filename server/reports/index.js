// server/reports/index.js
const express = require('express');
const cors = require('cors');
const { prisma } = require('../shared'); 
const app = express();
const PORT = 3004;

app.use(cors());
app.use(express.json());

// Health / debug
app.get('/', (req, res) => {
  res.json({ message: 'Reports Service Operational' });
});

/**
 * GET /resumen-defectos
 *
 * Devuelve resumen de:
 *  - total de defectos por tipo
 *  - suma de costoMXN y costoUSD por tipo
 *
 * Ejemplo de respuesta:
 * [
 *   {
 *     "tipo": "Daño superficial",
 *     "cantidad": 10,
 *     "totalMXN": 2500,
 *     "totalUSD": 121.95
 *   },
 *   ...
 * ]
 */
app.get('/resumen-defectos', async (req, res) => {
  try {
    const grouped = await prisma.defecto.groupBy({
      by: ['tipo'],
      _count: { _all: true },
      _sum: { costoMXN: true, costoUSD: true },
    });

    const response = grouped.map((row) => ({
      tipo: row.tipo,
      cantidad: row._count._all,
      totalMXN: row._sum.costoMXN ?? 0,
      totalUSD: row._sum.costoUSD ?? 0,
    }));

    res.json(response);
  } catch (err) {
    console.error('Error en /resumen-defectos:', err);
    res.status(500).json({ message: 'Error al generar el resumen de defectos' });
  }
});

/**
 * GET /detalle-defectos
 *
 * Detalle de defectos, con filtros y paginación.
 *
 * Query params soportados:
 *  - tipo=Daño superficial
 *  - severidad=CRITICA|ALTA|MEDIA|BAJA
 *  - page=1 (por defecto)
 *  - pageSize=20 (por defecto)
 *
 * Respuesta:
 * {
 *   "page": 1,
 *   "pageSize": 20,
 *   "total": 37,
 *   "items": [ { ...defecto }, ... ]
 * }
 */
app.get('/detalle-defectos', async (req, res) => {
  try {
    const {
      tipo,
      severidad,
      page = '1',
      pageSize = '20',
    } = req.query;

    const pageNum = Math.max(parseInt(page, 10) || 1, 1);
    const sizeNum = Math.min(Math.max(parseInt(pageSize, 10) || 20, 1), 100);

    const where = {};

    if (tipo) {
      where.tipo = tipo;
    }

    if (severidad) {
      where.severidad = severidad;
    }

    const [total, items] = await Promise.all([
      prisma.defecto.count({ where }),
      prisma.defecto.findMany({
        where,
        orderBy: { createdAt: 'desc' },
        skip: (pageNum - 1) * sizeNum,
        take: sizeNum,
      }),
    ]);

    res.json({
      page: pageNum,
      pageSize: sizeNum,
      total,
      items,
    });
  } catch (err) {
    console.error('Error en /detalle-defectos:', err);
    res.status(500).json({ message: 'Error al obtener el detalle de defectos' });
  }
});

/**
 * GET /resumen-severidad
 */
app.get('/resumen-severidad', async (req, res) => {
  try {
    const grouped = await prisma.defecto.groupBy({
      by: ['severidad'],
      _count: { _all: true },
      _sum: { costoMXN: true, costoUSD: true },
    });

    const response = grouped.map((row) => ({
      severidad: row.severidad,
      cantidad: row._count._all,
      totalMXN: row._sum.costoMXN ?? 0,
      totalUSD: row._sum.costoUSD ?? 0,
    }));

    res.json(response);
  } catch (err) {
    console.error('Error en /resumen-severidad:', err);
    res.status(500).json({ message: 'Error al generar el resumen por severidad' });
  }
});

app.listen(PORT, () => {
  console.log(`Reports Service running on port ${PORT}`);
});
