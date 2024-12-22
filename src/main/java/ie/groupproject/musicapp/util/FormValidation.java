package ie.groupproject.musicapp.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FormValidation {
    /**
     * Check if value is an email
     *
     * @param value The value to be checked
     * @return Boolean
     */
    public static boolean isEmail(String value) {
        Pattern pattern = Pattern.compile("[.\\S]+@[.\\S]+\\.[\\w]{2,3}");
        Matcher matcher = pattern.matcher(value);
        return matcher.find();
    }

    /**
     * Check if value contains the specified amount of special characters.<br/>
     * Useful for password strength checks.
     *
     * @param value         The value to be checked.
     * @param numberOfChars Number of special characters required.
     * @return Boolean
     */
    public static boolean hasSpecialChars(String value, int numberOfChars) {
        return Pattern
                .compile("\\W")
                .matcher(value)
                .results()
                .count() >= numberOfChars;

    }

    /**
     * Check if value contains the specified amount of digits.<br/>
     * Useful for password strength checks.
     *
     * @param value          The value to be checked.
     * @param numberOfDigits Number of digits required.
     * @return Boolean
     */
    public static boolean hasDigits(String value, int numberOfDigits) {
        return Pattern
                .compile("\\d")
                .matcher(value)
                .results()
                .count() >= numberOfDigits;
    }

    /**
     * Check if value contains the specified amount of uppercase letters.<br/>
     * Useful for password strength checks.
     *
     * @param value             The value to be checked.
     * @param numberOfUppercase Number of uppercase letters required.
     * @return Boolean
     */
    public static boolean hasUppercase(String value, int numberOfUppercase) {
        return Pattern
                .compile("[A-Z]")
                .matcher(value)
                .results()
                .count() >= numberOfUppercase;

    }
}
