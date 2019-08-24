// Import Admin SDK
let admin = require('firebase-admin');

// Get a database reference to reuters market data
let fxSpotMarketDataGridRef = admin.database().ref('fxSpotMarketDataGrid/');

/**
 * Background Cloud Function to be triggered by Pub/Sub.
 *
 * @param {object} event The Cloud Functions event.
 * @param {function} callback The callback function.
 */
exports.subscribeToFxMarketData = function(event, callback) {
    const pubsubMessage = event.data;
    const ricData = pubsubMessage.data ? Buffer.from(pubsubMessage.data, 'base64').toString() : 'NoData';
    console.log(`Got it, ${ricData}!`);
    console.log(`Got it RIC, ${ricData.RIC}!`);

    fxSpotMarketDataGridRef.child(ricData.RIC).set(ricData, function(error) {
        if (error) {
            console.log('Data could not be saved.' + error);
        } else {
            console.log('Data saved successfully.');
        }
    });

    callback();
};
