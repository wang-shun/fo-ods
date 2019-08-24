CREATE TABLE TRADE (
  tradeId INT64 NOT NULL,
  tradeDate DATE NOT NULL,
  primaryAssetClass STRING(16) NOT NULL,
  productType STRING(64) NOT NULL,
  origSystem STRING(64) NOT NULL,
  osTradeId STRING(64) NOT NULL,
  createdTimeStamp TIMESTAMP NOT NULL,
  createdBy STRING(64) NOT NULL,
) PRIMARY KEY(tradeId);

CREATE TABLE TRADE_EVENT (
  tradeId INT64 NOT NULL,
  validFromTimestamp TIMESTAMP NOT NULL,
  createdTimeStamp TIMESTAMP NOT NULL,
  parentTradeId INT64,
  eventType STRING(64) NOT NULL,
  eventStatus STRING(32) NOT NULL,
  eventReference STRING(64),
  noOfLegs INT64,
  osVersion STRING(32),
  osVersionStatus STRING(64),
  orderId STRING(64),
  exchangeExecutionId STRING(64),
  rpTrader STRING(64) NOT NULL,
  rpSalesman STRING(64),
  rpTraderCountry STRING(64),
  rpSalesmanCountry STRING(64),
  createdBy STRING(64) NOT NULL,
) PRIMARY KEY(tradeId, validFromTimestamp, createdTimeStamp),
  INTERLEAVE IN PARENT TRADE ON DELETE CASCADE;

CREATE INDEX tradeEventsByTrade ON TRADE_EVENT(tradeId), INTERLEAVE IN TRADE;
CREATE INDEX tradesByTrader ON TRADE_EVENT(tradeId, rpTrader), INTERLEAVE IN TRADE;

CREATE TABLE TRADE_PARTY (
  tradeId INT64 NOT NULL,
  validFromTimestamp TIMESTAMP NOT NULL,
  createdTimeStamp TIMESTAMP NOT NULL,
  partyRef STRING(64) NOT NULL,
  partyRole STRING(64) NOT NULL
) PRIMARY KEY(tradeId, validFromTimestamp, createdTimeStamp, partyRef),
  INTERLEAVE IN PARENT TRADE_EVENT ON DELETE CASCADE;
​
CREATE TABLE TRADE_LEG (
  tradeId INT64 NOT NULL,
  validFromTimestamp TIMESTAMP NOT NULL,
  createdTimeStamp TIMESTAMP NOT NULL,
  legNumber INT64 NOT NULL,
  book STRING(32),
  internalProductType STRING(64),
  internalProductRef STRING(64),
  instrumentId STRING(64) NOT NULL,
  ric STRING(64),
  isin STRING(64),
  currency STRING(3) NOT NULL,
  settlementCcy STRING(3),
  settlementAmount FLOAT64,
  settlementDate DATE,
  exchange STRING(16) NOT NULL,
  cfiCode STRING(64),
  quantity INT64 NOT NULL,
  price FLOAT64 NOT NULL,
  grossPrice FLOAT64 NOT NULL
) PRIMARY KEY(tradeId, validFromTimestamp, createdTimeStamp, legNumber),
  INTERLEAVE IN PARENT TRADE_EVENT ON DELETE CASCADE;
​
CREATE INDEX tradesByInstrumentId ON TRADE_LEG(tradeId, instrumentId), INTERLEAVE IN TRADE;
​CREATE INDEX tradesByBook ON TRADE_LEG(tradeId, book), INTERLEAVE IN TRADE;

CREATE TABLE TRADE_BULK_AMEND (
  bulkAmendId STRING(512) NOT NULL,
  validFromTimestamp TIMESTAMP NOT NULL,
  createdTimeStamp TIMESTAMP NOT NULL,
  bulkAmendType STRING(32) NOT NULL,
  statusCode STRING(16) NOT NULL,
  createdBy STRING(64) NOT NULL
) PRIMARY KEY(bulkAmendId,validFromTimestamp,createdTimeStamp);

CREATE TABLE TRADE_BULK_AMEND_TASK (
  bulkAmendId STRING(512) NOT NULL,
  validFromTimestamp TIMESTAMP NOT NULL,
  createdTimeStamp TIMESTAMP NOT NULL,
  tradeId INT64 NOT NULL,
  actionCode STRING(16) NOT NULL,
  metadata STRING(128)
  ) PRIMARY KEY(bulkAmendId,validFromTimestamp,createdTimeStamp,tradeId),
  INTERLEAVE IN PARENT TRADE_BULK_AMEND ON DELETE CASCADE;
