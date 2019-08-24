select TRADE.tradeId AS TRADEtradeId,TRADE.tradeDate AS TRADEtradeDate,TRADE.primaryAssetClass AS TRADEprimaryAssetClass,TRADE.productType AS TRADEproductType,TRADE.origSystem AS TRADEorigSystem,TRADE.osTradeId AS TRADEosTradeId,TRADE.createdTimeStamp AS TRADEcreatedTimeStamp,TRADE.createdBy AS TRADEcreatedBy,TRADE_EVENT.tradeId AS TRADE_EVENTtradeId,TRADE_EVENT.validTimeFrom AS TRADE_EVENTvalidTimeFrom,TRADE_EVENT.validTimeTo AS TRADE_EVENTvalidTimeTo,TRADE_EVENT.transactionTimeFrom AS TRADE_EVENTtransactionTimeFrom,TRADE_EVENT.transactionTimeTo AS TRADE_EVENTtransactionTimeTo,TRADE_EVENT.parentTradeId AS TRADE_EVENTparentTradeId,TRADE_EVENT.eventType AS TRADE_EVENTeventType,TRADE_EVENT.eventStatus AS TRADE_EVENTeventStatus,TRADE_EVENT.eventReference AS TRADE_EVENTeventReference,TRADE_EVENT.noOfLegs AS TRADE_EVENTnoOfLegs,TRADE_EVENT.osVersion AS TRADE_EVENTosVersion,TRADE_EVENT.osVersionStatus AS TRADE_EVENTosVersionStatus,TRADE_EVENT.orderId AS TRADE_EVENTorderId,TRADE_EVENT.exchangeExecutionId AS TRADE_EVENTexchangeExecutionId,TRADE_EVENT.trader AS TRADE_EVENTtrader,TRADE_EVENT.salesman AS TRADE_EVENTsalesman,TRADE_EVENT.traderCountry AS TRADE_EVENTtraderCountry,TRADE_EVENT.salesmanCountry AS TRADE_EVENTsalesmanCountry,TRADE_EVENT.createdBy AS TRADE_EVENTcreatedBy,TRADE_LEG.tradeId AS TRADE_LEGtradeId,TRADE_LEG.validTimeFrom AS TRADE_LEGvalidTimeFrom,TRADE_LEG.validTimeTo AS TRADE_LEGvalidTimeTo,TRADE_LEG.transactionTimeFrom AS TRADE_LEGtransactionTimeFrom,TRADE_LEG.transactionTimeTo AS TRADE_LEGtransactionTimeTo,TRADE_LEG.legNumber AS TRADE_LEGlegNumber,TRADE_LEG.book AS TRADE_LEGbook,TRADE_LEG.internalProductType AS TRADE_LEGinternalProductType,TRADE_LEG.internalProductRef AS TRADE_LEGinternalProductRef,TRADE_LEG.instrumentId AS TRADE_LEGinstrumentId,TRADE_LEG.ric AS TRADE_LEGric,TRADE_LEG.isin AS TRADE_LEGisin,TRADE_LEG.currency AS TRADE_LEGcurrency,TRADE_LEG.settlementCcy AS TRADE_LEGsettlementCcy,TRADE_LEG.settlementAmount AS TRADE_LEGsettlementAmount,TRADE_LEG.settlementDate AS TRADE_LEGsettlementDate,TRADE_LEG.maturityDate AS TRADE_LEGmaturityDate,TRADE_LEG.exchange AS TRADE_LEGexchange,TRADE_LEG.cfiCode AS TRADE_LEGcfiCode,TRADE_LEG.quantity AS TRADE_LEGquantity,TRADE_LEG.price AS TRADE_LEGprice,TRADE_LEG.grossPrice AS TRADE_LEGgrossPrice,TRADE_PARTY.tradeId AS TRADE_PARTYtradeId,TRADE_PARTY.validTimeFrom AS TRADE_PARTYvalidTimeFrom,TRADE_PARTY.validTimeTo AS TRADE_PARTYvalidTimeTo,TRADE_PARTY.transactionTimeFrom AS TRADE_PARTYtransactionTimeFrom,TRADE_PARTY.transactionTimeTo AS TRADE_PARTYtransactionTimeTo,TRADE_PARTY.partyRef AS TRADE_PARTYpartyRef,TRADE_PARTY.partyRole AS TRADE_PARTYpartyRole
 from TRADE,TRADE_EVENT,TRADE_LEG,TRADE_PARTY
 where TRADE.tradeId = TRADE_EVENT.tradeId
 and TRADE.tradeId = TRADE_LEG.tradeId
 and TRADE.tradeId = TRADE_PARTY.tradeId
 and TRADE_EVENT.validTimeFrom = TRADE_LEG.validTimeFrom
 and TRADE_EVENT.validTimeFrom = TRADE_PARTY.validTimeFrom
 and TRADE.tradeId = 8854046600017693053 and TRADE_EVENT.validTimeFrom < '2017-08-15T22:35:48.003000000Z' order by TRADE_EVENT.validTimeFrom desc


select count(*)
from trade,  trade_event
where
trade.tradeId = trade_event.tradeId
and trade_event.exchangeExecutionId like 'EE%'

--#Bulk Amend
select TRADE.tradeId AS TRADEtradeId,TRADE.tradeDate AS TRADEtradeDate,TRADE_EVENT.eventType AS TRADE_EVENTeventType,TRADE_EVENT.eventStatus AS TRADE_EVENTeventStatus,TRADE_EVENT.eventReference AS TRADE_EVENTeventReference,TRADE_EVENT.noOfLegs AS TRADE_EVENTnoOfLegs,TRADE_EVENT.trader AS TRADE_EVENTtrader,TRADE_EVENT.activeFlag AS TRADE_EVENTactiveFlag,TRADE_EVENT.lockedFlag AS TRADE_EVENTlockedFlag,IRD_LEG.legNumber AS IRD_LEGlegNumber,IRD_LEG.legType AS IRD_LEGlegType,IRD_LEG.book AS IRD_LEGbook,IRD_LEG.irdLegType AS IRD_LEGirdLegType,IRD_LEG.maturityDate AS IRD_LEGmaturityDate,IRD_LEG.notional AS IRD_LEGnotional,TRADE_PARTY.partyRef AS TRADE_PARTYpartyRef,TRADE_PARTY.partyRole AS TRADE_PARTYpartyRole
 from TRADE,TRADE_EVENT,IRD_LEG,TRADE_PARTY
 where TRADE.tradeId = TRADE_EVENT.tradeId
 and TRADE.tradeId = IRD_LEG.tradeId
 and TRADE.tradeId = TRADE_PARTY.tradeId
 and TRADE_EVENT.validTimeFrom = IRD_LEG.validTimeFrom
 and TRADE_EVENT.validTimeFrom = TRADE_PARTY.validTimeFrom
 and (TRADE_EVENT.trader = 'IRD_TRD_CMPR10') and (TRADE_EVENT.activeFlag = true)
