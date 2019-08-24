// trade/index.js
const casheqtrade = require('./casheq-trade');
const irstrade = require('./irs-trade');
const bulkamend = require('./bulk-amend');

module.exports = {
    createCashEqTrade: casheqtrade.createCashEqTrade,
    createIrsTrade: irstrade.createIrsTrade,
    initiateBulkAmend: bulkamend.initiateBulkAmend,
    actionBulkAmend: bulkamend.actionBulkAmend,
};
