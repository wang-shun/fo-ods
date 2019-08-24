CREATE TABLE trades3 (
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
	book1000 STRING(16) NOT NULL
) PRIMARY KEY (tradeId, tradeVersion)

CREATE NULL_FILTERED INDEX idxClient
ON trades3 (
	client
)

CREATE NULL_FILTERED INDEX idxAccount
ON trades3 (
	account
)

CREATE NULL_FILTERED INDEX idxBook1000
ON trades3 (
	book1000
)
