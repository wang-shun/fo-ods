// Import the Firebase SDK for Google Cloud Functions.
const functions = require('firebase-functions');

const express = require('express');
const bulkAmend = express();
const bodyParser = require('body-parser');
bulkAmend.use(bodyParser.urlencoded({
    extended: true
}));
bulkAmend.use(bodyParser.json());

let irBulkAmendClient = null;
let eqBulkAmendClient = null;
/*  IMPL */
const grpc = require('grpc');
/** BALCM GRPC */
let PROTO_PATH = __dirname + '/proto/com/maplequad/fo/ods/tradecore/proto/service/grpc/tradecore-balcm.proto';
let tradecore = grpc.load(PROTO_PATH).com.maplequad.fo.ods.tradecore.proto.service.grpc;
//let irServer = '130.211.48.239:3001'; //SIT
//let eqServer = '35.190.223.18:3001'; //SIT
let irServer = '35.187.18.217:3001'; //UAT
let eqServer = '35.195.119.231:3001'; //UAT
//let irServer = functions.config().ir_balcm.url;
//let eqServer = functions.config().eq_balcm.url;
console.log('irServer>>'+irServer);
irBulkAmendClient = new tradecore.TradeCoreBulkAmend(irServer, grpc.credentials.createInsecure());
eqBulkAmendClient = new tradecore.TradeCoreBulkAmend(eqServer, grpc.credentials.createInsecure());

bulkAmend.post('/initiateBulkAmend', (request, response) => {
    console.log("request.body>"+request.body);
    /**
     * Run the inner function
     * @param {string} error called 
     * @param {string} resp
     */
    function createCallback(error, resp) {
        if (error) {
            // callback(error);
            console.log(error);
            console.log(resp);
            return;
        }
        // debug here, if 
        console.log('response-->'+resp);
        bulkAmendId = resp.bulkAmendId;
        response.status(200).send(`<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="utf-8">
    <title>TradeCore Bulk Amend</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <!-- Loading Bootstrap -->
    <link href="flat-ui/css/vendor/bootstrap/css/bootstrap.min.css" rel="stylesheet">

    <!-- Loading Flat UI -->
    <link href="flat-ui/css/flat-ui.min.css" rel="stylesheet">

    <!-- HTML5 shim, for IE6-8 support of HTML5 elements. All other JS at the end of file. -->
    <!--[if lt IE 9]>
      <script src="js/vendor/html5shiv.js"></script>
      <script src="js/vendor/respond.min.js"></script>
    <![endif]-->
    <style>
        html,
        body {
            height: 100%;
            font-size: 1.0em;
            color: white;
            background-color: #302e2e;
        }
       td { 
        padding-left: 10px;
        }
    </style>
</head>

<body>
<div style = "margin-top: 20px; margin-left: 20px">
            <table style="border-spacing: 10px;">
            <tr><td> Bulk Amend ID </td><td>${resp.bulkAmendId} successfully created.</td></tr>
             </table>
            </div>
            <script>
            </script>
            </body>
        </html>`);
    }
        
    const userName = request.body.hdnUserName;
    const bulkAmendType = request.body.hdnBulkAmendType;
    const asOfDate = new Date(request.body.txtAsOfDate);
    const asOfDateTs = Math.floor(asOfDate.getTime() + asOfDate.getTimezoneOffset() * 60000)+'';
    const tslen = asOfDateTs.length;    
    const secs = parseInt(asOfDateTs.substr(0, tslen - 3));
    const nanos = parseInt(asOfDateTs.substr(tslen - 3)) * 1000000;
    if(bulkAmendType === 'IRSTRCP'){
        let compression = {
            'bookId': request.body.txtTradeBook,
            'instrumentId': request.body.txtInstrumentId
        };

        let bulkAmendDetails = {
            'compression': compression
        };

        let insertBulkAmend = {
            'assetClass': 'ir-swap',
            'createdBy': request.body.hdnUserName,
            'description': 'IRS Trade Compression',
            'bulkAmendType': 'IRD_TRD_CMPR',
            'asOf': {
                seconds: secs,
                nanos: nanos,
            },
            'type': bulkAmendDetails
        };

        console.log('Sending trade compression');
        console.log(insertBulkAmend);
    
        let call2 = irBulkAmendClient.initiateBulkAmend(insertBulkAmend, createCallback);

    } else if (bulkAmendType === 'EQTSPLT'){
        let split = {
            'ratio': parseFloat(request.body.txtSplitRatio.replace(/,/g, '')),
            'instrumentId': request.body.txtInstrumentId1
        };

        let bulkAmendDetails = {
            'split': split
        };

        let insertBulkAmend = {
            'assetClass': 'cash-eq',
            'createdBy': request.body.hdnUserName,
            'description': 'Equity Stock Split',
            'bulkAmendType': 'EQT_STOCK_SPLIT',
            'asOf': {
                seconds: secs,
                nanos: nanos,
            },
            'type': bulkAmendDetails
        };

        console.log('Sending trade compression');
        console.log(insertBulkAmend);
    
        let call2 = eqBulkAmendClient.initiateBulkAmend(insertBulkAmend, createCallback);
    }
});

bulkAmend.post('/actionBulkAmend', (request, response) => {
    console.log(request.body);
    /**
     * Run the inner function
     * @param {string} error called 
     * @param {string} resp
     */
    function actionCallback(error, resp) {
        if (error) {
            // callback(error);
            console.log(error);
            console.log(resp);
            return;
        }
        // debug here, if 
        console.log('response-->');
        console.log(resp);
        
        response.status(200).redirect('/bulk-amend-grid.html');
/*
        response.status(200).send(`<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="utf-8">
    <title>TradeCore Bulk Amend</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <!-- Loading Bootstrap -->
    <link href="flat-ui/css/vendor/bootstrap/css/bootstrap.min.css" rel="stylesheet">

    <!-- Loading Flat UI -->
    <link href="flat-ui/css/flat-ui.min.css" rel="stylesheet">

    <!-- HTML5 shim, for IE6-8 support of HTML5 elements. All other JS at the end of file. -->
    <!--[if lt IE 9]>
      <script src="js/vendor/html5shiv.js"></script>
      <script src="js/vendor/respond.min.js"></script>
    <![endif]-->
    <style>
        html,
        body {
            height: 100%;
            font-size: 1.0em;
            color: white;
            background-color: #302e2e;
        }
       td { 
        padding-left: 10px;
        }
    </style>
</head>

<body>
<div style = "margin-top: 20px; margin-left: 20px">
            <table style="border-spacing: 10px;">
            <tr><td> Bulk Amend ID </td><td>${resp.bulkAmendId} successfully processed.</td></tr>
             </table>
            </div>
            <script>
            </script>
            </body>
        </html>`);
*/
    }
        
    const bulkAmendType = request.body.hdnBulkAmendType;
    const bulkAmendAction = request.body.hdnBulkAmendAction;
    
    let actionBulkAmend = {
        'bulkAmendId': request.body.hdnBulkAmendId
    }

    console.log('actioning trade compression');
    console.log(actionBulkAmend);

    if(bulkAmendType === 'IRSTRCP'){
        if(bulkAmendAction === 'EXEC'){
        let call2 = irBulkAmendClient.executeBulkAmend(actionBulkAmend, actionCallback);
        } else if(bulkAmendAction === 'CANC'){
        let call2 = irBulkAmendClient.cancelBulkAmend(actionBulkAmend, actionCallback);
        }
    } else if (bulkAmendType === 'EQTSPLT'){
        if(bulkAmendAction === 'EXEC'){
            let call2 = eqBulkAmendClient.executeBulkAmend(actionBulkAmend, actionCallback);
        } else if(bulkAmendAction === 'CANC'){
            let call2 = eqBulkAmendClient.cancelBulkAmend(actionBulkAmend, actionCallback);
        }    
    }
});

exports.initiateBulkAmend = functions.https.onRequest(bulkAmend);
exports.actionBulkAmend = functions.https.onRequest(bulkAmend);
