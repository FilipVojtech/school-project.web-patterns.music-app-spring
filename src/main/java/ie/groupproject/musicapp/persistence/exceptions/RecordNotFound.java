package ie.groupproject.musicapp.persistence.exceptions;

/**
 * @author Filip Vojtěch
 */
public class RecordNotFound extends RuntimeException {
    public RecordNotFound(String message) {
        super(message);
    }
}
