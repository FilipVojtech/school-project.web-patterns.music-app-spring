package ie.groupproject.musicapp.persistence;

import ie.groupproject.musicapp.business.Playlist;
import ie.groupproject.musicapp.business.Song;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlaylistDaoImpl extends MySQLDao implements PlaylistDao {
    public PlaylistDaoImpl() {
        super();
    }

    public PlaylistDaoImpl(Connection conn) {
        super(conn);
    }

    public PlaylistDaoImpl(String propertiesFilename) {
        super(propertiesFilename);
    }

    @Override
    public boolean createPlaylist(Playlist playlist) {
        String sql = "INSERT INTO playlist (owner_id, name, visibility) VALUES (?, ?, ?)";
        int rowsAffected = 0;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, playlist.getUserId());
            ps.setString(2, playlist.getName());
            ps.setBoolean(3, playlist.isPublic());

            rowsAffected = ps.executeUpdate();

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    playlist.setPlaylistId(generatedKeys.getInt(1));
                }
            }

        } catch (SQLException e) {
            System.out.println("Error creating playlist: " + e.getMessage());
        }

        return rowsAffected > 0;
    }

    @Override
    public boolean updatePlaylist(Playlist playlist) {
        String sql = "UPDATE playlist SET name = ?, visibility = ? WHERE id = ?";
        int rowsAffected = 0;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, playlist.getName());
            ps.setBoolean(2, playlist.isPublic());
            ps.setInt(3, playlist.getPlaylistId());

            rowsAffected = ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error updating playlist: " + e.getMessage());
        }

        return rowsAffected > 0;
    }

    @Override
    public boolean addSongToPlaylist(int playlistId, int songId) {
        String sql = "INSERT INTO playlist_songs (playlist_id, song_id) VALUES (?, ?)";
        int rowsAffected = 0;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, playlistId);
            ps.setInt(2, songId);

            rowsAffected = ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error adding song to playlist: " + e.getMessage());
        }

        return rowsAffected > 0;
    }

    @Override
    public boolean removeSongFromPlaylist(int playlistId, int songId) {
        String sql = "DELETE FROM playlist_songs WHERE playlist_id = ? AND song_id = ?";
        int rowsAffected = 0;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, playlistId);
            ps.setInt(2, songId);

            rowsAffected = ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error removing song from playlist: " + e.getMessage());
        }

        return rowsAffected > 0;
    }

    @Override
    public Playlist getPlaylistById(int playlistId) {
        String sql = "SELECT * FROM playlist WHERE id = ?";
        Playlist playlist = null;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, playlistId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    playlist = mapPlaylist(rs);
                    playlist.setSongs(getSongsInPlaylist(playlistId)); // Load songs if needed
                }
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving playlist: " + e.getMessage());
        }

        return playlist;
    }

    @Override
    public List<Playlist> getUserPlaylists(int userId) {
        String sql = "SELECT * FROM playlist WHERE owner_id = ?";
        List<Playlist> playlists = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    playlists.add(mapPlaylist(rs));
                }
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving user playlists: " + e.getMessage());
        }

        return playlists;
    }

    @Override
    public List<Playlist> getPublicPlaylists() {
        String sql = "SELECT * FROM playlist WHERE visibility = TRUE";
        List<Playlist> playlists = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                playlists.add(mapPlaylist(rs));
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving public playlists: " + e.getMessage());
        }

        return playlists;
    }

    @Override
    public List<Song> getSongsInPlaylist(int playlistId) {
        String sql = "SELECT s.* FROM song s JOIN playlist_songs ps ON s.id = ps.song_id WHERE ps.playlist_id = ?";
        List<Song> songs = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, playlistId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    songs.add(mapSong(rs));
                }
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving songs in playlist: " + e.getMessage());
        }

        return songs;
    }

    private Playlist mapPlaylist(ResultSet rs) throws SQLException {
        return Playlist.builder()
                .playlistId(rs.getInt("id"))
                .name(rs.getString("name"))
                .isPublic(rs.getBoolean("visibility"))
                .userId(rs.getInt("owner_id"))
                .build();
    }

    private Song mapSong(ResultSet rs) throws SQLException {
        return Song.builder()
                .id(rs.getInt("id"))
                .title(rs.getString("title"))
                .artist_id(rs.getInt("artist_id"))
                .build();
    }
}
