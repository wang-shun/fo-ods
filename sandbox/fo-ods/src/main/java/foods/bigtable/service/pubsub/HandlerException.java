package foods.bigtable.service.pubsub;

/**
 * Created by roman on 20/07/2017.
 */
public class HandlerException extends RuntimeException {

    public HandlerException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
