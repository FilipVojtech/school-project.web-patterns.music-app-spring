package ie.groupproject.musicapp.persistence;

import ie.groupproject.musicapp.business.User;
import ie.groupproject.musicapp.persistence.exceptions.EmailAddressAlreadyUsed;
import ie.groupproject.musicapp.persistence.exceptions.RecordNotFound;
import lombok.NonNull;

/**
 * @author Filip Vojtěch
 */
public interface UserDao extends AutoCloseable {
    /**
     * Retrieves a user by their ID
     *
     * @param id ID to look up by
     * @return A new {@systemProperty User} object. Null if the user couldn't be found
     * @throws RecordNotFound When the user is not found
     */
    User getUser(@NonNull int id) throws RecordNotFound;

    /**
     * Looks up user by their email
     *
     * @param email Email to look up by
     * @return The found user. Null if none found
     * @throws RecordNotFound When the user is not found
     */
    User getUserByEmail(@NonNull String email) throws RecordNotFound;

    /**
     * Creates a new user.
     *
     * @param user The new user data
     * @return True if create succeeded. False otherwise.
     * @throws EmailAddressAlreadyUsed When the created user has an email address already used by a different user.
     */
    boolean createUser(@NonNull User user) throws EmailAddressAlreadyUsed;

    /**
     * Updates the user based on ID.
     *
     * @param newUserData Data to update the user
     * @return True if update succeeded. False otherwise.
     */
    boolean updateUser(@NonNull User newUserData);
}
