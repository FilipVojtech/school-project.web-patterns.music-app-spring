package ie.groupproject.musicapp.controllers;

import ie.groupproject.musicapp.business.CreditCard;
import ie.groupproject.musicapp.business.User;
import ie.groupproject.musicapp.business.exceptions.InvalidCardNumberException;
import ie.groupproject.musicapp.persistence.UserDao;
import ie.groupproject.musicapp.persistence.UserDaoImpl;
import ie.groupproject.musicapp.persistence.exceptions.RecordNotFound;
import ie.groupproject.musicapp.ui.Form;
import ie.groupproject.musicapp.util.FormValidation;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Locale;

@Controller
@Slf4j
public class AuthController {
    @Autowired
    MessageSource messageSource;

    @GetMapping("/login")
    public String loginPage(Model model, HttpSession session) {
        if (session.getAttribute("user") != null) return "redirect:/";
        if (!model.containsAttribute("form")) model.addAttribute("form", new Form());
        return "pages/auth/login";
    }

    @PostMapping("/auth/login")
    public String loginFormHandler(
            HttpSession session,
            @RequestParam(name = "email") String email,
            @RequestParam(name = "password") String password,
            RedirectAttributes redirectAttributes,
            Locale locale
    ) {
        UserDao userDao = new UserDaoImpl("database.properties");
        Form form = new Form();
        var emailField = form.addField("email", email);
        var passwordField = form.addField("password", password);

        redirectAttributes.addFlashAttribute("form", form);

        //region Email Validation
        if (!FormValidation.isEmail(email))
            emailField.addError(messageSource.getMessage("form.errors.emailAddressIncorrectFormat", null, locale));
        //endregion

        User user;
        try {
            user = userDao.getUserByEmail(email);
        } catch (RecordNotFound e) {
            user = null;
        }

        if (user != null && BCrypt.checkpw(password, user.getPassword())) {
            session.setAttribute("user", user);
            return "redirect:/";
        } else {
            form.addError(messageSource.getMessage("form.errors.incorrectEmailOrPassword", null, locale));
            return "redirect:/login";
        }

    }

    @GetMapping("/register")
    public String registerPage(Model model, HttpSession session) {
        if (session.getAttribute("user") != null) return "redirect:/";

        var map = new LinkedHashMap<Integer, String>();
        for (int i = 0; i < 12; i++) {
            map.put(i + 1, LocalDate.of(2024, i + 1, 1).getMonth().toString());
        }
        model.addAttribute("months", map);
        if (!model.containsAttribute("form")) model.addAttribute("form", new Form());
        return "pages/auth/register";
    }

    @PostMapping("/auth/register")
    public String registerFormHandler(
            HttpSession session,
            @RequestParam(name = "displayName") String displayName,
            @RequestParam(name = "email") String email,
            @RequestParam(name = "password") String password,
            @RequestParam(name = "passwordCheck") String passwordCheck,
            @RequestParam(name = "cardName") String cardName,
            @RequestParam(name = "cardNumber") String cardNumber,
            @RequestParam(name = "cardMonth") int cardMonth,
            @RequestParam(name = "cardYear") int cardYear,
            @RequestParam(name = "cardSecCode") String cardSecCode,
            @RequestParam(name = "gdpr", required = false, defaultValue = "false") boolean gdpr,
            RedirectAttributes redirectAttributes,
            Locale locale
    ) {
        UserDao userDao = new UserDaoImpl("database.properties");
        Form form = new Form();
        var displayNameField = form.addField("displayName", displayName);
        var emailField = form.addField("email", email);
        var passwordField = form.addField("password", password);
        var passwordCheckField = form.addField("passwordCheck", passwordCheck);
        var cardNameField = form.addField("cardName", cardName);
        var cardNumberField = form.addField("cardNumber", cardNumber);
        var cardMonthField = form.addField("cardMonth", String.valueOf(cardMonth));
        var cardYearField = form.addField("cardYear", String.valueOf(cardYear));
        var cardSecCodeField = form.addField("cardSecCode", String.valueOf(cardSecCode));
        var gdprField = form.addField("gdpr", String.valueOf(gdpr));

        redirectAttributes.addFlashAttribute("form", form);

        //region GDPR
        if (!gdpr) {
            form.addError(messageSource.getMessage("form.errors.gdprNotAccepted", null, locale));
        }
        //endregion

        //region Display Name Validation
        if (displayName.length() > 60) {
            displayNameField.addError(messageSource.getMessage("form.errors.displayNameTooLong", null, locale));
        }
        //endregion

        //region Email Validation
        if (!FormValidation.isEmail(email))
            emailField.addError(messageSource.getMessage("form.errors.emailAddressIncorrectFormat", null, locale));
        try {
            userDao.getUserByEmail(email);
            emailField.addError(messageSource.getMessage("form.errors.userAlreadyExists", null, locale));
        } catch (RecordNotFound ignore) {
        }
        //endregion

        //region Password Validation
        if (!password.equals(passwordCheck))
            passwordCheckField.addError(messageSource.getMessage("form.errors.passwordMismatch", null, locale));

        if (password.length() < 10)
            passwordField.addError(messageSource.getMessage("form.errors.passwordIsShort", null, locale));
        if (!FormValidation.hasUppercase(password, 1))
            passwordField.addError(messageSource.getMessage("form.errors.passwordNoUppercase", null, locale));
        if (!FormValidation.hasDigits(password, 1))
            passwordField.addError(messageSource.getMessage("form.errors.passwordNoDigit", null, locale));
        if (!FormValidation.hasSpecialChars(password, 1))
            passwordField.addError(messageSource.getMessage("form.errors.passwordNoSpecialCharacter", null, locale));
        //endregion

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

        if (!form.isValid()) {
            return "redirect:/register";
        }

        String hashedPw = BCrypt.hashpw(password, BCrypt.gensalt(14));
        User newUser = new User(email, hashedPw, displayName, LocalDate.now(), LocalDate.now().plusMonths(12));

        userDao.createUser(newUser);
        log.info("Created a new user '{}'", newUser.getEmail());
        session.setAttribute("user", newUser);

        return "redirect:/";
    }

    @GetMapping("/auth/logout")
    public String logoutUser(HttpSession httpSession) {
        User user = (User) httpSession.getAttribute("user");
        if (user != null) {
            httpSession.removeAttribute("user");
            log.info("User logged out {}", user.getEmail());
        }
        return "redirect:/";
    }
}
