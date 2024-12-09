package ie.groupproject.musicapp.persistence;

import ie.groupproject.musicapp.business.Rating;
import ie.groupproject.musicapp.business.Song;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Alex Clinton
 */

public class RatingDaoImpl extends MySQLDao implements RatingDao {
    public RatingDaoImpl(Connection conn) {
        super(conn);
    }

    public RatingDaoImpl(String propertiesFilename) {
        super(propertiesFilename);
    }

    @Override
    public boolean rateSong(Rating rating) {
        String sql = "INSERT INTO song_ratings (user_id, song_id, rating_value) VALUES (?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE rating_value = ?";
        int rowsAffected = 0;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, rating.getUserId());
            ps.setInt(2, rating.getSongId());
            ps.setInt(3, rating.getRatingValue());
            ps.setInt(4, rating.getRatingValue()); // For the UPDATE part

            rowsAffected = ps.executeUpdate();

            // Retrieve the generated rating ID (if a new rating was inserted)
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    rating.setRatingId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error inserting/updating rating: " + e.getMessage());
            e.printStackTrace();
            return false;
        }

        return rowsAffected > 0;
    }

//    @Override
//    public boolean updateRating(Rating rating) {
//        return false;
//    }

    @Override
    public List<Rating> getUserRatings(int userId) {
        String sql = "SELECT sr.rating_id, sr.user_id, sr.song_id, sr.rating_value, s.title " +
                "FROM song_ratings sr JOIN song s ON sr.song_id = s.id " +
                "WHERE sr.user_id = ?";
        List<Rating> ratings = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Rating rating = Rating.builder()
                            .ratingId(rs.getInt("rating_id"))
                            .userId(rs.getInt("user_id"))
                            .songId(rs.getInt("song_id"))
                            .ratingValue(rs.getInt("rating_value"))
                            .build();

                    ratings.add(rating);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving user ratings: " + e.getMessage());
            e.printStackTrace();
        }

        return ratings;
    }

    @Override
    public Song getTopRatedSong() {
        String sql = "SELECT s.*, AVG(sr.rating_value) AS avg_rating " +
                "FROM song s JOIN song_ratings sr ON s.id = sr.song_id " +
                "GROUP BY s.id " +
                "ORDER BY avg_rating DESC " +
                "LIMIT 1";
        Song song = null;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                song = Song.builder()
                        .id(rs.getInt("id"))
                        .title(rs.getString("title"))
                        .artist_id(rs.getInt("artist_id"))
                        .rating(rs.getInt("rating"))
                        .build();
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving top-rated song: " + e.getMessage());
            e.printStackTrace();
        }

        return song;
    }

    @Override
    public Song getMostPopularSong() {
        String sql = "SELECT s.*, COUNT(ps.playlist_id) AS playlist_count " +
                "FROM song s JOIN playlist_songs ps ON s.id = ps.song_id " +
                "GROUP BY s.id " +
                "ORDER BY playlist_count DESC " +
                "LIMIT 1";
        Song song = null;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                song = Song.builder()
                        .id(rs.getInt("id"))
                        .title(rs.getString("title"))
                        .artist_id(rs.getInt("artist_id"))
                        .build();

                // Set the playlist count (popularity)
                song.setPlaylistCount(rs.getInt("playlist_count")); //---- Temporarily edited out to not casue error to show, waiting for playlistcount to be added to Song Class
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving most popular song: " + e.getMessage());
            e.printStackTrace();
        }

        return song;
    }

    @Override
    public double getAverageRating(int songId) {
        String sql = "SELECT AVG(rating_value) AS avg_rating FROM song_ratings WHERE song_id = ?";
        double averageRating = 0.0;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, songId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    averageRating = rs.getDouble("avg_rating");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error calculating average rating: " + e.getMessage());
            e.printStackTrace();
        }

        return averageRating;
    }
}
