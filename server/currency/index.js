const express = require('express');
const cors = require('cors');
const soap = require('soap');
const fs = require('fs');
const path = require('path');

const app = express();
const PORT = 3002;

app.use(cors());
app.use(express.json());

// SOAP Service Definition
const service = {
    CurrencyService: {
        CurrencyPort: {
            getRate: function(args) {
                // Return a hardcoded rate as per original logic
                return { rate: 20.50 };
            }
        }
    }
};

// Read WSDL
const wsdlPath = path.join(__dirname, 'service.wsdl');
const xml = fs.readFileSync(wsdlPath, 'utf8');

// Legacy REST endpoint (optional, kept for reference)
app.get('/rate', (req, res) => {
    res.json({ rate: 20.50 });
});

app.listen(PORT, () => {
    console.log(`Currency Service running on port ${PORT}`);
    
    // Initialize SOAP Server
    soap.listen(app, '/wsdl', service, xml, function(){
      console.log('SOAP server initialized at http://localhost:' + PORT + '/wsdl?wsdl');
    });
});
