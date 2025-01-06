package ie.groupproject.musicapp.controllers;

import ie.groupproject.musicapp.util.CreditCard;
import ie.groupproject.musicapp.business.User;
import ie.groupproject.musicapp.business.exceptions.InvalidCardNumberException;
import ie.groupproject.musicapp.persistence.UserDao;
import ie.groupproject.musicapp.persistence.UserDaoImpl;
import ie.groupproject.musicapp.ui.Form;
import ie.groupproject.musicapp.util.FormValidation;
import jakarta.servlet.http.HttpSession;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Locale;

/**
 * Routes in this controller are all guarded so that if unauthenticated user visits the page, they are redirected to the login page.
 *
 * @author Filip Vojtěch
 */
@Controller
public class UserController {
    private final MessageSource messageSource;
    private final UserDao userDao;

    public UserController(MessageSource messageSource) {
        this.messageSource = messageSource;
        this.userDao = new UserDaoImpl("database.properties");
    }

    /**
     * Redirect to a valid path if only the /me part was supplied
     */
    @GetMapping("/me")
    public String redirectToAccount() {
        return "redirect:/me/account";
    }

    /**
     * Account management page
     */
    @GetMapping("/me/account")
    public String userPage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");

        if (user == null) return "redirect:/login";

        model.addAttribute("page", "/me/account");

        Form form = (Form) model.getAttribute("form");
        if (form == null) {
            form = new Form();
            model.addAttribute("form", form);
            var displayNameField = form.addField("displayName", user.getDisplayName());
            var emailField = form.addField("email", user.getEmail());
            var currentPasswordField = form.addField("password", "");
            var newPasswordField = form.addField("newPassword", "");
            var passwordCheckField = form.addField("passwordCheck", "");
        }

        return "pages/user/account";
    }

    /**
     * Subscription page
     */
    @GetMapping("/me/subscription")
    public String subscriptionPage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");

        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("page", "/me/subscription");
        model.addAttribute("subscriptionEndDate", user.getSubscriptionEnd());

        Form form = (Form) model.getAttribute("form");
        if (form == null) {
            form = new Form();
            model.addAttribute("form", form);
            form.addField("cardName", "");
            form.addField("cardNumber", "");
            form.addField("cardMonth", "");
            form.addField("cardYear", "");
            form.addField("cardSecCode", "");
        }

        return "pages/user/subscription";
    }

    /**
     * Display name form handler.
     * Changes the display name only if it differs from the current one
     */
    @PostMapping("/me/displayName")
    public String updateDisplayName(
            HttpSession session,
            RedirectAttributes redirectAttributes,
            Locale locale,
            @RequestParam(name = "displayName") String displayName
    ) {
        Form form = new Form();
        var displayNameField = form.addField("displayName", displayName);

        User user = (User) session.getAttribute("user");

        if (user == null) {
            return "redirect:/login";
        }

        if (!displayName.equals(user.getDisplayName())) {
            if (displayName.isBlank())
                displayNameField.addError(messageSource.getMessage("form.errors.displayNameMissing", null, locale));
            if (displayName.length() > 60)
                displayNameField.addError(messageSource.getMessage("form.errors.displayNameTooLong", null, locale));

            if (displayNameField.isValid()) user.setDisplayName(displayName);
            else redirectAttributes.addFlashAttribute("form", form);

            if (userDao.updateUser(user)) session.setAttribute("user", user);
        }

        return "redirect:/me";
    }

    /**
     * Email form handler.
     * Updates the email address only if the new one differs from the old one.
     */
    @PostMapping("/me/email")
    public String updateEmail(
            HttpSession session,
            RedirectAttributes redirectAttributes,
            Locale locale,
            @RequestParam(name = "email") String email
    ) {
        Form form = new Form();
        var emailField = form.addField("email", email);

        User user = (User) session.getAttribute("user");

        if (user == null) {
            return "redirect:/login";
        }

        //region Email Validation
        if (!email.equalsIgnoreCase(user.getEmail())) {
            if (!FormValidation.isEmail(email))
                emailField.addError(messageSource.getMessage("form.errors.emailAddressIncorrectFormat", null, locale));

            if (emailField.isValid()) user.setEmail(email);
            else redirectAttributes.addFlashAttribute("form", form);

            if (userDao.updateUser(user)) session.setAttribute("user", user);
        }
        //endregion

        return "redirect:/me";
    }

    /**
     * Password change form handler.
     * Checks that the current password matches, then if the two new passwords match.
     * Finally checks that the new password is strong enough.
     */
    @PostMapping("/me/password")
    public String updatePassword(
            HttpSession session,
            RedirectAttributes redirectAttributes,
            Locale locale,
            @RequestParam(name = "password") String currentPassword,
            @RequestParam(name = "newPassword", required = false) String newPassword,
            @RequestParam(name = "passwordCheck", required = false) String passwordCheck
    ) {
        Form form = new Form();
        var currentPasswordField = form.addField("password", currentPassword);
        var newPasswordField = form.addField("newPassword", newPassword);
        var passwordCheckField = form.addField("passwordCheck", passwordCheck);

        User user = (User) session.getAttribute("user");

        //region Current Password Validation
        if (!BCrypt.checkpw(currentPassword, user.getPassword()))
            currentPasswordField.addError(messageSource.getMessage("form.errors.incorrectCurrentPassword", null, locale));
        //endregion

        //region New Password Validation
        String newPwHash = BCrypt.hashpw(newPassword, BCrypt.gensalt(14));
        if (!newPassword.isBlank()) {
            if (!newPassword.equals(passwordCheck))
                passwordCheckField.addError(messageSource.getMessage("form.errors.passwordMismatch", null, locale));
            if (newPassword.length() < 10)
                newPasswordField.addError(messageSource.getMessage("form.errors.passwordIsShort", null, locale));
            if (!FormValidation.hasUppercase(newPassword, 1))
                newPasswordField.addError(messageSource.getMessage("form.errors.passwordNoUppercase", null, locale));
            if (!FormValidation.hasDigits(newPassword, 1))
                newPasswordField.addError(messageSource.getMessage("form.errors.passwordNoDigit", null, locale));
            if (!FormValidation.hasSpecialChars(newPassword, 1))
                newPasswordField.addError(messageSource.getMessage("form.errors.passwordNoSpecialCharacter", null, locale));

            if (newPasswordField.isValid() && passwordCheckField.isValid()) user.setPassword(newPwHash);
            else redirectAttributes.addFlashAttribute("form", form);

            if (userDao.updateUser(user)) session.setAttribute("user", user);
        }
        //endregion

        return "redirect:/me";
    }

    /**
     * Renew subscription form handler.
     * Checks the card and if it is valid and extends the subscription by one year.
     */
    @PostMapping("/me/renewSubscription")
    public String renewSubscription(
            HttpSession session,
            Locale locale,
            @RequestParam(name = "cardName") String cardName,
            @RequestParam(name = "cardNumber") String cardNumber,
            @RequestParam(name = "cardMonth") int cardMonth,
            @RequestParam(name = "cardYear") int cardYear,
            @RequestParam(name = "cardSecCode") String cardSecCode,
            RedirectAttributes redirectAttributes
    ) {
        User user = (User) session.getAttribute("user");

        if (user == null) return "redirect:/login";

        Form form = new Form();
        redirectAttributes.addFlashAttribute("form", form);
        var cardNameField = form.addField("cardName", cardName);
        var cardNumberField = form.addField("cardNumber", cardNumber);
        var cardMonthField = form.addField("cardMonth", String.valueOf(cardMonth));
        var cardYearField = form.addField("cardYear", String.valueOf(cardYear));
        var cardSecCodeField = form.addField("cardSecCode", String.valueOf(cardSecCode));

        //region Card Validation
        BigInteger cardNum;
        CreditCard card;

        try {
            cardNumber = cardNumber.replaceAll("\\D", "");
            cardNum = new BigInteger(cardNumber);

            try {
                LocalDate expiration = LocalDate.of(
                        cardYear,
                        cardMonth,
                        LocalDate.of(cardYear, cardMonth, 1).lengthOfMonth()
                );

                card = new CreditCard(cardNum, expiration, cardSecCode, cardName);

                if (card.isExpired())
                    form.addError(messageSource.getMessage("form.errors.cardAlreadyExpired", null, locale));
            } catch (IllegalArgumentException e) {
                form.addError(messageSource.getMessage("form.errors.cardRecheck", null, locale));
            } catch (InvalidCardNumberException e) {
                cardNumberField.addError(messageSource.getMessage("form.errors.cardInvalidNumber", null, locale));
            }
            // Card is valid
        } catch (NumberFormatException e) {
            cardNumberField.addError(messageSource.getMessage("form.errors.cardFormat", null, locale));
        }
        //endregion

        if (form.isValid()) {
            user.extendSubscription();
            userDao.updateUser(user);
            session.setAttribute("user", user);
        }

        return "redirect:/me/subscription";
    }
}
