// Import the Firebase SDK for Google Cloud Functions.
const functions = require('firebase-functions');

const express = require('express');
const eqTradeCore = express();
const bodyParser = require('body-parser');
eqTradeCore.use(bodyParser.urlencoded({
    extended: true
}));
eqTradeCore.use(bodyParser.json());

const LCMConnectFlag = true;
let client = null;
let eventRef = '';
/*  IMPL 1*/
const grpc = require('grpc');
if (LCMConnectFlag) {
    /** LCM GRPC */
    let PROTO_PATH = __dirname + '/proto/com/maplequad/fo/ods/tradecore/proto/service/grpc/tradecore-lifecycle.proto';
    let tradecore = grpc.load(PROTO_PATH).com.maplequad.fo.ods.tradecore.proto.service.grpc;
    //let server = '35.195.249.183:3000'; //SIT
    let server = '35.187.89.197:3001'; // UAT
    //let server = functions.config().eq_lcm.url;
    eventRef = 'LCM-123456';
    client = new tradecore.TradeCoreLifeCycle(server, grpc.credentials.createInsecure());
} else { /*  IMPL 1 - TradeCoreStore*/
    let PROTO_PATH = __dirname + '/proto/com/maplequad/fo/ods/tradecore/proto/service/grpc/tradecore-store.proto';
    let tradecore = grpc.load(PROTO_PATH).com.maplequad.fo.ods.tradecore.proto.service.grpc;
    //let server = '104.155.34.173:3002'; //SIT
    let server = '35.195.216.51:3002' //UAT
    //let server = functions.config().xa_store.url;    
    eventRef = 'TCS-345673';
    client = new tradecore.TradeCoreStore(server, grpc.credentials.createInsecure());
}
/** IMPL 2 */
/*
//const path = require('path');
const caller = require('grpc-caller');
//const PROTO_PATH = path.resolve(__dirname, './proto/com/maplequad/fo/ods/tradecore/proto/service/grpc/tradecore-lifecycle.proto');
const PROTO_PATH = __dirname + '/proto/com/maplequad/fo/ods/tradecore/proto/service/grpc/tradecore-lifecycle.proto';

const client = caller('35.195.180.125:3000', PROTO_PATH, 'com.maplequad.fo.ods.tradecore.proto.service.grpc.TradeCoreLifeCycle');
*/

eqTradeCore.post('/createCashEqTrade', (request, response) => {
    // console.log(__dirname);
    console.log(request.body);
    // let body = JSON.stringify(request.body, null, 4);
    // let lcmReponse = '';
    let tradeId = '';
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
        console.log('response-->');
        console.log(resp);
        tradeId = resp.trade.tradeId;
        tradeEvent = resp.trade.tradeEvents[0];
        tradeLeg = tradeEvent.equityLegs[0];
        tradeParty1 = tradeEvent.tradeParties[0];
        tradeParty2 = tradeEvent.tradeParties[1];

        response.status(200).send(`<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="utf-8">
    <title>TradeCore Order Form</title>
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
            <tr><td> Trade ID </td><td>${tradeId} </td></tr>
            <tr><td> Primary Asset Class </td><td>${resp.trade.assetClass} </td></tr>
            <tr><td> Product Type </td><td>${resp.trade.productType} </td></tr>
            <tr><td> Trade Type </td><td>${resp.trade.tradeType} </td></tr>
            <tr><td> Originating System </td><td>${resp.trade.origSystem} </td></tr>
            <tr><td> OS Trade ID </td><td>${resp.trade.osTradeId} </td></tr>
            <tr><td> Created Time Stamp</td><td><span id="spanCTS"></span></td></tr>
            <tr><td> Event Type </td><td>${tradeEvent.eventType} </td></tr>
            <tr><td> Event Status </td><td>${tradeEvent.eventStatus} </td></tr>
            <tr><td> Event Reference </td><td>${tradeEvent.eventReference} </td></tr>
            <tr><td> Trader </td><td>${tradeEvent.trader} </td></tr>
            <tr><td> Trade Leg Number</td><td>${tradeLeg.legNumber} </td></tr>
            <tr><td> Trade Book </td><td>${tradeLeg.book} </td></tr>
            <tr><td> RIC Code </td><td>${tradeLeg.instrumentId} </td></tr>
            <tr><td> Currency </td><td>${tradeLeg.currency} </td></tr>
            <tr><td> Exchange Code </td><td>${tradeLeg.exchange} </td></tr>
            <tr><td> CFI Code </td><td>${tradeLeg.cfiCode} </td></tr>
            <tr><td> Quantity </td><td>${tradeLeg.quantity} </td></tr>
            <tr><td> Price </td><td>${tradeLeg.price} </td></tr>
            <tr><td> Gross Price </td><td>${tradeLeg.grossPrice} </td></tr>
            <tr><td> Buy Sell Ind </td><td>${tradeLeg.buySellInd} </td></tr>
            <tr><td> Party1 Ref </td><td>${tradeParty1.partyRef} </td></tr>
            <tr><td> Party1 Role </td><td>${tradeParty1.partyRole} </td></tr>
            <tr><td> Party2 Ref </td><td>${tradeParty2.partyRef} </td></tr>
            <tr><td> Party2 Role </td><td>${tradeParty2.partyRole} </td></tr>
            </table>
            </div>
            <script>
                let cTS = new Date(${resp.trade.createdTimeStamp.seconds * 1000}).toLocaleString();
                document.getElementById('spanCTS').innerHTML = cTS;
            </script>
            </body>
        </html>`);
    }
    const ldate = new Date();
    const ndate = Math.floor(ldate.getTime() + ldate.getTimezoneOffset() * 60000);
    const cdate = ndate + '';
    const tslen = cdate.length;
    const secs = parseInt(cdate.substr(0, tslen - 3));
    const nanos = parseInt(cdate.substr(tslen - 3)) * 1000000;
    const userName = request.body.hdnUserName;
    const tradeLegIP = {
        'legNumber': 1,
        'legType': 'cash-equity',
        'book': request.body.tradeBook,
        'ric': request.body.hdnsymbol,
        'instrumentId': request.body.hdnsymbol,
        'currency': request.body.hdncurrencyCode,
        'exchange': request.body.hdnexchange,
        'cfiCode': 'ES',
        'quantity': parseInt(request.body.quantity),
        'price': parseFloat(request.body.price),
        'grossPrice': parseInt(request.body.quantity) * parseFloat(request.body.price),
        'buySellInd': request.body.hdnOrderInd,
    };
    let tradeLegIPFx = {
        'legNumber': 2,
        'legType': 'fx-forward',
        'book': request.body.tradeBook,
        'ric': request.body.hdncurrencyCode + request.body.hedgeCurrency,
        'instrumentId': request.body.hdncurrencyCode + request.body.hedgeCurrency,
        'currency': request.body.hdncurrencyCode,
        'exchange': request.body.hdnexchange,
        'spotRate': parseFloat(request.body.hdnSpotRate),
        'allInRate': parseFloat(request.body.hdnSpotRate),
        'baseCcy': request.body.hdncurrencyCode,
        'counterCcy': request.body.hedgeCurrency,
        'tenor': 'spot',
        'valueDate': '2017-09-28',
        'volume': parseInt(request.body.quantity) * parseFloat(request.body.price) * parseFloat(request.body.hdnSpotRate),
    };
    
    const tradePartiesIP = [{
        'partyRef': 'My Bank Plc.',
        'partyRole': 'oboParty',
    }, {
        'partyRef': 'Your Bank Plc.',
        'partyRole': 'counterParty',
    }];

    const tradeEventIP = {
        'eventType': 'NEW',
        'eventStatus': 'NEW',
        'eventReference': eventRef,
        'exchangeExecutionId': 'FE-MBCK-774',
        'trader': userName,
        'equityLegs': tradeLegIP,
        'fxdLegs': tradeLegIPFx,
        'tradeParties': tradePartiesIP,
        'createdBy': userName,
    };
    
    let tradeTypeCode = 'SINGLE';
    if (request.body.hedgeFlag === 'on') {
        Object.assign(tradeEventIP, {
            'fxdLegs': tradeLegIPFx,
        });
        tradeTypeCode = 'STRUCTURED';
    };

    const tradeIP = {
        'tradeDate': ndate,
        'assetClass': 'eqt',
        'productType': 'cash-equity',
        'tradeType': tradeTypeCode,
        'origSystem': 'TradeCore-UI',
        'osTradeId': cdate,
        'tradeEvents': tradeEventIP,
        'createdBy': userName,
        'createdTimeStamp': {
        seconds: secs,
            nanos: nanos,
        },
    };
    console.log('Sending trade');
    console.log(tradeIP);

    if (LCMConnectFlag) {
        const trackLogValues = {
            'serialNumber': cdate,
            'numOfTrades': 1,
            'subnumber': 1,
            'ULT_requestTimestamp': {
                seconds: secs,
                nanos: nanos,
            },
        };
        console.log('Sending trackLogValues-->');
        console.log(trackLogValues);

        let call2 = client.createTrade({
            trade: tradeIP,
            trackLog: trackLogValues,
        }, createCallback);
        // call.on('end', createCallback);
        // call.end();
    } else {
        let call2 = client.createTrade({
            trade: tradeIP,
        }, createCallback);
    }
});

exports.createCashEqTrade = functions.https.onRequest(eqTradeCore);
