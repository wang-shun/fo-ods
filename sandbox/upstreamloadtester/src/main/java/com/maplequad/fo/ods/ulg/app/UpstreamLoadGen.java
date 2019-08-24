package com.maplequad.fo.ods.ulg.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.maplequad.fo.ods.ulg.services.TradeConverterService;
import com.maplequad.fo.ods.ulg.services.TradeGeneratorService;
import com.maplequad.fo.ods.ulg.services.TradePersistenceService;

/***
 * UpstreamLoadGen
 * This is the main class that wires up all the services and provide a way to use one or all at once.
 *
 * @author Madhav Mindhe
 * @since :   23/07/2017
 */
public class UpstreamLoadGen {

    private static final Logger LOGGER = LoggerFactory.getLogger(UpstreamLoadGen.class);

    //Instance of all Services
    private static TradeGeneratorService genService;
    private static TradeConverterService conService;
    private static TradePersistenceService perService;


    /**
     * This is the main method that wires up all the services together.
     *
     * @param args containing the projectId, instanceId, assetClass, tableName and total trades to be generated
     * @throws Exception
     */
    public static void main(String[] args) throws Exception{
        LOGGER.info("Entered main");
        String projectId = "fo-ods";
        String instanceId = "equitytest";
        String assetClass = "cash-eq";
        String tableName = "trades";
        int tCount = 1000;

        //Reading the arguments and setting up the variables.
        if(args != null && args.length == 5) {
            projectId = args[0];
            instanceId = args[1];
            assetClass = args[2];
            tableName = args[3];
            tCount = Integer.parseInt(args[4]);
        }

        //Generating the instances of the services
        genService = new TradeGeneratorService(assetClass);
        conService = new TradeConverterService(assetClass);
        perService = new TradePersistenceService(projectId, instanceId, tableName);

        //Invoking the services
        perService.serve(conService.serve(genService.serve(tCount)));

        LOGGER.info("Exited main");
    }
}
