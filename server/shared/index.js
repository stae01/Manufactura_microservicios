const { PrismaClient } = require('@prisma/client');
const rabbitMQService = require('./lib/rabbitmq');
const path = require('path');

const dbPath = path.join(__dirname, 'prisma/dev.db');
console.log('Shared Module: Using DB at', dbPath);

const prisma = new PrismaClient({
  datasources: {
    db: {
      url: 'file:' + dbPath,
    },
  },
});

module.exports = {
  prisma,
  rabbitMQService
};
