CREATE TABLE trades4 (
	tradeId STRING(256) NOT NULL,
	tradeVersion INT64 NOT NULL,
	book STRING(64) NOT NULL,
	buySell BOOL NOT NULL,
	ccy STRING(16) NOT NULL,
	consideration FLOAT64 NOT NULL,
	executionVenue STRING(16) NOT NULL,
	instrumentId STRING(256) NOT NULL,
	instrumentIsin STRING(MAX),
	instrumentRic STRING(256) NOT NULL,
	price FLOAT64 NOT NULL,
	productType STRING(16) NOT NULL,
	quantity FLOAT64 NOT NULL,
	settlementCcy STRING(16) NOT NULL,
	client STRING(16) NOT NULL,
	account INT64 NOT NULL,
	account1m INT64 NOT NULL,
	account10m INT64 NOT NULL,
	book1000 STRING(16) NOT NULL
) PRIMARY KEY (tradeId, tradeVersion);

CREATE NULL_FILTERED INDEX idxClient
ON trades4 (
	client
);

CREATE NULL_FILTERED INDEX idxAccount
ON trades4 (
	account
);

CREATE NULL_FILTERED INDEX idxAccount1m
ON trades4 (
	account1m
);

CREATE NULL_FILTERED INDEX idxAccount10m
ON trades4 (
	account10m
);

CREATE NULL_FILTERED INDEX idxBook1000
ON trades4 (
	book1000
);


CREATE TABLE trades4_legs (
	tradeId STRING(256) NOT NULL,
	tradeVersion INT64 NOT NULL,
	legId INT64 NOT NULL,
	ccy STRING(16) NOT NULL,
	type STRING(256) NOT NULL,
	quantity FLOAT64 NOT NULL,
	client STRING(16) NOT NULL,
	account INT64 NOT NULL,
	account1m INT64 NOT NULL,
	account10m INT64 NOT NULL,
	book STRING(64) NOT NULL,
	book1000 STRING(16) NOT NULL
) PRIMARY KEY (tradeId, tradeVersion, legId),
INTERLEAVE IN PARENT trades4 ON DELETE CASCADE;

CREATE NULL_FILTERED INDEX idxLegAccount10m
ON trades4_legs (
	account10m
);
