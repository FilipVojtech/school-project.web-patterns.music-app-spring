package ie.groupproject.musicapp.persistence;

import ie.groupproject.musicapp.business.Song;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Dylan Habis
 */
public class SongDaoImpl extends MySQLDao implements SongDao {
    public SongDaoImpl(Connection conn) {
        super(conn);
    }

    public SongDaoImpl(String propertiesFilename) {
        super(propertiesFilename);
    }

    /**
     * Retrieves a list of songs from the database that belong to a specified album
     *
     * @param albumId the ID of the album for which the songs are to be retrieved
     * @return a list of Song objects that belong to the specified album
     */
    @Override
    public List<Song> getSongsByAlbum(int albumId) {
        List<Song> songs = new ArrayList<>();

        Connection conn = super.getConnection();
        String sql = "SELECT * FROM songs WHERE album_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, albumId);
            ResultSet resultSet = ps.executeQuery();

            // Iterate through the result set and create Song objects
            while (resultSet.next()) {
                Song song = new Song();
                song.setId(resultSet.getInt("id"));
                song.setTitle(resultSet.getString("title"));
                song.setAlbumId(resultSet.getInt("album_id"));
                songs.add(song); // Add the song to the list
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            // Close the connection using the superclass method
            super.freeConnection(conn);
        }

        return songs;
    }

    /**
     * Searches for songs in the database based on a keyword The search is performed on
     * song titles, album titles, and artist names
     *
     * @param keyword the keyword to search for in the song titles, album titles, and artist names
     * @return a list of songs that match the search criteria
     */
    @Override
    public List<Song> searchSongs(String keyword) {
        List<Song> songs = new ArrayList<>();
        Connection conn = super.getConnection();
        String sql = "SELECT s.* " +
                     "FROM song s " +
                     "     JOIN music_app.album a ON s.artist_id = a.artist_id " +
                     "     JOIN music_app.artist ar ON ar.id = a.artist_id " +
                     "WHERE s.title LIKE ? " +
                     "   OR a.title LIKE ? " +
                     "   OR ar.name LIKE ?;";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            String searchPattern = "%" + keyword + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            ps.setString(3, searchPattern);

            ResultSet resultSet = ps.executeQuery();

            while (resultSet.next()) {
                Song song = new Song();
                song.setId(resultSet.getInt("id"));
                song.setTitle(resultSet.getString("title"));
                song.setArtist_id(resultSet.getInt("artist_id"));
                songs.add(song); // Add the song to the list
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            super.freeConnection(conn);
        }

        return songs;
    }

    public String getArtistName(int artistId) {
        String artistName = "";
        String sql = "SELECT name FROM artist WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, artistId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    artistName = rs.getString("name");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving artist name: " + e.getMessage());
        }

        return artistName;
    }

    @Override
    public List<Song> getSongsByPlaylist(int playlistId) {
        List<Song> songs = new ArrayList<>();
        String sql = "SELECT s.id, s.title, s.artist_id FROM song s " +
                "JOIN playlist_songs ps ON s.id = ps.song_id " +
                "WHERE ps.playlist_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, playlistId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Song song = new Song();
                    song.setId(rs.getInt("id"));
                    song.setTitle(rs.getString("title"));
                    song.setArtist_id(rs.getInt("artist_id"));
                    songs.add(song);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving songs by playlist: " + e.getMessage());
        }

        return songs;
    }

    @Override
    public String getFirstSongName() {
        String sql = "SELECT title FROM song";
        String songName = null;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                songName = rs.getString("title");
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving the first song name: " + e.getMessage());
        }

        return songName; // Will return null if no songs exist
    }



}
