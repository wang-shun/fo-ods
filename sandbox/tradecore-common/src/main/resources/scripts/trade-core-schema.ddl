CREATE TABLE BULK_AMEND (
  bulkAmendId STRING(32) NOT NULL,
  assetClass STRING(16) NOT NULL,
  bulkAmendType STRING(32) NOT NULL,
  description STRING(256) NOT NULL,
  scope STRING(2048) NOT NULL,
  status STRING(16) NOT NULL,
  createdBy STRING(64) NOT NULL,
  asOf TIMESTAMP,
  osRequestTimestamp TIMESTAMP,
  lcmStartTimestamp TIMESTAMP,
  dsStartTimestamp TIMESTAMP,
  dbStartTimestamp TIMESTAMP,
  dbFinishTimestamp TIMESTAMP,
  dsFinishTimestamp TIMESTAMP,
  lcmFinishTimestamp TIMESTAMP,
) PRIMARY KEY(bulkAmendId);
CREATE INDEX searchByBulkAmendType ON BULK_AMEND (bulkAmendType);
CREATE INDEX findByAssetClass ON BULK_AMEND (assetClass);

CREATE TABLE BULK_AMEND_TASK (
  bulkAmendId STRING(32) NOT NULL,
  validTimeFrom TIMESTAMP NOT NULL,
  transactionTimeFrom TIMESTAMP NOT NULL,
  validTimeTo TIMESTAMP NOT NULL,
  transactionTimeTo TIMESTAMP NOT NULL,
  tradeId STRING(32) NOT NULL,
  action STRING(16) NOT NULL,
  metadata STRING(128),
) PRIMARY KEY(bulkAmendId, tradeId),
  INTERLEAVE IN PARENT BULK_AMEND ON DELETE CASCADE;

CREATE TABLE TRADE (
  tradeId STRING(32) NOT NULL,
  tradeDate DATE NOT NULL,
  assetClass STRING(16) NOT NULL,
  tradeType STRING(16) NOT NULL,
  productType STRING(64) NOT NULL,
  origSystem STRING(64) NOT NULL,
  osTradeId STRING(64) NOT NULL,
  createdTimeStamp TIMESTAMP NOT NULL,
  createdBy STRING(64) NOT NULL,
) PRIMARY KEY(tradeId);

CREATE INDEX searchByAssetClass ON TRADE(assetClass);

CREATE UNIQUE INDEX searchByOSTradeID ON TRADE(osTradeId);

CREATE TABLE TRADE_EVENT (
  tradeId STRING(32) NOT NULL,
  validTimeFrom TIMESTAMP NOT NULL,
  transactionTimeFrom TIMESTAMP NOT NULL,
  validTimeTo TIMESTAMP NOT NULL,
  transactionTimeTo TIMESTAMP NOT NULL,
  parentTradeId STRING(64),
  eventType STRING(64) NOT NULL,
  eventStatus STRING(32) NOT NULL,
  eventReference STRING(64) NOT NULL,
  eventRemarks STRING(512),
  noOfLegs INT64,
  osVersion STRING(32),
  osVersionStatus STRING(64),
  orderId STRING(64),
  exchangeExecutionId STRING(64),
  trader STRING(64) NOT NULL,
  salesman STRING(64),
  traderCountry STRING(64),
  salesmanCountry STRING(64),
  createdBy STRING(64) NOT NULL,
  activeFlag BOOL NOT NULL,
  lockedFlag BOOL,
) PRIMARY KEY(tradeId, validTimeFrom DESC, transactionTimeFrom DESC),
  INTERLEAVE IN PARENT TRADE ON DELETE CASCADE;

CREATE INDEX tradeEventsByTrade ON TRADE_EVENT(tradeId);

CREATE INDEX tradesByTrader ON TRADE_EVENT(trader);

CREATE TABLE EQUITY_LEG (
  tradeId STRING(32) NOT NULL,
  validTimeFrom TIMESTAMP NOT NULL,
  transactionTimeFrom TIMESTAMP NOT NULL,
  validTimeTo TIMESTAMP NOT NULL,
  transactionTimeTo TIMESTAMP NOT NULL,
  legNumber INT64 NOT NULL,
  legType STRING(32),
  book STRING(32),
  internalProductType STRING(64),
  internalProductRef STRING(64),
  instrumentId STRING(64) NOT NULL,
  ric STRING(64),
  isin STRING(64),
  currency STRING(3) NOT NULL,
  exchange STRING(16) NOT NULL,
  cfiCode STRING(64),
  quantity INT64 NOT NULL,
  price FLOAT64 NOT NULL,
  grossPrice FLOAT64 NOT NULL,
  buySellInd STRING(8) NOT NULL,
) PRIMARY KEY(tradeId, validTimeFrom DESC, transactionTimeFrom DESC, legNumber),
  INTERLEAVE IN PARENT TRADE_EVENT ON DELETE CASCADE;

CREATE INDEX eqTradesByBook ON EQUITY_LEG(book);

CREATE INDEX eqTradesByInstrumentId ON EQUITY_LEG(instrumentId);

CREATE TABLE FXD_LEG (
  tradeId STRING(32) NOT NULL,
  validTimeFrom TIMESTAMP NOT NULL,
  transactionTimeFrom TIMESTAMP NOT NULL,
  validTimeTo TIMESTAMP NOT NULL,
  transactionTimeTo TIMESTAMP NOT NULL,
  legNumber INT64 NOT NULL,
  legType STRING(32),
  book STRING(32),
  internalProductType STRING(64),
  internalProductRef STRING(64),
  instrumentId STRING(64) NOT NULL,
  ric STRING(64),
  isin STRING(64),
  currency STRING(3) NOT NULL,
  exchange STRING(16) NOT NULL,
  spotRate FLOAT64 NOT NULL,
  allInRate FLOAT64 NOT NULL,
  volume FLOAT64 NOT NULL,
  baseCcy STRING(3) NOT NULL,
  counterCcy STRING(3) NOT NULL,
  tenor STRING(5),
  valueDate DATE NOT NULL,
  buySellInd STRING(8) NOT NULL,
) PRIMARY KEY(tradeId, validTimeFrom DESC, transactionTimeFrom DESC, legNumber),
  INTERLEAVE IN PARENT TRADE_EVENT ON DELETE CASCADE;

CREATE INDEX fxTradesByBook ON FXD_LEG(book);

CREATE INDEX fxTradesByInstrumentId ON FXD_LEG(instrumentId);

CREATE TABLE IRD_LEG (
  tradeId STRING(32) NOT NULL,
  validTimeFrom TIMESTAMP NOT NULL,
  transactionTimeFrom TIMESTAMP NOT NULL,
  validTimeTo TIMESTAMP NOT NULL,
  transactionTimeTo TIMESTAMP NOT NULL,
  legNumber INT64 NOT NULL,
  legType STRING(32),
  book STRING(32),
  internalProductType STRING(64),
  internalProductRef STRING(64),
  instrumentId STRING(64) NOT NULL,
  ric STRING(64),
  isin STRING(64),
  currency STRING(3) NOT NULL,
  exchange STRING(16) NOT NULL,
  irdLegType STRING(16),
  maturityDate DATE,
  notional FLOAT64 NOT NULL,
  index STRING(16),
  notionalExp STRING(16),
  term STRING(16),
  rate FLOAT64,
  basis STRING(16),
  spread FLOAT64,
  settlementAmount FLOAT64,
  settlementDate DATE,
  settlementCcy STRING(3),
  buySellInd STRING(8) NOT NULL,
) PRIMARY KEY(tradeId, validTimeFrom DESC, transactionTimeFrom DESC, legNumber),
  INTERLEAVE IN PARENT TRADE_EVENT ON DELETE CASCADE;

CREATE INDEX irTradesByBook ON IRD_LEG(book);

CREATE INDEX irTradesByInstrumentId ON IRD_LEG(instrumentId);

CREATE TABLE TRADE_PARTY (
  tradeId STRING(32) NOT NULL,
  validTimeFrom TIMESTAMP NOT NULL,
  transactionTimeFrom TIMESTAMP NOT NULL,
  validTimeTo TIMESTAMP NOT NULL,
  transactionTimeTo TIMESTAMP NOT NULL,
  partyRef STRING(64) NOT NULL,
  partyRole STRING(64) NOT NULL,
) PRIMARY KEY(tradeId, validTimeFrom DESC, transactionTimeFrom DESC, partyRef),
  INTERLEAVE IN PARENT TRADE_EVENT ON DELETE CASCADE;
