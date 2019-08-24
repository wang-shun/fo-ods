package com.maplequad.fo.ods.tradecore.md.data.model.reuters;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/***
 * MarketData
 *
 * This is part of the Reuters Market Data Model
 *
 * @author Madhav Mindhe
 * @since :   10/09/2017
 */
public class MarketData {

    @SerializedName("RIC")
    @Expose
    private String ric;
    @SerializedName("TOPIC")
    @Expose
    private String topic;
    @SerializedName("TIMESTAMP")
    @Expose
    private String timestamp;
    @SerializedName("Fields")
    @Expose
    private Fields fields;

    public String getRic() {
        if (ric != null) {
            ric = ric.replace("=", "");
            //This is needed as firebase doesn't like dots in the database refs
            ric = ric.replace(".", "_");
        }
        return ric;
    }

    public void setRic(String ric) {
        this.ric = ric;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Fields getFields() {
        return fields;
    }

    public void setFields(Fields fields) {
        this.fields = fields;
    }

/*    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MarketData)) return false;
        MarketData that = (MarketData) o;
        return com.google.common.base.Objects.equal(getRic(), that.getRic());
    }

    @Override
    public int hashCode() {
        return com.google.common.base.Objects.hashCode(getRic());
    }*/

    @Override
    public String toString() {
        return "MarketData{" +
                "ric='" + getRic() + '\'' +
                ", topic='" + topic + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", fields=" + fields +
                '}';
    }
}
