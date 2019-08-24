CREATE TABLE stats (
	tradeId STRING(256) NOT NULL,
	ult TIMESTAMP NOT NULL,
	action STRING(256) NOT NULL,
	dbEnd TIMESTAMP NOT NULL,
	dbStart TIMESTAMP NOT NULL,
	dbTime INT64 NOT NULL,
	dltArriveTime TIMESTAMP NOT NULL,
	errormsg STRING(MAX),
	lcCrudEnd TIMESTAMP NOT NULL,
	lcCrudStart TIMESTAMP NOT NULL,
	lcCrudTime INT64 NOT NULL,
	lcStart TIMESTAMP NOT NULL,
	lcTime INT64 NOT NULL,
	lcToDbTime INT64 NOT NULL,
	lcToDltTime INT64 NOT NULL,
	osTradeid STRING(256) NOT NULL,
	serialNumber INT64 NOT NULL,
	totalTime INT64 NOT NULL,
	totalLcmToDltTime INT64 NOT NULL,
	ultToLcTime INT64 NOT NULL,
) PRIMARY KEY (tradeId, ult,action);

CREATE INDEX idxserialNumber 
ON stats (
	serialNumber
)
