package com.maplequad.fo.ods.tradecore.data.xform;

import com.google.cloud.Timestamp;
import com.google.common.base.Strings;
import com.maplequad.fo.ods.tradecore.data.model.trade.ITradeLeg;
import com.maplequad.fo.ods.tradecore.data.model.trade.eq.Equity;
import com.maplequad.fo.ods.tradecore.proto.model.eq.EquityOuterClass;

/***
 * EquityTradeTransformer -
 *
 * This class is used to convert the trade from proto to std and vice versa.
 *
 * @author Madhav Mindhe
 * @since :   31/08/2017
 */
public class EquityTradeTransformer {

    //Added Private Constructor to hide the implicit public one
    private EquityTradeTransformer() {

    }

    /**
     * This method is used to generate Proto instance of the Equity
     *
     * @param iTradeLeg
     * @return Proto instance of the Equity
     */
    public static EquityOuterClass.Equity toProto(ITradeLeg iTradeLeg) {

        Equity tradeLeg = (Equity) iTradeLeg;
        EquityOuterClass.Equity.Builder pTradeLegBuilder = EquityOuterClass.Equity.newBuilder();

        //Primary Key
        if (!Strings.isNullOrEmpty(tradeLeg.getTradeId())) {
            pTradeLegBuilder.setTradeId(tradeLeg.getTradeId());
        }
        if (tradeLeg.getLegNumber() != 0) {
            pTradeLegBuilder.setLegNumber(tradeLeg.getLegNumber());
        }

        //Bi-Temporal Support
        if (tradeLeg.getValidTimeFrom() != null &&
                !Timestamp.MIN_VALUE.equals(tradeLeg.getValidTimeFrom())) {
            pTradeLegBuilder.setValidTimeFrom(tradeLeg.getValidTimeFrom().toProto());
        }
        if (tradeLeg.getValidTimeTo() != null &&
                !Timestamp.MIN_VALUE.equals(tradeLeg.getValidTimeTo())) {
            pTradeLegBuilder.setValidTimeTo(tradeLeg.getValidTimeTo().toProto());
        }
        if (tradeLeg.getTransactionTimeFrom() != null &&
                !Timestamp.MIN_VALUE.equals(tradeLeg.getTransactionTimeFrom())) {
            pTradeLegBuilder.setTransactionTimeFrom(tradeLeg.getTransactionTimeFrom().toProto());
        }
        if (tradeLeg.getTransactionTimeTo() != null &&
                !Timestamp.MIN_VALUE.equals(tradeLeg.getTransactionTimeTo())) {
            pTradeLegBuilder.setTransactionTimeTo(tradeLeg.getTransactionTimeTo().toProto());
        }

        //Common Leg Attributes
        if (!Strings.isNullOrEmpty(tradeLeg.getLegType())) {
            pTradeLegBuilder.setLegType(tradeLeg.getLegType());
        }
        if (!Strings.isNullOrEmpty(tradeLeg.getBook())) {
            pTradeLegBuilder.setBook(tradeLeg.getBook());
        }
        if (!Strings.isNullOrEmpty(tradeLeg.getInternalProductRef())) {
            pTradeLegBuilder.setInternalProductRef(tradeLeg.getInternalProductRef());
        }
        if (!Strings.isNullOrEmpty(tradeLeg.getInternalProductType())) {
            pTradeLegBuilder.setInternalProductType(tradeLeg.getInternalProductType());
        }
        if (!Strings.isNullOrEmpty(tradeLeg.getInstrumentId())) {
            pTradeLegBuilder.setInstrumentId(tradeLeg.getInstrumentId());
        }
        if (!Strings.isNullOrEmpty(tradeLeg.getRic())) {
            pTradeLegBuilder.setRic(tradeLeg.getRic());
        }
        if (!Strings.isNullOrEmpty(tradeLeg.getIsin())) {
            pTradeLegBuilder.setIsin(tradeLeg.getIsin());
        }
        if (!Strings.isNullOrEmpty(tradeLeg.getCurrency())) {
            pTradeLegBuilder.setCurrency(tradeLeg.getCurrency());
        }
        if (!Strings.isNullOrEmpty(tradeLeg.getExchange())) {
            pTradeLegBuilder.setExchange(tradeLeg.getExchange());
        }
        if (!Strings.isNullOrEmpty(tradeLeg.getBuySellInd())) {
            pTradeLegBuilder.setBuySellInd(tradeLeg.getBuySellInd());
        }

        //Equity Specific Attributes
        if (!Strings.isNullOrEmpty(tradeLeg.getCfiCode())) {
            pTradeLegBuilder.setCfiCode(tradeLeg.getCfiCode());
        }
        if (tradeLeg.getQuantity() != 0) {
            pTradeLegBuilder.setQuantity(tradeLeg.getQuantity());
        }
        if (tradeLeg.getPrice() > 0.0) {
            pTradeLegBuilder.setPrice(tradeLeg.getPrice());
        }
        if (tradeLeg.getGrossPrice() > 0.0) {
            pTradeLegBuilder.setGrossPrice(tradeLeg.getGrossPrice());
        }


        return pTradeLegBuilder.build();
    }

    /**
     * This method is used to create an instance of TradeLeg from its Proto Form
     *
     * @param iTradeLeg
     * @return instance of TradeLeg with all details from its Proto form.
     */
    public static ITradeLeg fromProto(EquityOuterClass.Equity iTradeLeg) {

        Equity tradeLeg = new Equity();
        //Primary Key
        tradeLeg.setTradeId(iTradeLeg.getTradeId());
        tradeLeg.setLegNumber((int) iTradeLeg.getLegNumber());

        //Bi-Temporal Support
        tradeLeg.setValidTimeFrom(Timestamp.fromProto(iTradeLeg.getValidTimeFrom()));
        tradeLeg.setValidTimeTo(Timestamp.fromProto(iTradeLeg.getValidTimeTo()));
        tradeLeg.setTransactionTimeFrom(Timestamp.fromProto(iTradeLeg.getTransactionTimeFrom()));
        tradeLeg.setTransactionTimeTo(Timestamp.fromProto(iTradeLeg.getTransactionTimeTo()));

        //Common Leg Attributes
        tradeLeg.setLegType(iTradeLeg.getLegType());
        tradeLeg.setBook(iTradeLeg.getBook());
        tradeLeg.setInternalProductType(iTradeLeg.getInternalProductType());
        tradeLeg.setInternalProductRef(iTradeLeg.getInternalProductRef());
        tradeLeg.setInstrumentId(iTradeLeg.getInstrumentId());
        tradeLeg.setRic(iTradeLeg.getRic());
        tradeLeg.setIsin(iTradeLeg.getIsin());
        tradeLeg.setCurrency(iTradeLeg.getCurrency());
        tradeLeg.setExchange(iTradeLeg.getExchange());
        tradeLeg.setBuySellInd(iTradeLeg.getBuySellInd());

        //Equities Specific Attributes
        tradeLeg.setCfiCode(iTradeLeg.getCfiCode());
        tradeLeg.setQuantity((int) iTradeLeg.getQuantity());
        tradeLeg.setPrice(iTradeLeg.getPrice());
        tradeLeg.setGrossPrice(iTradeLeg.getGrossPrice());
        return tradeLeg;
    }


}
