package com.maplequad.fo.ods.tradecore.data.model;

import com.google.cloud.Timestamp;

/***
 * BiTemporal - List of all attributes that form help to achieve bi-temporality
 *
 * @author Madhav Mindhe
 * @since :   11/08/2017
 */
public class BiTemporal {

    public static final String VALID_TIME_FROM = "validTimeFrom";
    public static final String VALID_TIME_TO = "validTimeTo";
    public static final String TXN_TIME_FROM = "transactionTimeFrom";
    public static final String TXN_TIME_TO = "transactionTimeTo";

    private Timestamp validTimeFrom;
    private Timestamp validTimeTo;
    private Timestamp transactionTimeFrom;
    private Timestamp transactionTimeTo;

    public Timestamp getValidTimeFrom() {
        return validTimeFrom;
    }

    public void setValidTimeFrom(Timestamp validTimeFrom) {
        this.validTimeFrom = validTimeFrom;
    }

    public Timestamp getValidTimeTo() {
        return validTimeTo;
    }

    public void setValidTimeTo(Timestamp validTimeTo) {
        this.validTimeTo = validTimeTo;
    }

    public Timestamp getTransactionTimeFrom() {
        return transactionTimeFrom;
    }

    public void setTransactionTimeFrom(Timestamp transactionTimeFrom) {
        this.transactionTimeFrom = transactionTimeFrom;
    }

    public Timestamp getTransactionTimeTo() {
        return transactionTimeTo;
    }

    public void setTransactionTimeTo(Timestamp transactionTimeTo) {
        this.transactionTimeTo = transactionTimeTo;
    }

}
