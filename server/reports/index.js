const express = require('express');
const cors = require('cors');

const app = express();
const PORT = 3004;

app.use(cors());
app.use(express.json());

app.get('/', (req, res) => {
    res.json({ message: "Reports Service Operational" });
});

app.listen(PORT, () => {
    console.log(`Reports Service running on port ${PORT}`);
});

