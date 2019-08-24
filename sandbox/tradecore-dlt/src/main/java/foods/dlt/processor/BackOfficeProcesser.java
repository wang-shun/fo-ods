package foods.dlt.processor;

import com.foods.statediagram.Exception.IncorrectTransitionforState;
import com.foods.statediagram.Exception.NoTradeEventFoundException;
import com.foods.statediagram.Exception.NoTransitionFoundException;
import com.foods.statediagram.STATE;
import com.foods.statediagram.StateTransitionGraphFactory;
import com.foods.statediagram.StateTransitionGraphInterface;
import com.maplequad.fo.ods.tradecore.data.model.trade.TradeEvent;
import com.maplequad.fo.ods.tradecore.lcm.processor.LCTrackLog;
import foods.dlt.pubsub.PubMessageHelper;
import com.maplequad.fo.ods.tradecore.data.model.trade.Trade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class BackOfficeProcesser {
    private static final Logger LOGGER = LoggerFactory.getLogger(BackOfficeProcesser.class);
    private PubMessageHelper mainPublisher;
    private PubMessageHelper failPublisher;
    private List actionApprovalScope;
    private Map approvalMap;
    private Map rejectMap;

    double successRate;
    Random rand;
    StateTransitionGraphFactory transitionFactory;
    public BackOfficeProcesser(){
        rand=new Random(System.currentTimeMillis());
    }
    public Trade processTrade(Trade t,LCTrackLog req) throws NoTransitionFoundException,NoTradeEventFoundException,IncorrectTransitionforState,com.foods.statediagram.Exception.TradeValidationException{
        TradeEvent et = t.getTradeEventList().get(0);
        Trade newTrade=null;
        String lastAction=et.getEventType();

        //Throw a dice
        String nextAction;
        Object nextActionObj;
        boolean isApproved=this.approveOrNot();
        if(isApproved){
            nextActionObj=this.approvalMap.get(lastAction);
        }else{
            nextActionObj=this.rejectMap.get(lastAction);
        }
        if(nextActionObj==null){
            return t;
        }

        StateTransitionGraphInterface graphInterface = transitionFactory.getMyTransitionDiagram(t.getProductType());
        nextAction = nextActionObj.toString();
        newTrade = graphInterface.transitionState(t, t.getProductType(), nextAction);
        if(isApproved){
            //Push to approve queue

            String newSerialNum= this.mainPublisher.publishMessage(newTrade,nextAction,req.serialNumber);

            LOGGER.info("Success process: Tradeid:{},action:{},ostradeid:{} serialNumber {}",newTrade.getTradeId(),nextAction,t.getOsTradeId(),newSerialNum);
        }else{
            //Push to reject queue

            newTrade.getTradeEventList().get(0).setEventRemarks("Reject by backoffice");

            //Update reject status to mainstream
            String newSerialNum= this.mainPublisher.publishMessage(newTrade,nextAction,req.serialNumber);
            newSerialNum=this.failPublisher.publishMessage(newTrade,nextAction,req.serialNumber);
            LOGGER.info("Reject process: TradeId{},action:{},ostradeid:{} serialNumber {}",newTrade.getTradeId(),nextAction,t.getOsTradeId(),newSerialNum);
        }

        return newTrade;
    }

    private boolean approveOrNot(){
        double r = this.rand.nextDouble();
        if(r>this.successRate){
            return false;
        }else{
            return true;
        }

    }

    public void publishSystemFailure(Trade t, String errorMsg,String oldSerialNumber){
        TradeEvent et = t.getTradeEventList().get(0);
        Trade newTrade=null;
        String lastAction=et.getEventType();

        et.setEventRemarks(errorMsg);
        this.failPublisher.publishMessage(t,lastAction,oldSerialNumber);
    }

    public PubMessageHelper getMainPublisher() {
        return mainPublisher;
    }

    public void setMainPublisher(PubMessageHelper mainPublisher) {
        this.mainPublisher = mainPublisher;
    }

    public PubMessageHelper getFailPublisher() {
        return failPublisher;
    }

    public void setFailPublisher(PubMessageHelper failPublisher) {
        this.failPublisher = failPublisher;
    }

    public double getSuccessRate() {
        return successRate;
    }

    public void setSuccessRate(String StrsuccessRate) {
        double successRate = Double.parseDouble(StrsuccessRate);
        this.setSuccessRate(successRate);
    }

    public void setSuccessRate(double successRate) {
        if(successRate<0.0)
            successRate=0;
        if(successRate>1)
            successRate=1;
        this.successRate = successRate;
    }

    public List getActionApprovalScope() {
        return actionApprovalScope;
    }

    public void setActionApprovalScope(List actionApprovalScope) {
        this.actionApprovalScope = actionApprovalScope;
    }

    public Map getApprovalMap() {
        return approvalMap;
    }

    public void setApprovalMap(Map approvalMap) {
        this.approvalMap = approvalMap;
    }

    public Map getRejectMap() {
        return rejectMap;
    }

    public void setRejectMap(Map rejectMap) {
        this.rejectMap = rejectMap;
    }

    public StateTransitionGraphFactory getTransitionFactory() {
        return transitionFactory;
    }

    public void setTransitionFactory(StateTransitionGraphFactory transitionFactory) {
        this.transitionFactory = transitionFactory;
    }
}
