package foods.bigtable.repository;

/**
 * Created by roman on 20/07/2017.
 */
public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(String msg) {
        super(msg);
    }
}
