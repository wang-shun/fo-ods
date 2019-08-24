package foods.ult.pubsub;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import com.maplequad.fo.ods.tradecore.client.service.GsonWrapper;
import com.maplequad.fo.ods.tradecore.client.service.UltGsonObject;
import com.maplequad.fo.ods.tradecore.data.model.trade.Trade;
import com.maplequad.fo.ods.tradecore.data.model.trade.TradeEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;

public class UltWorker implements Callable<Object> {

  private static Logger log = LoggerFactory.getLogger(UltWorker.class);

  private final int delay;
  private MetricRegistry registry;
  private String action;
  private String state;
  private String serialNumber;
  private TradeFactory factory;
  private Publisher publisher;
  private Meter requests;
  private int tradeCount;

  public UltWorker(MetricRegistry registry, String action, String state, String serialNumber,
                   TradeFactory factory, Publisher publisher, int tradeCount, int delay) {
    this.registry = registry;
    this.action = action;
    this.state = state;
    this.serialNumber = serialNumber;
    this.factory = factory;
    this.publisher = publisher;
    requests = registry.meter("requests");
    this.tradeCount = tradeCount;
    this.delay = delay;
  }

  @Override
  public Object call() {

    log.info("Worker is starting");

    try {
      process();
    } catch (Exception e) {
      log.error("Worker terminated with exception", e);
    }
    log.info("Worker is done");
    return null;
  }

  private void process() throws InterruptedException {
    int logCnt = 0;
    int logFreq = 100;

    for (int i = 0; i < tradeCount; i++, logCnt++) {
      Trade trade = createTrade();

      UltGsonObject obj = new UltGsonObject();
      obj.trade = trade;
      obj.batchserialnum = serialNumber;
      obj.begintimestamp = System.currentTimeMillis();
      obj.subnumber = i;
      obj.batchnumOfTrades = tradeCount;
      obj.action = action;

      String payload = GsonWrapper.toJson(obj);

      //LOGGER.info("payload=============: {}", payload);
      sendMessage(payload);
      requests.mark();

      if (logCnt == logFreq) {
          log.info("Published {}/{} {} trades, serial = {}", i, tradeCount, trade.getProductType(), serialNumber);
          logCnt = 0;
      }

      if (delay > 0) {
        Thread.sleep(delay);
      }
    }
  }

  private void sendMessage(String payload) {
    if (publisher != null) {
      PubsubMessage pubsubMessage = PubsubMessage.newBuilder().setData(ByteString.copyFromUtf8(payload)).build();
      publisher.publish(pubsubMessage);
    }
  }

  private Trade createTrade() {
    Trade trade = factory.generate();
    TradeEvent et = trade.getTradeEventList().get(0);
    et.setEventStatus(state);
    return trade;
  }

}
