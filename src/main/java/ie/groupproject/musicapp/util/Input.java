package ie.groupproject.musicapp.util;

import ie.groupproject.musicapp.util.exceptions.ExpirationDateInThePast;

import java.math.BigInteger;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Except for the string methods, all input is taken on the same line.
 *
 * @author Filip Vojtěch
 */
public class Input {
    private static final Scanner sc = new Scanner(System.in);

    /**
     * Handles string before below public methods return
     *
     * @param string String to be handled
     * @return Handled string
     */
    private static String handleStringBeforeReturn(String string) {
        string = string.trim();
        return string;
    }

    /**
     * Prompts the user for a command.
     *
     * @return Command by the user
     */
    public static String command() {
        System.out.print("> ");
        return handleStringBeforeReturn(sc.nextLine());
    }

    /**
     * Prompt the user for a password.
     * For debug purposes only.</br>
     * <strong>DO NOT USE IN PRODUCTION CODE!!!</strong></br>
     * It is not secure.
     *
     * @return Character array of the password
     */
    public static char[] password() {
        System.out.print("Password: ");
        return sc.nextLine().toCharArray();
    }

    /**
     * Prompts the user for string input.
     *
     * @param prompt          Prompt displayed to the user. Input happens on the same line.
     * @param inputOnSameLine True - Input happens on the same line. False - Input happens on the next line
     * @return Input by the user.
     */
    public static String string(String prompt, boolean inputOnSameLine) {
        System.out.print(prompt);
        if (!inputOnSameLine) {
            System.out.print('\n');
        }
        return handleStringBeforeReturn(sc.nextLine());
    }

    /**
     * Prompts the user for string input.
     *
     * @param prompt Prompt displayed to the user. Input happens on the same line.
     * @return Input by the user.
     */
    public static String string(String prompt) {
        return string(prompt, true);
    }

    /**
     * Gets a valid email address from the user
     *
     * @return An email address
     */
    public static String email() {
        Pattern pattern = Pattern.compile("[.\\S]+@[.\\S]+\\.[\\w]{2,3}");

        while (true) {
            System.out.print("Email: ");
            String email = sc.nextLine();
            Matcher matcher = pattern.matcher(email);

            if (matcher.find()) {
                return handleStringBeforeReturn(email);
            } else {
                System.out.println("Please enter an email address.");
            }
        }
    }

    /**
     * Prompt the user for an integer.
     *
     * @param prompt To display to the user.
     * @param min    Minimum value of the number (inclusive).
     * @param max    Maximum value of the number (exclusive).
     * @return A valid integer.
     */
    public static int integer(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            try {
                int input = sc.nextInt();

                if (input < min || input >= max) {
                    System.out.println(MessageFormat.format("Number must be between {0} and {1}", min, max + 1));
                    continue;
                }

                return input;
            } catch (InputMismatchException e) {
                System.out.println("Please enter a number.");
                sc.nextLine();
            }
        }
    }

    /**
     * Prompt the user for an integer.
     *
     * @param prompt To display to the user.
     * @param min    Minimum value of the number (inclusive).
     * @return A valid integer.
     */
    public static int integer(String prompt, int min) {
        return integer(prompt, min, Integer.MAX_VALUE);
    }

    /**
     * Prompt the user for an integer.
     *
     * @param prompt To display to the user.
     * @return A valid integer.
     */
    public static int integer(String prompt) {
        return integer(prompt, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    /**
     * Prompts the user for a card number
     *
     * @param prompt Prompt to show to the user
     * @return 16 digits long card number, not validated
     */
    public static BigInteger cardNumber(String prompt) {
        while (true) {
            String input = string(prompt, false);

            input = input.replace("-", "");
            input = input.replace(" ", "");

            if (input.length() != 16) {
                System.out.println("Incorrect number format [xxxx xxxx xxxx xxxx]");
                continue;
            }

            try {
                return new BigInteger(input);
            } catch (NumberFormatException e) {
                System.out.println("Incorrect number format [xxxx xxxx xxxx xxxx]");
                continue;
            }
        }
    }

    /**
     * Prompts the user for a card expiration date.
     *
     * @param prompt            Prompt to display to the user.
     * @param throwOnDateInPast If set to true, throws {@link ExpirationDateInThePast} exception. If set to false, the previous input is discarded and user is prompted again.
     * @return Valid card expiration date
     * @throws ExpirationDateInThePast If the date set by the user is in the past
     */
    public static LocalDate cardExpirationDate(String prompt, boolean throwOnDateInPast) throws ExpirationDateInThePast {
        while (true) {
            String input = string(prompt + " [mm/yy]", false);
            String[] inputParts = input.split("/");

            if (inputParts.length != 2) {
                System.out.println("Please enter the date in a correct format.");
                continue;
            }

            int month;
            int year;

            try {
                month = Integer.parseInt(inputParts[0]);
                year = Integer.parseInt(inputParts[1]);
            } catch (NumberFormatException e) {
                System.out.println("Please enter the date in a correct format.");
                continue;
            }

            if (month < 1 || month > 12) {
                System.out.println("Please enter a valid month.");
                continue;
            }

            year += 2000;

            var expiration = LocalDate.of(
                    year,
                    month,
                    YearMonth.of(year, month).atEndOfMonth().getDayOfMonth()
            );

            if (expiration.isBefore(LocalDate.now())) {
                if (throwOnDateInPast) {
                    throw new ExpirationDateInThePast("Expiration date in the past.");
                }
                System.out.println("Expiration date in the past.");
                continue;
            }

            return expiration;
        }
    }

    /**
     * Prompts the user for a card expiration date.</br>
     * This method does not handle expiration date in the past.
     * For better handling on expired date use the other <code>cardExpirationDate</code> method.
     *
     * @param prompt Prompt to display to the user.
     * @return Valid card expiration date.
     */
    public static LocalDate cardExpirationDate(String prompt) {
        return cardExpirationDate(prompt, false);
    }

    /**
     * Prompts the user for cvv number
     *
     * @param prompt Prompt to display to the user
     * @return Valid CVV number.
     */
    public static String cvv(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine();
            if (input.length() != 3) {
                System.out.println("CVV invalid format.");
                continue;
            }
            try {
                Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a CVV.");
                continue;
            }
            return handleStringBeforeReturn(input);
        }
    }
}
