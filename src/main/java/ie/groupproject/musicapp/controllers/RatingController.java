package ie.groupproject.musicapp.controllers;

import ie.groupproject.musicapp.business.Rating;
import ie.groupproject.musicapp.business.Song;
import ie.groupproject.musicapp.business.User;
import ie.groupproject.musicapp.persistence.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.SQLException;
import java.util.List;

@Controller
public class RatingController {

    private final SongDao songDao;
    private final RatingDao ratingDao;

    public RatingController() {

        this.songDao = new SongDaoImpl("database.properties");
        this.ratingDao = new RatingDaoImpl("database.properties");
    }

    @GetMapping("/rating")
    public String showRatingPage(Model model, HttpSession session) throws SQLException {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return "redirect:/login"; // Ensure the user is logged in
        }


        // Fetch top-rated song
        Song topRatedSong = ratingDao.getTopRatedSong();
        Song mostPopularSong = ratingDao.getMostPopularSong();

        model.addAttribute("topRatedSong", topRatedSong);
        model.addAttribute("mostPopularSong", mostPopularSong);


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

        // Fetch top-rated song and most popular song to keep them displayed
        Song topRatedSong = ratingDao.getTopRatedSong();
        Song mostPopularSong = ratingDao.getMostPopularSong();

        // Add data to the model
        model.addAttribute("searchResults", searchResults);
        model.addAttribute("keyword", query);
        model.addAttribute("topRatedSong", topRatedSong);
        model.addAttribute("mostPopularSong", mostPopularSong);

        return "pages/rating";
    }

    @PostMapping("/rate")
    public String rateSongs(@RequestParam int songId, int rating, Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return "redirect:/login";
        }

        if (rating <1 || rating >5) {
            model.addAttribute("Invalid rating. Enter number between 1 & 5");
            return "pages/rating";
        }

        Rating userRating = Rating.builder()
                .userId(currentUser.getId())
                .songId(songId)
                .ratingValue(rating)
                .build();

        boolean success = ratingDao.rateSong(userRating);
        if (!success) {
            model.addAttribute("Failed to rate song");
        } else {
            model.addAttribute("success");
        }
        return "redirect:/rating";
    }

    @GetMapping("/mostPopularSong")
    @ResponseBody
    public Song getMostPopularSong() {
        return ratingDao.getMostPopularSong();
    }
}