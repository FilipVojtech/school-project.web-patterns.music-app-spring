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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    /**
     * Renders the playlists page and displays playlists specific to the logged-in user.
     *
     * @param model   The model to pass data to the view.
     * @param session The HTTP session to fetch the current user.
     * @return The name of the view template to render.
     */
    // Render the playlists page and display user-specific playlists
    @GetMapping
    public String playlistsPage(Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return "redirect:/login";
        }

        int userId = currentUser.getId();
        List<Playlist> userPlaylists = playlistDao.getUserPlaylists(userId);

        List<Playlist> publicPlaylists = playlistDao.getPublicPlaylists();
        List<Playlist> userPublicPlaylists = new ArrayList<>();
        List<Playlist> otherPublicPlaylists = new ArrayList<>();

        for (Playlist playlist : publicPlaylists) {
            if (playlist.getUserId() == userId) {
                userPublicPlaylists.add(playlist);
            } else {
                otherPublicPlaylists.add(playlist);
            }
        }

        model.addAttribute("playlists", userPlaylists);
        model.addAttribute("userPublicPlaylists", userPublicPlaylists);
        model.addAttribute("otherPublicPlaylists", otherPublicPlaylists);

        return "pages/playlists";
    }

    /**
     * Creates a new playlist for the logged-in user.
     *
     * @param name       The name of the new playlist.
     * @param visibility Indicates if the playlist is public or private.
     * @param session    The HTTP session to fetch the current user.
     * @return A redirect to the playlists page.
     */
    // Create a new playlist
    @PostMapping("/create")
    public String createPlaylist(@RequestParam String name, @RequestParam boolean visibility, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return "redirect:/login";
        }

        int userId = currentUser.getId();

        Playlist newPlaylist = Playlist.builder()
                .name(name)
                .isPublic(visibility)
                .userId(userId)
                .build();

        playlistDao.createPlaylist(newPlaylist);

        return "redirect:/playlists";
    }

    /**
     * Fetches all public playlists and categorizes them as user-specific or other public playlists.
     *
     * @param model   The model to pass data to the view.
     * @param session The HTTP session to fetch the current user.
     * @return The name of the view template to render.
     */
    // Fetch all public playlists
    @GetMapping("/public")
    public String publicPlaylistsPage(Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return "redirect:/login";
        }

        List<Playlist> publicPlaylists = playlistDao.getPublicPlaylists();
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

        model.addAttribute("userPublicPlaylists", userPublicPlaylists);
        model.addAttribute("otherPublicPlaylists", otherPublicPlaylists);

        return "pages/publicPlaylists";
    }

    /**
     * Fetches songs for a specific playlist.
     *
     * @param playlistId The ID of the playlist to fetch songs from.
     * @return A list of maps containing song details (title and artist name).
     * @throws SQLException If there is an issue accessing the database.
     */
    // Fetch songs for a specific playlist
    @GetMapping("/{playlistId}/songs")
    @ResponseBody
    public List<Map<String, String>> getPlaylistSongs(@PathVariable int playlistId) throws SQLException {
        List<Song> songs = songDao.getSongsByPlaylist(playlistId);
        List<Map<String, String>> songDetails = new ArrayList<>();

        for (Song song : songs) {
            Map<String, String> songDetail = new HashMap<>();
            songDetail.put("title", song.getTitle());
            songDetail.put("artist", songDao.getArtistName(song.getArtist_id()));
            songDetails.add(songDetail);
        }

        return songDetails;
    }


//      edited out. cant figure out why search doesnt work since its a copy/paste of search method in RatingController  :(
//    @GetMapping("/playlists/searchPlaylistSongs")
//    public String searchPlaylistSongs(@RequestParam String query, Model model, HttpSession session) throws SQLException {
//        User currentUser = (User) session.getAttribute("user");
//        if (currentUser == null) {
//            return "redirect:/login";
//        }
//
//        // Fetch songs matching the keyword
//        List<Song> searchResults = songDao.searchSongs(query);
//
//        // Add data to the model
//        model.addAttribute("searchResults", searchResults);
//        model.addAttribute("keyword", query);
//
//        return "pages/playlists";
//    }


    /**
     * Adds a song to a specified playlist.
     *
     * @param playlistId The ID of the playlist to add the song to.
     * @param songId     The ID of the song to add.
     * @param session    The HTTP session to fetch the current user.
     * @return A redirect to the playlists page.
     */
    // Add a song to a playlist
    @PostMapping("/addSong")
    public String addSongToPlaylist(@RequestParam int playlistId, @RequestParam int songId, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return "redirect:/login";
        }

        playlistDao.addSongToPlaylist(playlistId, songId);

        return "redirect:/playlists";
    }

    /**
     * Removes a song from a specified playlist.
     *
     * @param playlistId        The ID of the playlist to remove the song from.
     * @param songId            The ID of the song to remove.
     * @param session           The HTTP session to fetch the current user.
     * @param redirectAttributes Redirect attributes to pass success or error messages.
     * @return A redirect to the playlists page.
     */
    // Remove a song from a playlist
    @PostMapping("/{playlistId}/removeSong")
    public String removeSongFromPlaylist(
            @PathVariable int playlistId,
            @RequestParam int songId,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return "redirect:/login";
        }

        boolean success = playlistDao.removeSongFromPlaylist(playlistId, songId);

        if (success) {
            redirectAttributes.addFlashAttribute("message", "Song removed successfully!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Failed to remove the song.");
        }

        return "redirect:/playlists"; // Ensure this redirect is accurate
    }

    /**
     * Renames a playlist.
     *
     * @param playlistId        The ID of the playlist to rename.
     * @param newName           The new name for the playlist.
     * @param session           The HTTP session to fetch the current user.
     * @param redirectAttributes Redirect attributes to pass success or error messages.
     * @return A redirect to the playlists page.
     */
    @PostMapping("/{playlistId}/rename")
    public String renamePlaylist(
            @PathVariable int playlistId,
            @RequestParam String newName,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return "redirect:/login";
        }

        boolean success = playlistDao.renamePlaylist(playlistId, newName);

        if (success) {
            redirectAttributes.addFlashAttribute("message", "Playlist renamed successfully!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Failed to rename the playlist.");
        }

        return "redirect:/playlists";
    }

    /**
     * Displays the details of a specific playlist, including the songs in the playlist.
     *
     * @param playlistId The ID of the playlist to view.
     * @param model      The model to pass data to the view.
     * @param session    The HTTP session to fetch the current user.
     * @return The name of the view template to render.
     * @throws SQLException If there is an issue accessing the database.
     */
    @GetMapping("/details/{playlistId}")
    public String viewPlaylistDetails(@PathVariable int playlistId, Model model, HttpSession session) throws SQLException {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return "redirect:/login";
        }

        Playlist selectedPlaylist = playlistDao.getPlaylistById(playlistId);
        if (selectedPlaylist == null || selectedPlaylist.getUserId() != currentUser.getId()) {
            return "redirect:/playlists"; // Redirect if the playlist does not belong to the user
        }

        List<Song> songs = songDao.getSongsByPlaylist(playlistId);

        // Fetch all playlists for the sidebar
        List<Playlist> userPlaylists = playlistDao.getUserPlaylists(currentUser.getId());

        model.addAttribute("playlists", userPlaylists);
        model.addAttribute("selectedPlaylist", selectedPlaylist);
        model.addAttribute("songs", songs);

        return "pages/playlists"; // Render the same playlists page with the selected playlist's songs
    }
}
