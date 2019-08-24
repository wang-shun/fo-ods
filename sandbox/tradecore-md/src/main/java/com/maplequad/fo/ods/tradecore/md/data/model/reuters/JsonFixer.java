package com.maplequad.fo.ods.tradecore.md.data.model.reuters;

/***
 * JsonFixer
 *
 * This class is used to simplify the complex structure of Reuters Market Data Model
 *
 * @author Madhav Mindhe
 * @since :   10/09/2017
 */
public class JsonFixer {

    /**
     * @param marketData
     * @return
     */
    public static final String fix(String marketData) {
        String fixedMarketData = marketData.replace("[{", "{");
        fixedMarketData = fixedMarketData.replace("]}", "");
        fixedMarketData = fixedMarketData.replace("},{", "},");
        fixedMarketData = fixedMarketData + "}}";
        return fixedMarketData;
    }
}
