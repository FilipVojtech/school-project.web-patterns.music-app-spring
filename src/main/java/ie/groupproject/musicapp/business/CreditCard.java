package ie.groupproject.musicapp.business;

import ie.groupproject.musicapp.business.exceptions.InvalidCardNumberException;
import ie.groupproject.musicapp.business.exceptions.UnsupportedCardIssuerException;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDate;

/**
 * @author Filip Vojtěch
 */
@Getter
@Setter
public class CreditCard {
    private enum CardIssuer {
        MasterCard("Master Card"),
        Visa("Visa"),
        /*AmericanExpress("American Express")*/;

        private final String issuerName;

        CardIssuer(String issuerName) {
            this.issuerName = issuerName;
        }

        public String toString() {
            return issuerName;
        }

        public static CardIssuer fromNumber(BigInteger cardNumber) throws UnsupportedCardIssuerException {
            var cardNumberString = cardNumber.toString();

            // Visa starts with 4
            // Master Card starts with 2 or 5
            // AmEx starts with 3
            if (cardNumberString.startsWith("4")) {
                return Visa;
            } else if (cardNumberString.startsWith("2") || cardNumberString.startsWith("5")) {
                return MasterCard;
            } /*else if (cardNumberString.startsWith("3")) {
                return AmericanExpress;
            }*/

            throw new UnsupportedCardIssuerException("The card issuer is not supported at the moment");
        }
    }

    private @NonNull BigInteger cardNumber;
    private @NonNull LocalDate expiration;
    private @NonNull String cvv;
    private @NonNull CardIssuer issuer;
    private @NonNull String nameOnCard;

    public CreditCard(@NonNull BigInteger cardNumber, @NonNull LocalDate expiration, @NonNull String cvv, @NonNull String nameOnCard) throws UnsupportedCardIssuerException, InvalidCardNumberException {
        validateCardNumber(cardNumber);

        this.cardNumber = cardNumber;
        this.expiration = expiration;
        this.cvv = cvv;
        this.issuer = CardIssuer.fromNumber(cardNumber);
        this.nameOnCard = nameOnCard;
    }

    public void setExpiration(@NonNull LocalDate expiration) {
        if (expiration.isBefore(LocalDate.now())) {
            return;
        }

        this.expiration = expiration;
    }

    public boolean isExpired() {
        return expiration.isBefore(LocalDate.now());
    }

    private boolean validateCardNumber(BigInteger cardNumber) throws InvalidCardNumberException {
        var numberString = cardNumber.toString();

        if (numberString.length() != 16) {
            throw new InvalidCardNumberException("Card number length is too short");
        }

        {
            var checksum = 0;    // running checksum total
            var j = 1;           // takes value of 1 or 2

            // Process each digit one by one starting at the right
            int calc;
            for (var i = numberString.length() - 1; i >= 0; i--) {
                // Extract the next digit and multiply by 1 or 2 on alternative digits.
                calc = Integer.parseInt(String.valueOf(numberString.charAt(i))) * j;

                // If the result is in two digits add 1 to the checksum total
                if (calc > 9) {
                    checksum++;
                    calc -= 10;
                }

                // Add the units element to the checksum total
                checksum += calc;

                // Switch the value of j
                if (j == 1) {
                    j = 2;
                } else {
                    j = 1;
                }
            }

            if (checksum % 10 != 0) {
                throw new InvalidCardNumberException("Invalid card number");
            }
        }
        return true;
    }
}
