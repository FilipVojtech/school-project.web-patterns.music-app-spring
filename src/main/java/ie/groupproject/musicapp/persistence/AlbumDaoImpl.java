package ie.groupproject.musicapp.persistence;

import ie.groupproject.musicapp.business.Album;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Dylan Habis
 */
public class AlbumDaoImpl extends MySQLDao implements AlbumDao {

    public AlbumDaoImpl(Connection conn) {
        super(conn);
    }

    public AlbumDaoImpl(String propertiesFilename) {
        super(propertiesFilename);
    }

    /**
     * This Method Retrieves a list of albums by the specified artist
     *
     * @param artistId The ID of the artist for retrieving albums
     * @return A list of Album objects associated with the specified artist
     */
    @Override
    public List<Album> getAlbumsByArtist(int artistId) {
        List<Album> albums = new ArrayList<>();

        Connection conn = super.getConnection();
        String sql = "SELECT * FROM albums WHERE artist_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, artistId);
            ResultSet resultSet = ps.executeQuery();

            while (resultSet.next()) {
                Album album = new Album();
                album.setId(resultSet.getInt("id"));
                album.setTitle(resultSet.getString("title"));
                album.setArtist_Id(resultSet.getInt("artist_id"));
                albums.add(album);
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            super.freeConnection(conn);
        }

        return albums; // Return the list of albums
    }
}
