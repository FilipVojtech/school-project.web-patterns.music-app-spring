package ie.groupproject.musicapp.controllers;

import ie.groupproject.musicapp.business.User;
import ie.groupproject.musicapp.persistence.PlaylistDaoImpl;
import ie.groupproject.musicapp.persistence.SongDao;
import ie.groupproject.musicapp.persistence.SongDaoImpl;
import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

public class RatingController {

    private final SongDao songDao;

    public RatingController(SongDao songDao) {
        this.songDao = new SongDaoImpl("database.properties");
    }
    @GetMapping("/firstSong")
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

}
