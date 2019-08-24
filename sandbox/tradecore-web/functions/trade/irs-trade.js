// Import the Firebase SDK for Google Cloud Functions.
const functions = require('firebase-functions');

const express = require('express');
const tradeCore = express();
const bodyParser = require('body-parser');
tradeCore.use(bodyParser.urlencoded({
    extended: true
}));
tradeCore.use(bodyParser.json());

const LCMConnectFlag = true;
let client = null;
let eventRef = '';
/*  IMPL 1*/
const grpc = require('grpc');
if (LCMConnectFlag) {
    /** LCM GRPC */
    let PROTO_PATH = __dirname + '/proto/com/maplequad/fo/ods/tradecore/proto/service/grpc/tradecore-lifecycle.proto';
    let tradecore = grpc.load(PROTO_PATH).com.maplequad.fo.ods.tradecore.proto.service.grpc;
    //let server = '35.195.236.201:3000';//SIT
    let server = '146.148.126.11:3001'; //UAT
    //let server = functions.config().ir_lcm.url;    
    eventRef = 'LCM-585897895';
    client = new tradecore.TradeCoreLifeCycle(server, grpc.credentials.createInsecure());
} else { /*  IMPL 1 - TradeCoreStore*/
    let PROTO_PATH = __dirname + '/proto/com/maplequad/fo/ods/tradecore/proto/service/grpc/tradecore-store.proto';
    let tradecore = grpc.load(PROTO_PATH).com.maplequad.fo.ods.tradecore.proto.service.grpc;
    //let server = '104.155.34.173:3002'; //SIT
    let server = '35.195.216.51:3002' //UAT
    //let server = functions.config().xa_store.url;        
    eventRef = 'TCS-8356645736';
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

tradeCore.post('/createIrsTrade', (request, response) => {
    console.log(request.body);
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
        tradeLeg1 = tradeEvent.irdLegs[0];
        tradeLeg2 = tradeEvent.irdLegs[1];
        tradeParty1 = tradeEvent.tradeParties[0];
        tradeParty2 = tradeEvent.tradeParties[1];
        tradeParty3 = tradeEvent.tradeParties[2];

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
            <tr><td> Originating System </td><td>${resp.trade.origSystem} </td></tr>
            <tr><td> OS Trade ID </td><td>${resp.trade.osTradeId} </td></tr>
            <tr><td> Created Time Stamp</td><td><span id="spanCTS"></span></td></tr>
            <tr><td> Event Type </td><td>${tradeEvent.eventType} </td></tr>
            <tr><td> Event Status </td><td>${tradeEvent.eventStatus} </td></tr>
            <tr><td> Event Reference </td><td>${tradeEvent.eventReference} </td></tr>
            <tr><td> Trader </td><td>${tradeEvent.trader} </td></tr>
            </table><hr/><table style="border-spacing: 10px;">
            <tr><td> Trade Leg #</td><td>${tradeLeg1.legNumber} </td>
            <td> Trade Leg #</td><td>${tradeLeg2.legNumber} </td></tr>
            <tr><td> Trade Leg Type </td><td>${tradeLeg1.irdLegType} </td>
            <td>Trade Leg Type </td><td>${tradeLeg2.irdLegType} </td></tr>
            <tr><td> Trade Book </td><td>${tradeLeg1.book} </td>
            <td> Trade Book </td><td>${tradeLeg2.book} </td></tr>
            <tr><td> Instrument ID </td><td>${tradeLeg1.instrumentId} </td>
            <td> Instrument ID </td><td>${tradeLeg2.instrumentId} </td></tr>
            <tr><td> Notional </td><td>${tradeLeg1.notional} </td>
            <td> Notional </td><td>${tradeLeg2.notional} </td></tr>
            <tr><td> Currency </td><td>${tradeLeg1.currency} </td>
            <td> Currency </td><td>${tradeLeg2.currency} </td></tr>
            <tr><td> Exchange Code </td><td>${tradeLeg1.exchange} </td>
            <td> Exchange Code </td><td>${tradeLeg2.exchange} </td></tr>
            <tr><td> Maturity Date </td><td><span id="spanMaturityDate1"></span></td>
            <td> Maturity Date </td><td><span id="spanMaturityDate2"></span></td></tr>
            <tr><td> Index </td><td>${tradeLeg1.index} </td>
            <td> Index </td><td>${tradeLeg2.index} </td></tr>
            <tr><td> Term </td><td>${tradeLeg1.term} </td>
            <td> Term </td><td>${tradeLeg2.term} </td></tr>
            <tr><td> Rate </td><td>${tradeLeg1.rate} </td>
            <td> Rate </td><td>${tradeLeg2.rate} </td></tr>
            <tr><td> Basis </td><td>${tradeLeg1.basis} </td>
            <td> Basis </td><td>${tradeLeg2.basis} </td></tr>
            <tr><td> Spread </td><td>${tradeLeg1.spread} </td>
            <td> Spread </td><td>${tradeLeg2.spread} </td></tr>
            </table>
            <hr/>
            <table style="border-spacing: 10px;">
            <tr><td> Party1 Ref </td><td>${tradeParty1.partyRef} </td></td>
            <td> Party1 Role </td><td>${tradeParty1.partyRole} </td></td></tr>
            <tr><td> Party2 Ref </td><td>${tradeParty2.partyRef} </td></td>
            <td> Party2 Role </td><td>${tradeParty2.partyRole} </td></td></tr>
            <tr><td> Party3 Ref </td><td>${tradeParty3.partyRef} </td></td>
            <td> Party3 Role </td><td>${tradeParty3.partyRole} </td></tr>
            </table>
            </div>
            <script>
                let mDate = new Date(${tradeLeg1.maturityDate}).toDateString();
                document.getElementById('spanMaturityDate1').innerHTML = mDate;
                document.getElementById('spanMaturityDate2').innerHTML = mDate;
                let cTS = new Date(${resp.trade.createdTimeStamp.seconds * 1000}).toLocaleString();
                document.getElementById('spanCTS').innerHTML = cTS;
            </script>
            </body>
        </html>`);
    }
    const sdate = new Date(request.body.txtStartDate);
    const edate = new Date(request.body.txtEndDate);
    const startDate = sdate.getTime() + sdate.getTimezoneOffset() * 60000;
    const endDate = edate.getTime() + edate.getTimezoneOffset() * 60000;
    const ldate = new Date();
    const ndate = Math.floor(ldate.getTime() + ldate.getTimezoneOffset() * 60000);
    const cdate = ndate + '';
    const tslen = cdate.length;
    const secs = parseInt(cdate.substr(0, tslen - 3));
    const nanos = parseInt(cdate.substr(tslen - 3)) * 1000000;
    const userName = request.body.hdnUserName;
    const tradeLegIP = [{
        'legNumber': 1,
        'legType': 'ir-swap',
        'book': request.body.txtTradeBook,
        'internalProductType': request.body.txtProduct,
        'internalProductRef': request.body.txtProductDesc,
        'isin': request.body.txtProduct,
        'ric': request.body.txtProduct,
        'instrumentId': request.body.txtProduct,
        'currency': request.body.cboPayLegCcy,
        'exchange': request.body.hdnExchange,

        // Interest Rate Derivatives Attributes
        'irdLegType': 'Pay',
        'maturityDate': endDate,
        'notional': parseFloat(request.body.txtPayLegNotional.replace(/,/g, '')),
        'index': request.body.cboPayLegIdx,
        'notionalExp': request.body.cboPayLegNtlExp,
        'term': request.body.cboPayLegTerm,
        'rate': parseFloat(request.body.txtPayLegRate),
        'basis': request.body.cboPayLegBasis,
        'spread': parseFloat(request.body.txtPayLegSpread),
        'settlementCcy': request.body.cboRecLegCcy,
        'settlementAmount': parseFloat(request.body.txtRecLegNotional.replace(/,/g, '')),
        'settlementDate': endDate,
    }, {
        'legNumber': 2,
        'legType': 'ir-swap',
        'book': request.body.txtTradeBook,
        'internalProductType': request.body.txtProduct,
        'internalProductRef': request.body.txtProductDesc,
        'isin': request.body.txtProduct,
        'ric': request.body.txtProduct,
        'instrumentId': request.body.txtProduct,
        'currency': request.body.cboRecLegCcy,
        'exchange': request.body.hdnExchange,

        // Interest Rate Derivatives Attributes
        'irdLegType': 'Receive',
        'maturityDate': endDate,
        'notional': parseFloat(request.body.txtRecLegNotional.replace(/,/g, '')),
        'index': request.body.cboRecLegIdx,
        'notionalExp': request.body.cboRecLegNtlExp,
        'term': request.body.cboRecLegTerm,
        'rate': parseFloat(request.body.txtRecLegRate),
        'basis': request.body.cboRecLegBasis,
        'spread': parseFloat(request.body.txtRecLegSpread),
        'settlementCcy': request.body.cboPayLegCcy,
        'settlementAmount': parseFloat(request.body.txtPayLegNotional.replace(/,/g, '')),
        'settlementDate': endDate,
    },
    ];
    // console.log(JSON.stringify(tradeLegIP, null, 4));

    const tradePartiesIP = [{
        'partyRef': 'My Bank',
        'partyRole': 'oboParty',
    }, {
        'partyRef': request.body.txtCounterParty,
        'partyRole': 'counterParty',
    }, {
        'partyRef': request.body.txtClearing,
        'partyRole': 'clearingParty',
    }];
    const tradeEventIP = {
        'eventType': 'NEW',
        'eventStatus': 'NEW',
        'eventReference': eventRef,
        'exchangeExecutionId': 'FE-MBCK-774',
        'trader': userName,
        'irdLegs': tradeLegIP,
        'tradeParties': tradePartiesIP,
        'createdBy': userName,
    };
    const tradeIP = {
        'tradeDate': startDate,
        'assetClass': 'ird',
        'productType': 'ir-swap',
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

exports.createIrsTrade = functions.https.onRequest(tradeCore);
