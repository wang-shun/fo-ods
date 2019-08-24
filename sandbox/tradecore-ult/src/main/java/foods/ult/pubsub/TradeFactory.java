package foods.ult.pubsub;

import com.jsoniter.JsonIterator;
import com.maplequad.fo.ods.tradecore.client.model.TradeInfo;
import com.maplequad.fo.ods.tradecore.client.service.TradeGenerator;
import com.maplequad.fo.ods.tradecore.data.model.trade.Trade;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class TradeFactory {

    private static final Logger log = LoggerFactory.getLogger(TradeFactory.class);

    private static final String tradeJson = "/${ASSET_CLASS}/${ASSET_CLASS}-trade.json";

    private final TradeGenerator generator;
    private final int numLegs;
    private final int numParties;

    public TradeFactory(String assetClass, int numLegs, int numParties) throws Exception {
        generator = createTradeGenerator(assetClass);
        this.numLegs = numLegs;
        this.numParties = numParties;
    }

    public Trade generate() {
        return generator.createTrade(numLegs, numParties);
    }

    private static TradeGenerator createTradeGenerator(String assetClass) throws Exception {
        TradeInfo tradeInfo = getTradeInfo(assetClass);
        TradeGenerator tradeGenerator = TradeGenerator.getInstance(assetClass,tradeInfo);
        return tradeGenerator;
    }

    private static TradeInfo getTradeInfo(String assetClass) throws IOException {
        String newtradeJson = tradeJson.replace("${ASSET_CLASS}", assetClass.toLowerCase());
        log.info("Loading tradeinfo from {}", newtradeJson);
        InputStream in = TradeFactory.class.getResourceAsStream(newtradeJson);
        if (in == null) {
            throw new RuntimeException("Cannot load trade info from " + newtradeJson);
        }
        String result = IOUtils.toString(in, StandardCharsets.UTF_8);

        TradeInfo tradeInfo = JsonIterator.deserialize(result, TradeInfo.class);
        assert (tradeInfo != null);
        log.info("Initialize tradeinfo successfully");
        return tradeInfo;
    }
}
