package ie.groupproject.musicapp.controllers;

import ie.groupproject.musicapp.business.User;
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

import java.util.Locale;

@Controller
public class UserController {
    private final MessageSource messageSource;

    public UserController(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @GetMapping("/me")
    public String userPage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");

        if (user == null) return "redirect:/";

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

        return "pages/user/me";
    }

    @PostMapping("/me/edit")
    public String editUser(
            HttpSession session,
            RedirectAttributes redirectAttributes,
            @RequestParam(name = "displayName") String displayName,
            @RequestParam(name = "email") String email,
            @RequestParam(name = "password") String currentPassword,
            @RequestParam(name = "newPassword", required = false) String newPassword,
            @RequestParam(name = "passwordCheck", required = false) String passwordCheck,
            Locale locale
    ) {
        Form form = new Form();
        redirectAttributes.addFlashAttribute("form", form);
        var displayNameField = form.addField("displayName", displayName);
        var emailField = form.addField("email", email);
        var currentPasswordField = form.addField("password", currentPassword);
        var newPasswordField = form.addField("newPassword", newPassword);
        var passwordCheckField = form.addField("passwordCheck", passwordCheck);

        User user = (User) session.getAttribute("user");
        User updatedUser = new User(user);

        //region Current Password Validation
        if (!BCrypt.checkpw(currentPassword, user.getPassword()))
            currentPasswordField.addError(messageSource.getMessage("form.errors.incorrectCurrentPassword", null, locale));
        //endregion

        //region Display Name Validation
        if (!displayName.equals(user.getDisplayName())) {
            if (displayName.isBlank())
                displayNameField.addError(messageSource.getMessage("form.errors.displayNameMissing", null, locale));
            if (displayName.length() > 60)
                displayNameField.addError(messageSource.getMessage("form.errors.displayNameTooLong", null, locale));
            if (displayNameField.isValid()) updatedUser.setDisplayName(displayName);
        }
        //endregion

        //region Email Validation
        if (!email.equalsIgnoreCase(user.getEmail())) {
            if (!FormValidation.isEmail(email))
                emailField.addError(messageSource.getMessage("form.errors.emailAddressIncorrectFormat", null, locale));
            if (emailField.isValid()) updatedUser.setEmail(email);
        }
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
        }
        if (newPasswordField.isValid() && passwordCheckField.isValid()) updatedUser.setPassword(newPwHash);
        //endregion

        if (form.isValid())
            if (!updatedUser.equals(user) || !user.getPassword().equals(updatedUser.getPassword())) {
                UserDao userDao = new UserDaoImpl("database.properties");
                if (userDao.updateUser(updatedUser)) session.setAttribute("user", updatedUser);
            }
        return "redirect:/me";
    }
}
