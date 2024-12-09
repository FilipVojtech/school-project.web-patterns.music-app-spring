package ie.groupproject.musicapp.console;

import ie.groupproject.musicapp.business.Playlist;
import ie.groupproject.musicapp.business.Song;
import ie.groupproject.musicapp.persistence.PlaylistDao;
import ie.groupproject.musicapp.persistence.PlaylistDaoImpl;
import ie.groupproject.musicapp.session.Session;
import ie.groupproject.musicapp.util.Input;

import java.util.List;

/**
 * The PlaylistController class is a concrete implementation of the TextInterface
 * that provides a command-line interface for managing playlists.
 * It offers functionality to create playlists, add/remove songs from playlists,
 * and retrieve playlists and songs.
 */
public class PlaylistController extends TextInterface {
    private PlaylistDao playlistDao = new PlaylistDaoImpl();

    public PlaylistController() {
        super();
    }

    @Override
    public void listOptions() {
        System.out.println("Logged in as " + Session.getUser().getDisplayName());
        System.out.println("0. Exit");
        System.out.println("1. Create Playlist");
        System.out.println("2. Add Song to Playlist");
        System.out.println("3. Remove Song from Playlist");
        System.out.println("4. Get Playlist by ID");
        System.out.println("5. Get User Playlists");
        System.out.println("6. Get Public Playlists");
        System.out.println("7. Get Songs in Playlist");
    }

    @Override
    public void handleCommand(String choice) {
        switch (choice) {
            case "1":
                createPlaylist();
                break;
            case "2":
                addSongToPlaylist();
                break;
            case "3":
                removeSongFromPlaylist();
                break;
            case "4":
                getPlaylistById();
                break;
            case "5":
                getUserPlaylists();
                break;
            case "6":
                getPublicPlaylists();
                break;
            case "7":
                getSongsInPlaylist();
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
     * Prompts user to create new playlist by giving name and making sure
     * the playlist is public or private. The playlist is then saved using the `playlistDao`.
     * If the playlist creation is successful a success message is displayed to user.
     * If wrong, failure message shown
     *
     * The method follows these steps:
     * 1. Prompts the user to enter a name for the playlist.
     * 2. Prompts the user to specify if the playlist is public or private.
     * 3. Constructs a new `Playlist` object with the provided details and the ID of the current user.
     * 4. Uses `playlistDao` to attempt to create the playlist in the database.
     * 5. Displays a success or failure message based on the result of the playlist creation.
     */
    private void createPlaylist() {
        System.out.print("Enter playlist name: ");
        String name = Input.command();
        System.out.print("Is the playlist public? (true/false): ");
        String isPublicInput = Input.command();
        boolean isPublic = Boolean.parseBoolean(isPublicInput);

        Playlist playlist = Playlist.builder()
                .name(name)
                .isPublic(isPublic)
                .userId(Session.getUser().getId())
                .build();

        if (playlistDao.createPlaylist(playlist)) {
            System.out.println("Playlist created successfully: " + playlist.getName());
        } else {
            System.out.println("Failed to create playlist.");
        }
    }

    /**
     * This method prompts the user to enter a playlist ID and a song ID,
     * and then attempts to add the specified song to the specified playlist.
     * The method relies on user input to get the IDs and uses the `playlistDao`
     * to perform the addition. Success or failure messages are printed based
     * on the outcome of the operation.
     *
     * The method follows these steps:
     * 1. Prompts the user to enter a playlist ID.
     * 2. Prompts the user to enter a song ID.
     * 3. Attempts to add the song to the playlist using `playlistDao`.
     * 4. Prints a success message if the song is added successfully.
     * 5. Prints a failure message if the song could not be added.
     * 6. Catches and handles `NumberFormatException` for invalid input formats.
     */
    private void addSongToPlaylist() {
        try {
            System.out.print("Enter playlist ID: ");
            int playlistId = Integer.parseInt(Input.command());
            System.out.print("Enter song ID to add: ");
            int songId = Integer.parseInt(Input.command());

            if (playlistDao.addSongToPlaylist(playlistId, songId)) {
                System.out.println("Song added successfully to playlist.");
            } else {
                System.out.println("Failed to add song to playlist.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter valid numerical IDs.");
        }
    }

    /**
     * Prompts the user to remove a song from a specified playlist by entering the playlist ID
     * and the song ID that needs to be removed. The method uses the `playlistDao` to remove
     * the song and prints success or failure messages based on the operation outcome.
     *
     * The method follows these steps:
     * 1. Prompts the user to enter a playlist ID.
     * 2. Prompts the user to enter a song ID to remove.
     * 3. Attempts to remove the song from the playlist using `playlistDao`.
     * 4. Prints a success message if the song is removed successfully.
     * 5. Prints a failure message if the song could not be removed.
     * 6. Catches and handles `NumberFormatException` for invalid input formats.
     */
    private void removeSongFromPlaylist() {
        try {
            System.out.print("Enter playlist ID: ");
            int playlistId = Integer.parseInt(Input.command());
            System.out.print("Enter song ID to remove: ");
            int songId = Integer.parseInt(Input.command());

            if (playlistDao.removeSongFromPlaylist(playlistId, songId)) {
                System.out.println("Song removed successfully from playlist.");
            } else {
                System.out.println("Failed to remove song from playlist.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter valid numerical IDs.");
        }
    }

    /**
     * Retrieves a playlist by its ID, displays its details if found, and handles user input errors.
     *
     * This method follows these steps:
     * 1. Prompts the user to enter a playlist ID.
     * 2. Attempts to retrieve the playlist using `playlistDao`.
     * 3. Displays the playlist details if found, or a not-found message if the playlist doesn't exist.
     * 4. Catches and handles `NumberFormatException` for invalid input formats by printing an error message.
     */
    private void getPlaylistById() {
        try {
            System.out.print("Enter playlist ID: ");
            int playlistId = Integer.parseInt(Input.command());
            Playlist playlist = playlistDao.getPlaylistById(playlistId);

            if (playlist != null) {
                System.out.println("Playlist found: " + playlist.getName());
                displayPlaylistDetails(playlist);
            } else {
                System.out.println("No playlist found with that ID.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid numerical ID.");
        }
    }

    /**
     * Retrieves and displays the playlists belonging to the currently logged-in user.
     * If the user has no playlists, a message indicating this is displayed.
     * Otherwise, the IDs and names of the user's playlists are printed.
     */
    private void getUserPlaylists() {
        List<Playlist> playlists = playlistDao.getUserPlaylists(Session.getUser().getId());
        if (playlists.isEmpty()) {
            System.out.println("No playlists found for this user.");
        } else {
            System.out.println("User Playlists:");
            for (Playlist playlist : playlists) {
                System.out.println("- ID: " + playlist.getPlaylistId() + ", Name: " + playlist.getName());
            }
        }
    }

    /**
     * Retrieves and displays all public playlists.
     * This method fetches public playlists from the `playlistDao`. If no public playlists are found,
     * it prints a message indicating that no public playlists are available. Otherwise, it prints
     * the IDs and names of all public playlists.
     */
    private void getPublicPlaylists() {
        List<Playlist> playlists = playlistDao.getPublicPlaylists();
        if (playlists.isEmpty()) {
            System.out.println("No public playlists available.");
        } else {
            System.out.println("Public Playlists:");
            for (Playlist playlist : playlists) {
                System.out.println("- ID: " + playlist.getPlaylistId() + ", Name: " + playlist.getName());
            }
        }
    }

    /**
     * Retrieves and displays the songs in a specified playlist.
     *
     * This method prompts the user to enter a playlist ID, fetches the songs in the playlist
     * using `playlistDao`, and prints out the song details. If no songs are found in the playlist,
     * a message indicating an empty playlist is shown. Handles invalid input for the playlist ID.
     *
     * Steps:
     * 1. Prompts the user to enter a playlist ID.
     * 2. Attempts to retrieve songs from the specified playlist using `playlistDao`.
     * 3. If the playlist contains songs, prints the ID and title of each song.
     * 4. If the playlist is empty, prints a message indicating there are no songs.
     * 5. Catches and handles `NumberFormatException` for invalid input formats by printing an error message.
     */
    private void getSongsInPlaylist() {
        try {
            System.out.print("Enter playlist ID: ");
            int playlistId = Integer.parseInt(Input.command());
            List<Song> songs = playlistDao.getSongsInPlaylist(playlistId);

            if (songs.isEmpty()) {
                System.out.println("No songs found in this playlist.");
            } else {
                System.out.println("Songs in Playlist:");
                for (Song song : songs) {
                    System.out.println("- ID: " + song.getId() + ", Title: " + song.getTitle());
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid numerical ID.");
        }
    }

    /**
     * Displays detailed information about a playlist.
     *
     * @param playlist The playlist to display.
     */
    private void displayPlaylistDetails(Playlist playlist) {
        System.out.println("Playlist ID: " + playlist.getPlaylistId());
        System.out.println("Name: " + playlist.getName());
        System.out.println("Public: " + playlist.isPublic());
        System.out.println("Owner ID: " + playlist.getUserId());
    }
}
