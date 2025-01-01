package ie.groupproject.musicapp.controllers;

import ie.groupproject.musicapp.business.Playlist;
import ie.groupproject.musicapp.business.Song;
import ie.groupproject.musicapp.business.User;
import ie.groupproject.musicapp.persistence.PlaylistDao;
import ie.groupproject.musicapp.persistence.PlaylistDaoImpl;
import ie.groupproject.musicapp.persistence.SongDao;
import ie.groupproject.musicapp.persistence.SongDaoImpl;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/playlists") // Base path for playlist-specific actions
public class PlaylistController {

    private final PlaylistDao playlistDao;
    private final SongDao songDao;

    // Initialize PlaylistDao and SongDao with database properties file
    public PlaylistController() {
        this.playlistDao = new PlaylistDaoImpl("database.properties");
        this.songDao = new SongDaoImpl("database.properties");
    }

    // Render the playlists page and display user-specific playlists
    @GetMapping
    public String playlistsPage(Model model, HttpSession session) {
        // Retrieve the logged-in user
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            // Redirect to login if no user is logged in
            return "redirect:/login";
        }

        // Fetch playlists assigned to the current user
        int userId = currentUser.getId();
        List<Playlist> userPlaylists = playlistDao.getUserPlaylists(userId);

        // Fetch all public playlists
        List<Playlist> publicPlaylists = playlistDao.getPublicPlaylists();

        // Separate public playlists into user's and others'
        List<Playlist> userPublicPlaylists = new ArrayList<>();
        List<Playlist> otherPublicPlaylists = new ArrayList<>();

        for (Playlist playlist : publicPlaylists) {
            if (playlist.getUserId() == userId) {
                userPublicPlaylists.add(playlist);
            } else {
                otherPublicPlaylists.add(playlist);
            }
        }

        // Add the playlists to the model
        model.addAttribute("playlists", userPlaylists);
        model.addAttribute("userPublicPlaylists", userPublicPlaylists);
        model.addAttribute("otherPublicPlaylists", otherPublicPlaylists);

        return "pages/playlists"; // Render the playlists.html page
    }

    // Create a new playlist
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

    // Fetch all public playlists
    @GetMapping("/public")
    public String publicPlaylistsPage(Model model, HttpSession session) {
        // Retrieve the logged-in user
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return "redirect:/login"; // Redirect to login if no user is logged in
        }

        // Fetch all public playlists
        List<Playlist> publicPlaylists = playlistDao.getPublicPlaylists();

        // Separate public playlists into user's and others'
        int userId = currentUser.getId();
        List<Playlist> userPublicPlaylists = new ArrayList<>();
        List<Playlist> otherPublicPlaylists = new ArrayList<>();

        for (Playlist playlist : publicPlaylists) {
            if (playlist.getUserId() == userId) {
                userPublicPlaylists.add(playlist);
            } else {
                otherPublicPlaylists.add(playlist);
            }
        }

        // Add playlists to the model
        model.addAttribute("userPublicPlaylists", userPublicPlaylists);
        model.addAttribute("otherPublicPlaylists", otherPublicPlaylists);

        return "pages/publicPlaylists"; // Render a page for public playlists
    }

    // Fetch songs for a specific playlist
    @GetMapping("/{playlistId}/songs")
    @ResponseBody
    public List<Map<String, String>> getPlaylistSongs(@PathVariable int playlistId) throws SQLException {
        List<Song> songs = songDao.getSongsByPlaylist(playlistId); // Fetch songs for the playlist
        List<Map<String, String>> songDetails = new ArrayList<>();

        for (Song song : songs) {
            Map<String, String> songDetail = new HashMap<>();
            songDetail.put("title", song.getTitle());
            songDetail.put("artist", songDao.getArtistName(song.getArtist_id())); // Get artist name
            songDetails.add(songDetail);
        }

        return songDetails; // Return song details as JSON
    }
}
