package ie.groupproject.musicapp.console;

import ie.groupproject.musicapp.business.Rating;
import ie.groupproject.musicapp.business.Song;
import ie.groupproject.musicapp.persistence.RatingDao;
import ie.groupproject.musicapp.session.Session;
import ie.groupproject.musicapp.util.Input;

import java.util.List;

/**
 * @author Alex Clinton
 */
public class RatingController extends TextInterface {
    private RatingDao ratingDao;

    /**
     * Constructor initializes the RatingDao implementation.
     */
    public RatingController() {
        super();
    }

    /**
     * Lists the available rating options to the user.
     */
    @Override
    public void listOptions() {
        System.out.println("\n--- Rating Menu ---");
        System.out.println("Logged in as: " + Session.getUser().getDisplayName());
        System.out.println("0. Exit to Main Menu");
        System.out.println("1. Rate a Song");
        System.out.println("2. View Your Ratings");
        System.out.println("3. View Top Rated Song");
        System.out.println("4. View Most Popular Song");
        System.out.println("5. View Average Rating for a Song");
    }

    /**
     * Handles the user's menu selection and invokes the corresponding method.
     *
     * @param choice The user's menu choice.
     */
    @Override
    public void handleCommand(String choice) {
        switch (choice) {
            case "1":
                rateSong();
                break;
            case "2":
                viewUserRatings();
                break;
            case "3":
                viewTopRatedSong();
                break;
            case "4":
                viewMostPopularSong();
                break;
            case "5":
                viewAverageRatingForSong();
                break;
            case "0":
                exitProgram();
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
                break;
        }
    }

    /**
     * Allows the user to rate a song by providing the song ID and rating value.
     */
    private void rateSong() {
        try {
            System.out.print("Enter Song ID to rate: ");
            int songId = Integer.parseInt(Input.command());


            System.out.print("Enter rating value (1 to 5): ");
            int ratingValue = Integer.parseInt(Input.command());

            if (ratingValue < 1 || ratingValue > 5) {
                System.out.println("Invalid rating value. Please enter a value between 1 and 5.");
                return;
            }

            Rating rating = Rating.builder()
                    .userId(Session.getUser().getId())
                    .songId(songId)
                    .ratingValue(ratingValue)
                    .build();

            if (ratingDao.rateSong(rating)) {
                System.out.println("Successfully rated the song.");
                System.out.println("Rating ID: " + rating.getRatingId());
            } else {
                System.out.println("Failed to rate the song. Please try again.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter valid numerical IDs and rating values.");
        }
    }

    /**
     * Displays all ratings submitted by the logged-in user.
     */
    private void viewUserRatings() {
        int userId = Session.getUser().getId();
        List<Rating> ratings = ratingDao.getUserRatings(userId);

        if (ratings.isEmpty()) {
            System.out.println("You have not rated any songs yet.");
        } else {
            System.out.println("\n--- Your Ratings ---");
            for (Rating rating : ratings) {
                System.out.println("Rating ID: " + rating.getRatingId()
                        + ", Song ID: " + rating.getSongId()
                        + ", Rating: " + rating.getRatingValue());
            }
        }
    }

    /**
     * Displays the top-rated song based on average ratings.
     */
    private void viewTopRatedSong() {
        Song topRatedSong = ratingDao.getTopRatedSong();

        if (topRatedSong != null) {
            System.out.println("\n--- Top Rated Song ---");
            System.out.println("Song ID: " + topRatedSong.getId());
            System.out.println("Title: " + topRatedSong.getTitle());
            System.out.println("Artist ID: " + topRatedSong.getArtist_id());
            double averageRating = ratingDao.getAverageRating(topRatedSong.getId());
            System.out.println("Average Rating: " + String.format("%.2f", averageRating));
        } else {
            System.out.println("No ratings available to determine the top-rated song.");
        }
    }

    /**
     * Displays the most popular song based on the number of playlists it appears in.
     */
    private void viewMostPopularSong() {
        Song mostPopularSong = ratingDao.getMostPopularSong();

        if (mostPopularSong != null) {
            System.out.println("\n--- Most Popular Song ---");
            System.out.println("Song ID: " + mostPopularSong.getId());
            System.out.println("Title: " + mostPopularSong.getTitle());
            System.out.println("Artist ID: " + mostPopularSong.getArtist_id());
            System.out.println("Appears in " + mostPopularSong.getPlaylistCount() + " playlists.");
        } else {
            System.out.println("No data available to determine the most popular song.");
        }
    }

    /**
     * Displays the average rating for a specific song.
     */
    private void viewAverageRatingForSong() {
        try {
            System.out.print("Enter Song ID to view average rating: ");
            int songId = Integer.parseInt(Input.command());

            double averageRating = ratingDao.getAverageRating(songId);

            if (averageRating > 0) {
                System.out.println("Average Rating for Song ID " + songId + ": " + String.format("%.2f", averageRating));
            } else {
                System.out.println("No ratings available for Song ID " + songId + ".");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid numerical Song ID.");
        }
    }
}
