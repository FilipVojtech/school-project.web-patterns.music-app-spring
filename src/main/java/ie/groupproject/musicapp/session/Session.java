package ie.groupproject.musicapp.session;

import ie.groupproject.musicapp.business.User;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * @author Filip Vojtěch
 */
public class Session {
    @Getter
    @Setter(onParam_ = @NonNull)
    private static User user = null;

    public static boolean IsLoggedIn() {
        return user != null;
    }

    public static void clearSession() {
        user = null;
    }
}
