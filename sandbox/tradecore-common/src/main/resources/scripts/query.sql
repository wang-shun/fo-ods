SELECT * from
(select tradeId, validTimeFrom, transactionTimeFrom
 from Equity_Leg@{FORCE_INDEX=eqTradesByInstrumentId}
 where instrumentId = 'ABC.D')
INNER JOIN
(select tradeId, max(validTimeFrom) as validTimeFrom, max(transactionTimeFrom) as transactionTimeFrom
 from trade_event
where validTimeFrom <= '2017-09-14' and transactionTimeFrom <= '2017-09-14'
 group by tradeId)
USING (tradeId, validTimeFrom, transactionTimeFrom)
INNER JOIN TRADE USING (tradeId)
INNER JOIN TRADE_EVENT USING (tradeId, validTimeFrom, transactionTimeFrom)




SELECT * from (select tradeId, max(validTimeFrom) as validTimeFrom, max(transactionTimeFrom) as transactionTimeFrom from trade_event
where validTimeFrom <= '2017-09-14' and transactionTimeFrom <= '2017-09-14' and ostradeId = '' group by tradeId)
INNER JOIN
(select tradeId, max(validTimeFrom) as validTimeFrom, max(transactionTimeFrom) as transactionTimeFrom from trade_event
where validTimeFrom <= '2017-09-14' and transactionTimeFrom <= '2017-09-14'group by tradeId)
USING (tradeId, validTimeFrom, transactionTimeFrom)
INNER JOIN TRADE
USING (tradeId)
INNER JOIN TRADE_EVENT
USING (tradeId, validTimeFrom, transactionTimeFrom)

