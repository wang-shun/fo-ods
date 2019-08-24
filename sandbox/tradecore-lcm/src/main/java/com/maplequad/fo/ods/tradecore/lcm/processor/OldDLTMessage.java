package com.maplequad.fo.ods.tradecore.lcm.processor;

import com.google.gson.JsonSyntaxException;
import com.maplequad.fo.ods.tradecore.client.service.GsonWrapper;
import com.maplequad.fo.ods.tradecore.client.service.OldGsonWrapper;
import com.maplequad.fo.ods.tradecore.data.model.trade.Trade;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class OldDLTMessage {
    public com.maplequad.fo.ods.tradecore.lcm.processor.LCTrackLog log;
    public Trade trade;

    private final static OldGsonWrapper gw = new OldGsonWrapper();
    private final static JSONParser parser = new JSONParser();

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("DLTMessage{");
        sb.append("log=").append(log);
        sb.append(", trade=").append(trade);
        sb.append('}');
        return sb.toString();
    }

    final public static DLTMessage fromJson(String json)throws JsonSyntaxException {
        DLTMessage dlt=gw.fromJson(json,DLTMessage.class);

        try {
            JSONObject jobj = (JSONObject) parser.parse(json);
            JSONObject msg = (JSONObject) jobj.get("trade");
            if (msg == null) {
                return dlt;
            }
            String strTrade = msg.toJSONString();
            Trade realTrade = gw.fromJson(strTrade, Trade.class);
            dlt.trade=realTrade;
        }catch(ParseException p){
            p.printStackTrace();
            throw new JsonSyntaxException(p.getMessage());
        }
        return dlt;
    }


}
