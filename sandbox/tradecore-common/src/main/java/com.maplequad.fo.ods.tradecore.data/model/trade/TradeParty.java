package com.maplequad.fo.ods.tradecore.data.model.trade;

import com.maplequad.fo.ods.tradecore.data.model.BiTemporal;

/***
 * Trade - List of all attributes that form party of a trade
 *
 * @author Madhav Mindhe
 * @since :   11/08/2017
 */
public class TradeParty extends BiTemporal {

    public static final String ENTITY = "TRADE_PARTY";

    public static final String TRADE_ID = "tradeId";
    public static final String PARTY_REF = "partyRef";
    public static final String PARTY_ROLE = "partyRole";

    //Primary Key
    private String tradeId;
    private String partyRef;

    //Attributes
    private String partyRole;

    /**
     *
     */
    public TradeParty() {
        //this.setValidTimeTo(END_OF_TIME);
        //this.setTransactionTimeTo(END_OF_TIME);
    }

    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }

    public String getPartyRef() {
        return partyRef;
    }

    public void setPartyRef(String partyRef) {
        this.partyRef = partyRef;
    }

    public String getPartyRole() {
        return partyRole;
    }

    public void setPartyRole(String partyRole) {
        this.partyRole = partyRole;
    }

    @Override
    public String toString() {
        return "TradeParty{" +
                "tradeId=" + tradeId +
                ", validTimeFrom=" + this.getValidTimeFrom() + '\'' +
                ", validTimeTo=" + this.getValidTimeTo() + '\'' +
                ", transactionTimeFrom=" + this.getTransactionTimeFrom() + '\'' +
                ", transactionTimeTo=" + this.getTransactionTimeTo() + '\'' +
                ", partyRef='" + partyRef + '\'' +
                ", partyRole='" + partyRole + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TradeParty)) return false;

        TradeParty that = (TradeParty) o;

        if (!getTradeId().equals(that.getTradeId())) return false;
        return getPartyRef().equals(that.getPartyRef());
    }

    @Override
    public int hashCode() {
        int result = getTradeId().hashCode();
        result = 31 * result + getPartyRef().hashCode();
        return result;
    }
}
