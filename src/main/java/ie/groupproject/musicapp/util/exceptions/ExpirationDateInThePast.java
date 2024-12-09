package ie.groupproject.musicapp.util.exceptions;

public class ExpirationDateInThePast extends RuntimeException {
    public ExpirationDateInThePast(String message) {
        super(message);
    }
}
