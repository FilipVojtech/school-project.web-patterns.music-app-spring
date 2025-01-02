package ie.groupproject.musicapp.controllers;

import ie.groupproject.musicapp.business.Song;
import ie.groupproject.musicapp.business.User;
import ie.groupproject.musicapp.persistence.PlaylistDaoImpl;
import ie.groupproject.musicapp.persistence.SongDao;
import ie.groupproject.musicapp.persistence.SongDaoImpl;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.SQLException;
import java.util.List;

@Controller
public class RatingController {

    private final SongDao songDao;

    public RatingController() {
        this.songDao = new SongDaoImpl("database.properties");
    }

    @GetMapping("/rating")
    public String getFirstSongName(Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return "redirect:/login"; // Ensure the user is logged in
        }

        // Fetch the first song name
        String songName = songDao.getFirstSongName();
        model.addAttribute("songName", songName); // Add it to the model
        return "pages/rating"; // Render the "rating.html" page
    }

    @GetMapping("/searchSongs")
    public String searchSongs(@RequestParam String query, Model model, HttpSession session) throws SQLException {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return "redirect:/login";
        }

        // Fetch songs matching the keyword
        List<Song> searchResults = songDao.searchSongs(query);

        // Add data to the model
        model.addAttribute("searchResults", searchResults);
        model.addAttribute("keyword", query);

        return "pages/rating"; // Ensure this matches your template
    }

}