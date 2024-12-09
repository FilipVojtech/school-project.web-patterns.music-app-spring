package ie.groupproject.musicapp.business.exceptions;

/**
 * @author Filip Vojtěch
 */
public class UnsupportedCardIssuerException extends RuntimeException {
    public UnsupportedCardIssuerException(String message) {
        super(message);
    }
}
