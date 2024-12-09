package ie.groupproject.musicapp.persistence;

import ie.groupproject.musicapp.business.Artist;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Dylan Habis
 */
public class ArtistDaoImpl extends MySQLDao implements ArtistDao {

    public ArtistDaoImpl(Connection conn) {
        super(conn);
    }

    public ArtistDaoImpl(String propertiesFilename) {
        super(propertiesFilename);
    }

    /**
     * Retrieves all artists from the database using the sql command
     *
     * @return a list of all artists.
     */

    @Override
    public List<Artist> getAllArtists() {
        List<Artist> artists = new ArrayList<>();
        String sql = "SELECT * FROM artist";

        try (Connection conn = super.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Artist artist = new Artist();
                artist.setId(resultSet.getInt("id"));
                artist.setName(resultSet.getString("name"));
                artists.add(artist);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving artists", e);
        }

        return artists;
    }
}


