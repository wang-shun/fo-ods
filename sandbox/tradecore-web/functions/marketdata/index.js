// reuters/index.js
const marketdata = require('./reuters');

module.exports = {
    subscribeToFxMarketData: marketdata.subscribeToFxMarketData,
};
