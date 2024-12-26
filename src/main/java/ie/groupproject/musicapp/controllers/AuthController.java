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

@Controller
@Slf4j
public class AuthController {
    @GetMapping("/login")
    public String loginPage() {
        return "pages/auth/login";
    }

    @PostMapping("/auth/login")
    public RedirectView loginFormHandler() {
        return new RedirectView("/");
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
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
            RedirectAttributes redirectAttributes
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
            form.addError("You need to accept the terms to continue.");
        }
        //endregion

        //region Display Name Validation
        if (displayName.length() > 60) {
            displayNameField.addError("Display name cannot be longer than 60 characters");
        }
        //endregion

        //region Email Validation
        if (!FormValidation.isEmail(email)) emailField.addError("Please enter an email address");
        try {
            userDao.getUserByEmail(email);
            emailField.addError("User with this email address already exists, please choose a different email.");
        } catch (RecordNotFound ignore) {
        }
        //endregion

        //region Password Validation
        if (!password.equals(passwordCheck)) {
            passwordCheckField.addError("Passwords do not match.");
        }

        if (password.length() < 10) {
            passwordField.addError("Password must be at least 10 characters long.");
//            form.addError("password", "Password must be at least 10 characters long.");
        }
        if (!FormValidation.hasUppercase(password, 1))
            passwordField.addError("Password must have an uppercase letter.");
        if (!FormValidation.hasDigits(password, 1)) passwordField.addError("Password must have a digit.");
        if (!FormValidation.hasSpecialChars(password, 1))
            passwordField.addError("Password must have a special character.");
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

                if (card.isExpired()) form.addError("Card is already expired, please add a different one.");
            } catch (IllegalArgumentException e) {
                form.addError("Please check that the card details are correct.");
            } catch (InvalidCardNumberException e) {
                cardNumberField.addError("Invalid card number.");
            }
            // Card is valid
        } catch (NumberFormatException e) {
            cardNumberField.addError("Please check that card number is in correct format. Accepted characters are digits, spaces, and/or dashes.");
        }
        //endregion

        if (!form.isValid()) {
            return "redirect:/register";
        }

        String hashedPw = BCrypt.hashpw(password, BCrypt.gensalt(14));
        User newUser = new User(email, hashedPw, displayName);

        userDao.createUser(newUser);
        log.info("Created a new user '{}'", newUser.getEmail());
        session.setAttribute("user", newUser);

        return "redirect:/";
    }

    @GetMapping("/auth/logout")
    public RedirectView logoutUser(HttpSession httpSession) {
        httpSession.removeAttribute("user");

        return new RedirectView("/");
    }
}
