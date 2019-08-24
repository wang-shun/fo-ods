package foods.bigtable.service.pubsub;

/**
 * Created by roman on 20/07/2017.
 */
public class HandlerNotFoundException extends RuntimeException {

    public HandlerNotFoundException(String msg) {
        super(msg);
    }
}
