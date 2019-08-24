package foods.bigtable.service.pubsub;

import foods.bigtable.model.EquityTrade;

public class Request {

    private RequestType type;
    private EquityTrade trade;

    public Request() {
    }

    public RequestType getType() {
        return type;
    }

    public void setType(RequestType type) {
        this.type = type;
    }

    public EquityTrade getTrade() {
        return trade;
    }

    public void setTrade(EquityTrade trade) {
        this.trade = trade;
    }
}
