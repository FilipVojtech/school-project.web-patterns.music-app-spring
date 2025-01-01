package ie.groupproject.musicapp.controllers;

import ie.groupproject.musicapp.business.Playlist;
import ie.groupproject.musicapp.business.User;
import ie.groupproject.musicapp.persistence.PlaylistDao;
import ie.groupproject.musicapp.persistence.PlaylistDaoImpl;
import ie.groupproject.musicapp.session.Session;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/playlists") // Base path for playlist-specific actions
public class PlaylistController {

    private final PlaylistDao playlistDao;

    // Initialize PlaylistDao with database properties file
    public PlaylistController() {
        this.playlistDao = new PlaylistDaoImpl("database.properties");
    }

    // Render the playlists page
    @GetMapping
    public String playlistsPage() {
        return "pages/playlists"; // This serves the playlists.html view
    }

//    // Create a new playlist
//    @PostMapping("/create")
//    public String createPlaylist(@RequestParam String name, @RequestParam boolean visibility) {
//        Playlist newPlaylist = Playlist.builder()
//                .name(name)
//                .isPublic(visibility)
//                .userId(1) // Replace '1' with the logged-in user ID
//                .build();
//
//        playlistDao.createPlaylist(newPlaylist);
//
//        return "redirect:/playlists"; // Redirect back to the playlists page
//    }
//
//    @PostMapping("/create")
//    public String createPlaylist(@RequestParam String name, @RequestParam boolean visibility) {
//        if (!Session.IsLoggedIn()) {
//            // Handle case where no user is logged in
//            return "redirect:/login"; // Redirect to login page or show an error
//        }
//
//        // Get the current user's ID from the session
//        int userId = Session.getUser().getId();
//
//        // Create the new playlist
//        Playlist newPlaylist = Playlist.builder()
//                .name(name)
//                .isPublic(visibility)
//                .userId(userId) // Set the logged-in user's ID as the owner
//                .build();
//
//        // Save the playlist to the database
//        playlistDao.createPlaylist(newPlaylist);
//
//        return "redirect:/playlists"; // Redirect back to the playlists page
//    }

    @PostMapping("/create")
    public String createPlaylist(@RequestParam String name, @RequestParam boolean visibility, HttpSession session) {
        // Check if a user is logged in
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            // Redirect to login if no user is logged in
            return "redirect:/login";
        }

        // Get the logged-in user's ID
        int userId = currentUser.getId();

        // Create the new playlist
        Playlist newPlaylist = Playlist.builder()
                .name(name)
                .isPublic(visibility)
                .userId(userId) // Set the logged-in user's ID as the owner
                .build();

        // Save the playlist to the database
        playlistDao.createPlaylist(newPlaylist);

        return "redirect:/playlists"; // Redirect back to the playlists page
    }


}
