
import foods.bigtable.service.grpc.TradeRepositoryService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by roman on 20/07/2017.
 */
public class TradeRepositoryMain {

  static {
    System.out.println("TradeRepositoryMain::static");
  }

    private static Logger log = LoggerFactory.getLogger(TradeRepositoryMain.class);


    public static void main(String[] args) throws Exception {
      System.out.println("TradeRepositoryMain::main");
      log.info("Hello from TradeRepositoryMain");
      log.info("args: " + Arrays.asList(args));
      log.info("classpath: ");
      for (String str: System.getProperty("java.class.path").split(File.pathSeparator)) {
        log.info(str);
      }

      log.info("system properties: {}", System.getProperties());
      log.info("env: {}", System.getenv());

      String projectId = System.getenv("PROJECT_ID");
      String bigtableInstanceId = System.getenv("BIGTABLE_INSTANCE_ID");
      String grpcPort = System.getenv("SERVICE_GRPC_PORT");

      log.info("project={}, bigtable instanceId={}, grpc port={}", projectId, bigtableInstanceId, grpcPort);

      TradeRepositoryService service = new TradeRepositoryService(projectId, bigtableInstanceId);
      int port = Integer.valueOf(grpcPort);

      Executor exec = Executors.newSingleThreadExecutor();
      Server server = ServerBuilder.forPort(port).addService(service).executor(exec).build();
      server.start();

      log.info("service started - v2");

      Object sync = new Object();
      while (true) {
          synchronized (sync) {
              sync.wait();
          }
      }

    }
}
