package com.maplequad.fo.ods.tradecore.lcm.model;

import java.util.List;

public class Table {


    /***
     * TableName.
     */
    private String tableName;


    private List<TradeAttribute> attributes;

    /**
     * @return name of the table
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * @param tableName
     */
    public void setTableName(String tableName) {
        this.tableName = tableName;
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
