CREATE TABLE statsdex (
	tradeId STRING(256) NOT NULL,
    osTradeid STRING(256) NOT NULL,
    action STRING(256) NOT NULL,

    ult TIMESTAMP NOT NULL,
    ultRaw INT64 NOT NULL,
    lcStart TIMESTAMP NOT NULL,
    lcStartRaw INT64 NOT NULL,
    ultToLcTime INT64 NOT NULL,
    lcCrudStart TIMESTAMP NOT NULL,
    lcCrudStartRaw INT64 NOT NULL,
    lcCrudEnd TIMESTAMP NOT NULL,
    lcCrudEndRaw TIMESTAMP NOT NULL,
    lcCrudTime INT64 NOT NULL,
    dbStart TIMESTAMP NOT NULL,
    dbEnd TIMESTAMP NOT NULL,
    dbTime INT64 NOT NULL,
    lcToDbTime INT64 NOT NULL,
    lcTime INT64 NOT NULL,
    statsStart TIMESTAMP NOT NULL,
    lcToDltTime INT64 NOT NULL,
    totalTime INT64 NOT NULL
) PRIMARY KEY (tradeId, ult);
