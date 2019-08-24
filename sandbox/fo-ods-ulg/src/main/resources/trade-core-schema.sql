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
  tradeEventId STRING(512) NOT NULL,
  odsTimestamp TIMESTAMP NOT NULL,
  osVersion STRING(32),
  osVersionStatus STRING(64),
  eventType STRING(64) NOT NULL,
  orderId STRING(64),
  validFromTimestamp TIMESTAMP NOT NULL,
  exchangeExecutionId STRING(64),
  createdTimeStamp TIMESTAMP NOT NULL,
  createdBy STRING(64) NOT NULL,
) PRIMARY KEY(tradeId, tradeEventId),
  INTERLEAVE IN PARENT TRADE ON DELETE CASCADE;

CREATE INDEX tradeEventsByTrade ON TRADE_EVENT(tradeId), INTERLEAVE IN TRADE;
​
CREATE TABLE TRADE_PARTY (
  tradeId INT64 NOT NULL,
  tradeEventId STRING(512) NOT NULL,
  book STRING(64) NOT NULL,
  oboPartyRef STRING(64),
  counterpartyRef STRING(64),
  createdTimeStamp TIMESTAMP NOT NULL,
  createdBy STRING(64) NOT NULL,
) PRIMARY KEY(tradeId, tradeEventId),
  INTERLEAVE IN PARENT TRADE_EVENT ON DELETE CASCADE;

CREATE INDEX tradesByBook ON TRADE_PARTY(tradeId, book), INTERLEAVE IN TRADE;
​
CREATE TABLE TRADE_PRODUCT (
  tradeId INT64 NOT NULL,
  tradeEventId STRING(512) NOT NULL,
  packageId INT64,
  noOfLegs INT64,
  legNumber INT64,
  internalProductType STRING(64),
  internalProductRef STRING(64),
  instrumentId STRING(64) NOT NULL,
  ric STRING(64) NOT NULL,
  isin STRING(64),
  currency STRING(3) NOT NULL,
  settlementCcy STRING(3),
  settlementAmount FLOAT64,
  settlementDate DATE,
  exchange STRING(16) NOT NULL,
  cfiCode STRING(64),
  quantity INT64 NOT NULL,
  price FLOAT64 NOT NULL,
  grossPrice FLOAT64 NOT NULL,
  createdTimeStamp TIMESTAMP NOT NULL,
  createdBy STRING(64) NOT NULL,
) PRIMARY KEY(tradeId, tradeEventId),
  INTERLEAVE IN PARENT TRADE_EVENT ON DELETE CASCADE;
​
CREATE INDEX tradesByInstrumentId ON TRADE_PRODUCT(tradeId, instrumentId), INTERLEAVE IN TRADE;
​
CREATE TABLE TRADE_REG (
  tradeId INT64 NOT NULL,
  tradeEventId STRING(512) NOT NULL,
  rpTrader STRING(64) NOT NULL,
  rpSalesman STRING(64),
  rpTraderCountry STRING(64),
  rpSalesmanCountry STRING(64),
  createdTimeStamp TIMESTAMP NOT NULL,
  createdBy STRING(64) NOT NULL,
) PRIMARY KEY(tradeId, tradeEventId),
  INTERLEAVE IN PARENT TRADE_EVENT ON DELETE CASCADE;
​
CREATE INDEX tradesByTrader ON TRADE_REG(tradeId, rpTrader), INTERLEAVE IN TRADE;