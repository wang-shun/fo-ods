package com.maplequad.fo.ods.ulg.model;

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
    private List<TradeAttribute> attributes;

    /**
     * @return outputType
     */
    public String getOutputType() {
        return outputType;
    }

    /**
     * Sets the outputType to given value
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
     * @param outputDest
     */
    public void setOutputDest(String outputDest) {
        this.outputDest = outputDest;
    }

    /**
     * @return list of attributes
     */
    public List<TradeAttribute> getAttributes() {
        return attributes;
    }

    /**
     * @param attributes
     */
    public void setAttributes(List<TradeAttribute> attributes) {
        this.attributes = attributes;
    }
}
