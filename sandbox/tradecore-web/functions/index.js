// index.js
require('./firebase/index');
let user = require('./user/index');
let trade = require('./trade/index');
let marketdata = require('./marketdata/index');

module.exports = {
    createCashEqTrade: trade.createCashEqTrade,
    createIrsTrade: trade.createIrsTrade,
    createIrsTrade: trade.createIrsTrade,
    userAuthentication: user.userAuthentication,
    subscribeToFxMarketData: marketdata.subscribeToFxMarketData,
    initiateBulkAmend: trade.initiateBulkAmend,
    actionBulkAmend: trade.actionBulkAmend,
};
