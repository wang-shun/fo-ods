package com.maplequad.fo.ods.tradecore.md.data.model.reuters.attribute;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Attribute {
    @SerializedName("Value")
    @Expose
    private String value;
    @SerializedName("Type")
    @Expose
    private String type;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return " {" +
                "value='" + value + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
