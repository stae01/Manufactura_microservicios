const express = require('express');
const cors = require('cors');
const app = express();
const PORT = 3002;

app.use(cors());
app.use(express.json());

app.get('/rate', (req, res) => {
    res.json({ rate: 20.50 });
});

app.listen(PORT, () => {
    console.log(`Currency Service running on port ${PORT}`);
});

