package com.maplequad.fo.ods.tradecore.client.model;

import java.util.List;

/***
 * TradeInfo
 *
 * This class contains list of trade attributes and output type details.
 *
 * @author Madhav Mindhe
 * @since :   23/07/2017
 */
public class TradeInfo {

    /***
     * OutputType may be file, jms or hashmap depending on how output is expected.
     */
    private String outputType;

    /***
     * This would be a folder in case of outputType is file or JMS Queue or Topic if outputType is jms
     * or blank if outputType is just hashmap of values
     * */
    private String outputDest;
    private List<TradeAttribute> tradeEventAttributes;
    private List<TradeAttribute> tradeLegAttributes;
    private List<TradeAttribute> tradePartyAttributes;

    /**
     * @return outputType
     */
    public String getOutputType() {
        return outputType;
    }

    /**
     * Sets the outputType to given value
     *
     * @param outputType
     */
    public void setOutputType(String outputType) {
        this.outputType = outputType;
    }

    /**
     * @return outputDest
     */
    public String getOutputDest() {
        return outputDest;
    }

    /**
     * Sets the outputDest to given value
     *
     * @param outputDest
     */
    public void setOutputDest(String outputDest) {
        this.outputDest = outputDest;
    }


    public List<TradeAttribute> getTradeEventAttributes() {
        return tradeEventAttributes;
    }

    public void setTradeEventAttributes(List<TradeAttribute> tradeEventAttributes) {
        this.tradeEventAttributes = tradeEventAttributes;
    }

    public List<TradeAttribute> getTradeLegAttributes() {
        return tradeLegAttributes;
    }

    public void setTradeLegAttributes(List<TradeAttribute> tradeLegAttributes) {
        this.tradeLegAttributes = tradeLegAttributes;
    }

    public List<TradeAttribute> getTradePartyAttributes() {
        return tradePartyAttributes;
    }

    public void setTradePartyAttributes(List<TradeAttribute> tradePartyAttributes) {
        this.tradePartyAttributes = tradePartyAttributes;
    }
}
