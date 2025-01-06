package ie.groupproject.musicapp.persistence;

import ie.groupproject.musicapp.business.User;
import ie.groupproject.musicapp.persistence.exceptions.EmailAddressAlreadyUsed;
import ie.groupproject.musicapp.persistence.exceptions.RecordNotFound;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.MessageFormat;


/**
 * @author Filip Vojtěch
 */
@Slf4j
public class UserDaoImpl extends MySQLDao implements UserDao {
    public UserDaoImpl() {
        super();
    }

    public UserDaoImpl(Connection conn) {
        super(conn);
    }

    public UserDaoImpl(String propertiesFilename) {
        super(propertiesFilename);
    }

    /**
     * Get a user by ID.
     *
     * @param id ID to look up by.
     * @return The user data.
     * @throws RecordNotFound If the user could not be found.
     */
    @Override
    public User getUser(int id) throws RecordNotFound {
        final var sql = "SELECT * FROM users WHERE id = ?";

        try (Connection con = super.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);

            final var result = ps.executeQuery();

            if (result.next()) {
                return new User(
                        result.getInt("id"),
                        result.getString("display_name"),
                        result.getString("email"),
                        result.getString("password"),
                        result.getDate("sub_since").toLocalDate(),
                        result.getDate("sub_end").toLocalDate()
                );
            } else {
                throw new RecordNotFound(MessageFormat.format("Couldn''t find user by that ID ({0})", id));
            }
        } catch (SQLException e) {
            System.out.println("Error while fetching user.");
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Gets a user by their email.
     *
     * @param email Email to look up by.
     * @return The user.
     * @throws RecordNotFound If the user could not be found.
     */
    @Override
    public User getUserByEmail(@NonNull String email) throws RecordNotFound {
        final var sql = "SELECT * FROM users WHERE email = ?";

        try (Connection con = super.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);

            final var result = ps.executeQuery();

            if (result.next()) {
                User user = new User(
                        result.getInt("id"),
                        result.getString("display_name"),
                        result.getString("email"),
                        result.getString("password"),
                        result.getDate("sub_since").toLocalDate(),
                        result.getDate("sub_end").toLocalDate()
                );
                return user;
            } else {
                throw new RecordNotFound(MessageFormat.format("Couldn''t find user with that email ({0})", email));
            }
        } catch (SQLException e) {
            System.out.println("Error while fetching user.");
        }
        return null;
    }

    /**
     * Creates a user in the DB.
     *
     * @param user The new user data
     * @return True if the user was created, false otherwise.
     * @throws EmailAddressAlreadyUsed If the email is already in use.
     */
    @Override
    public boolean createUser(@NonNull User user) throws EmailAddressAlreadyUsed {
        final var sql = "INSERT INTO users (email, password, display_name) VALUE (?, ?, ?)";

        try {
            if (getUserByEmail(user.getEmail()) != null)
                throw new EmailAddressAlreadyUsed("User with this email address already exists.");
        } catch (RecordNotFound ignored) {
        }


        try (Connection con = super.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, user.getEmail());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getDisplayName());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Couldn't create user.");
        }
        return false;
    }

    /**
     * Updates the DB user with the new details using the ID of the user.
     * If the ID is not set, the user is not updated.
     *
     * @param newUserData Data to update the user.
     * @return True if the operation succeeded, false otherwise.
     */
    @Override
    public boolean updateUser(@NonNull User newUserData) {
        if (newUserData.getId() == 0) {
            System.out.println("Cannot update user. ID is not set.");
        }
        final var sql = "UPDATE users SET display_name = ?, email = ?, password = ?, sub_since = ?, sub_end = ? WHERE id = ?";

        try (Connection con = super.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, newUserData.getDisplayName());
            ps.setString(2, newUserData.getEmail());
            ps.setString(3, newUserData.getPassword());
            ps.setDate(4, Date.valueOf(newUserData.getSubscriptionSince()));
            ps.setDate(5, Date.valueOf(newUserData.getSubscriptionEnd()));
            ps.setInt(6, newUserData.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            log.error("Could not update user with ID={}", newUserData.getId());
        }
        return false;
    }
}
