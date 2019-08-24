package com.maplequad.fo.ods.tradecore.dao;

import com.google.cloud.Timestamp;
import com.google.cloud.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/***
 * Column -
 *
 * This class contain the handle of an attribute that trade details are comprised of.
 *
 * @author Madhav Mindhe
 * @since :   09/08/2017
 */
public class Column {

    private String name;
    private String type;
    private int intValue;
    private long longValue;
    private float floatValue;
    private boolean booleanValue;
    private String strValue;
    private Date date;
    private Timestamp timestamp;
    private static final Logger LOGGER = LoggerFactory.getLogger(Column.class);
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
    @Override
    public String toString(){
        StringBuilder builder = new StringBuilder();
        builder.append("[").append(name).append("=");
        //builder.append("type=[").append(type).append("]");
        if(longValue != 0)
        builder.append(longValue).append("]");
        if(floatValue != 0)
        builder.append(floatValue).append("]");
        if(strValue != null)
        builder.append(strValue).append("]");
        if(date != null)
        builder.append(date).append("]");
        if(timestamp != null)
        builder.append(timestamp).append("]");
        return builder.toString();
    }

    /**
     * Constructors
     * */
    public Column(String name, String datatype, String value){
        this.name = name;
        this.type = datatype;
        if(value != null) {
            switch (datatype) {
                case "int":
                    this.setIntValue(Integer.parseInt(value));
                    break;
                case "long":
                    this.setLongValue(Long.parseLong(value));
                    break;
                case "float":
                    this.setFloatValue(Float.parseFloat(value));
                    break;
                case "string":
                    this.setStrValue(value);
                    break;
                case "boolean":
                    this.setBooleanValue("true".equalsIgnoreCase(value));
                    break;
                case "date":
                    this.setDate(Date.parseDate(value));
                    break;
                case "timestamp":
                    try{
                        if(value.contains("-")) {
                            this.setTimestamp(Timestamp.of(SIMPLE_DATE_FORMAT.parse(value.replace("Z", ""))));
                        }
                        else {
                            this.setTimestamp(Timestamp.ofTimeMicroseconds(Long.parseLong(value)));
                        }
                    }catch(ParseException pe){
                        LOGGER.error("ERROR : Exception while parsing timestamp {}",value);
                    }
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported column type: " + datatype);
            }
        }
    }

    public Column(String name, String value){
        this.name = name;
        this.strValue = value;
    }

    public Column(String name, long value){
        this.name = name;
        this.longValue = value;
    }

    public Column(String name, float value){
        this.name = name;
        this.floatValue = value;
    }

    public Column(String name, Date value){
        this.name = name;
        this.date = value;
    }

    public Column(String name, Timestamp value){
        this.name = name;
        this.timestamp = value;
    }

    /**
     * Accessors
     * */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getIntValue() {
        return intValue;
    }

    public void setIntValue(int intValue) {
        this.intValue = intValue;
    }

    public long getLongValue() {
        return longValue;
    }

    public void setLongValue(long longValue) {
        this.longValue = longValue;
    }

    public float getFloatValue() {
        return floatValue;
    }

    public void setFloatValue(float floatValue) {
        this.floatValue = floatValue;
    }

    public boolean isBooleanValue() {
        return booleanValue;
    }

    public void setBooleanValue(boolean booleanValue) {
        this.booleanValue = booleanValue;
    }

    public String getStrValue() {
        return strValue;
    }

    public void setStrValue(String strValue) {
        this.strValue = strValue;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
