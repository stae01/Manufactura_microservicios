const amqp = require('amqplib');

class RabbitMQService {
  constructor() {
    this.connection = null;
    this.channel = null;
    this.connected = false;
    this.url = 'amqp://guest:guest@localhost:5672';
  }

  async connect(retries = 5, interval = 2000) {
    while (retries > 0) {
      try {
        console.log(`Attempting to connect to RabbitMQ... (${retries} retries left)`);
        this.connection = await amqp.connect(this.url);
        this.channel = await this.connection.createChannel();
        this.connected = true;
        console.log('Connected to RabbitMQ');
        
        this.connection.on('close', () => {
            console.error('RabbitMQ connection closed');
            this.connected = false;
            // Optionally reconnect here
        });
        
        return;
      } catch (err) {
        console.error('RabbitMQ connection error:', err.message);
        retries--;
        await new Promise(res => setTimeout(res, interval));
      }
    }
    throw new Error('Failed to connect to RabbitMQ after multiple attempts');
  }

  async publish(exchange, routingKey, data) {
    if (!this.connected) {
      throw new Error('RabbitMQ is not connected');
    }
    
    try {
        await this.channel.assertExchange(exchange, 'topic', { durable: true });
        const content = Buffer.from(JSON.stringify(data));
        this.channel.publish(exchange, routingKey, content);
        console.log(`Published message to ${exchange} -> ${routingKey}`);
    } catch (error) {
        console.error('Error publishing message:', error);
    }
  }

  async subscribe(queue, routingKey, callback) {
    if (!this.connected) {
      await this.connect();
    }

    try {
        const exchange = 'quality_events'; // Hardcoded as per prompt context implies shared exchange
        
        await this.channel.assertExchange(exchange, 'topic', { durable: true });
        await this.channel.assertQueue(queue, { durable: true });
        
        // Bind queue to exchange with routing key
        await this.channel.bindQueue(queue, exchange, routingKey);
        
        console.log(`Subscribed to ${queue} bound to ${exchange} with key ${routingKey}`);

        this.channel.consume(queue, (msg) => {
            if (msg !== null) {
                const content = JSON.parse(msg.content.toString());
                callback(content);
                this.channel.ack(msg);
            }
        });
    } catch (error) {
        console.error('Error subscribing to queue:', error);
    }
  }
}

module.exports = new RabbitMQService();

