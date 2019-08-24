package com.maplequad.fo.ods.ulg.model;

/***
 * TradeAttribute -
 *
 * This class contains all the different attributes that trade details are comprised of.
 *
 * @author Madhav Mindhe
 * @since :   23/07/2017
 */
public class TradeAttribute {

    /***
     * For examples and values please see resources
     */
    private String name;
    private String type;
    private String value;
    private String values;
    private String format;
    private String formula;
    private String minRange;
    private String maxRange;

    private String family;
    private String qualifier;

    /**
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return
     */
    public String getType() {
        return type;
    }

    /**
     * @param type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * @return
     */
    public String getValues() {
        return values;
    }

    /**
     * @param values
     */
    public void setValues(String values) {
        this.values = values;
    }

    /**
     * @return
     */
    public String getFormat() {
        return format;
    }

    /**
     * @param format
     */
    public void setFormat(String format) {
        this.format = format;
    }

    /**
     * @return
     */
    public String getFormula() {
        return formula;
    }

    /**
     * @param formula
     */
    public void setFormula(String formula) {
        this.formula = formula;
    }

    /**
     * @return
     */
    public String getMinRange() {
        return minRange;
    }

    /**
     * @param minRange
     */
    public void setMinRange(String minRange) {
        this.minRange = minRange;
    }

    /**
     * @return
     */
    public String getMaxRange() {
        return maxRange;
    }

    /**
     * @param maxRange
     */
    public void setMaxRange(String maxRange) {
        this.maxRange = maxRange;
    }

    /**
     * @return
     */
    public String getFamily() {
        return family;
    }

    /**
     * @param family
     */
    public void setFamily(String family) {
        this.family = family;
    }

    /**
     * @return
     */
    public String getQualifier() {
        return qualifier;
    }

    /**
     * @param qualifier
     */
    public void setQualifier(String qualifier) {
        this.qualifier = qualifier;
    }

}
