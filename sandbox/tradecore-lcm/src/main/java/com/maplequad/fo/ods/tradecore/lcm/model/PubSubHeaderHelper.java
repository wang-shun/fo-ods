package com.maplequad.fo.ods.tradecore.lcm.model;

import com.google.pubsub.v1.PubsubMessage;
import com.maplequad.fo.ods.tradecore.data.model.trade.Trade;
import com.maplequad.fo.ods.tradecore.lcm.processor.LCTrackLog;

public class PubSubHeaderHelper {
    static String datakey;
    static String datakeyname="tradeid";
    static String datatype="trade";
    static String datasource="fo-ods";
    static String dataversion;
    static int genesisstart=0;
    static boolean last=true;
    static String runid="fo-ods";
    static long seqnum=1;
    static String uuid;
    static long timestamp;

    private PubSubHeaderHelper(){}

    public static PubsubMessage.Builder fillPubSubBuilder(PubsubMessage.Builder builder, Trade tt){
        long validtimefrom= LCTrackLog.convertTimeStamp(tt.getTradeEventList().get(0).getValidTimeFrom());
        builder.putAttributes("datakey",tt.getTradeId());
        builder.putAttributes("datakeyname",datakeyname);
        builder.putAttributes("datatype",datatype);
        builder.putAttributes("datasource",datasource);
        builder.putAttributes("dataversion",String.valueOf(validtimefrom));
        builder.putAttributes("genesisstart",String.valueOf(genesisstart));
        builder.putAttributes("last",String.valueOf(last));
        builder.putAttributes("runid",runid);
        builder.putAttributes("seqnum",String.valueOf(seqnum));
        builder.putAttributes("uuid",tt.getTradeId());
        builder.putAttributes("timestamp", String.valueOf(validtimefrom) );
        return builder;
    }
}
