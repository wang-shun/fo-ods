package com.maplequad.fo.ods.ulg.web.servlet;

import com.maplequad.fo.ods.ulg.services.TradeConverterService;
import com.maplequad.fo.ods.ulg.services.TradeGeneratorService;
import com.maplequad.fo.ods.ulg.services.TradePersistenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

    // [START example]
    @SuppressWarnings("serial")
    @WebServlet(name = "booktrades", value = "/" )
    public class TradeBookingServlet extends HttpServlet {

        private static final Logger LOGGER = LoggerFactory.getLogger(TradeBookingServlet.class);

        //Instance of all Services
        private static TradeGeneratorService genService;
        private static TradeConverterService conService;
        private static TradePersistenceService perService;

        @Override
        public void doGet(HttpServletRequest req, HttpServletResponse resp) {
            LOGGER.trace("Entered doGet");

            String projectId = "fo-ods";
            String instanceId = "equitytest";
            String tableName = "fo-ods-trades";
            try {
                String strTcount = req.getParameter("tCount");
                if (strTcount != null) {
                    int tCount = Integer.parseInt(strTcount);
                    int batchSize = Integer.parseInt(req.getParameter("batchSize"));
                    String assetClass = req.getParameter("assetClass");
                    //String projectId = req.getParameter("projectId");
                    //String instanceId = req.getParameter("instanceId");
                    //String tableName = req.getParameter("tableName");

                    //Generating the instances of the services
                    genService = new TradeGeneratorService(assetClass,"CREATE");
                    conService = new TradeConverterService(assetClass);
                    perService = new TradePersistenceService(tableName, batchSize);

                    //Invoking the services
                    perService.serve(conService.serve(genService.serve(tCount)));

                    PrintWriter out = resp.getWriter();
                    out.println(tCount + " trades booked for assetClass " + assetClass);
                }
            }catch(Exception exception){
                LOGGER.error("Exception Occured - {}", exception.getMessage());
                //TODO - Remove printstacktrace once this is stable
                exception.printStackTrace();
            }
            LOGGER.trace("Exited doGet");
        }
    }
// [END example]
