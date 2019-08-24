package com.foods.statediagram.validation;

import com.foods.statediagram.Exception.TradeValidationException;
import com.maplequad.fo.ods.tradecore.data.model.trade.Trade;

public interface ValidationInterface {
    public void validate(Trade t) throws TradeValidationException;
}
