package com.maplequad.fo.ods.tradecore.data.xform;

import com.google.cloud.Timestamp;
import com.google.common.base.Strings;
import com.maplequad.fo.ods.tradecore.data.model.trade.ITradeLeg;
import com.maplequad.fo.ods.tradecore.data.model.trade.fi.Ird;
import com.maplequad.fo.ods.tradecore.proto.model.fi.IrdOuterClass;

import java.util.Date;

/***
 * IrdTradeTransformer -
 *
 * This class is used to convert the trade from proto to std and vice versa.
 *
 * @author Madhav Mindhe
 * @since :   31/08/2017
 */
public class IrdTradeTransformer {

    //Added Private Constructor to hide the implicit public one
    private IrdTradeTransformer() {

    }

    /**
     * This method is used to generate Proto instance of the Ird
     *
     * @param iTradeLeg
     * @return Proto instance of the Ird
     */
    public static IrdOuterClass.Ird toProto(ITradeLeg iTradeLeg) {

        Ird tradeLeg = (Ird) iTradeLeg;

        IrdOuterClass.Ird.Builder pTradeLegBuilder = IrdOuterClass.Ird.newBuilder();

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

        //IR_SWAP Specific Attributes
        if (!Strings.isNullOrEmpty(tradeLeg.getIrdLegType())) {
            pTradeLegBuilder.setIrdLegType(tradeLeg.getIrdLegType());
        }
        if (tradeLeg.getMaturityDate() != null) {
            pTradeLegBuilder.setMaturityDate(tradeLeg.getMaturityDate().getTime());
        }
        if (tradeLeg.getNotional() > 0.0) {
            pTradeLegBuilder.setNotional(tradeLeg.getNotional());
        }
        if (!Strings.isNullOrEmpty(tradeLeg.getIndex())) {
            pTradeLegBuilder.setIndex(tradeLeg.getIndex());
        }
        if (!Strings.isNullOrEmpty(tradeLeg.getNotionalExp())) {
            pTradeLegBuilder.setNotionalExp(tradeLeg.getNotionalExp());
        }
        if (!Strings.isNullOrEmpty(tradeLeg.getTerm())) {
            pTradeLegBuilder.setTerm(tradeLeg.getTerm());
        }
        if (tradeLeg.getRate() > 0.0) {
            pTradeLegBuilder.setRate(tradeLeg.getRate());
        }
        if (!Strings.isNullOrEmpty(tradeLeg.getBasis())) {
            pTradeLegBuilder.setBasis(tradeLeg.getBasis());
        }
        if (tradeLeg.getSpread() > 0.0) {
            pTradeLegBuilder.setSpread(tradeLeg.getSpread());
        }
        if (!Strings.isNullOrEmpty(tradeLeg.getSettlementCcy())) {
            pTradeLegBuilder.setSettlementCcy(tradeLeg.getSettlementCcy());
        }
        if (tradeLeg.getSettlementAmount() > 0.0) {
            pTradeLegBuilder.setSettlementAmount(tradeLeg.getSettlementAmount());
        }
        if (tradeLeg.getSettlementDate() != null) {
            pTradeLegBuilder.setSettlementDate(tradeLeg.getSettlementDate().getTime());
        }
        return pTradeLegBuilder.build();
    }

    /**
     * This method is used to create an instance of TradeLeg from its Proto Form
     *
     * @param iTradeLeg
     * @return instance of TradeLeg with all details from its Proto form.
     */
    public static ITradeLeg fromProto(IrdOuterClass.Ird iTradeLeg) {

        Ird tradeLeg = new Ird();
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

        //IR_SWAP Specific Attributes
        tradeLeg.setIrdLegType(iTradeLeg.getIrdLegType());
        tradeLeg.setMaturityDate(new Date(iTradeLeg.getMaturityDate()));
        tradeLeg.setNotional(iTradeLeg.getNotional());
        tradeLeg.setIndex(iTradeLeg.getIndex());
        tradeLeg.setNotionalExp(iTradeLeg.getNotionalExp());
        tradeLeg.setTerm(iTradeLeg.getTerm());
        tradeLeg.setRate(iTradeLeg.getRate());
        tradeLeg.setBasis(iTradeLeg.getBasis());
        tradeLeg.setSpread(iTradeLeg.getSpread());
        tradeLeg.setSettlementCcy(iTradeLeg.getSettlementCcy());
        tradeLeg.setSettlementAmount(iTradeLeg.getSettlementAmount());
        tradeLeg.setSettlementDate(new Date(iTradeLeg.getSettlementDate()));

        return tradeLeg;
    }


}
