package ie.groupproject.musicapp.controllers;

import ie.groupproject.musicapp.business.Album;
import ie.groupproject.musicapp.business.Artist;
import ie.groupproject.musicapp.business.Song;
import ie.groupproject.musicapp.business.User;
import ie.groupproject.musicapp.persistence.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.SQLException;
import java.util.List;

@Controller
public class SongController {

    private final SongDao songDao;
    private final ArtistDao artistDao;
    private final AlbumDao albumDao;

    // Constructor
    public SongController() {
        this.songDao = new SongDaoImpl("database.properties");
        this.artistDao = new ArtistDaoImpl("database.properties");
        this.albumDao = new AlbumDaoImpl("database.properties");
    }

    @GetMapping("/viewMusic")
    public String viewMusic(@RequestParam String query, Model model, HttpSession session) throws SQLException {
        List<Artist> artists = artistDao.getAllArtists();
        List<Album> albums = albumDao.getAlbumsByArtist(0); // Fetch all albums
        List<Song> songs = songDao.searchSongs(query != null ? query : "");

        model.addAttribute("artists", artists);
        model.addAttribute("albums", albums);
        model.addAttribute("songs", songs);
        model.addAttribute("query", query);
        return "pages/songs";
    }

    /**
     * View albums by a specific artist.
     */
    @GetMapping("/artists/{artistId}")
    public String viewAlbumsByArtist(@PathVariable int artistId, Model model) throws SQLException {
        List<Album> albums = albumDao.getAlbumsByArtist(artistId);

        Artist artist = null;
        for (Artist a : artistDao.getAllArtists()) {
            if (a.getId() == artistId) {
                artist = a;
                break;
            }
        }

        model.addAttribute("artistAlbums", albums);
        model.addAttribute("selectedArtistName", artist != null ? artist.getName() : "Unknown Artist");
        return "pages/songs";
    }


    @GetMapping("/albums/{albumId}")
    public String viewSongsByAlbum(@PathVariable int albumId, Model model) throws SQLException {
        List<Song> songs = songDao.getSongsByAlbum(albumId);

        Album album = null;
        for (Album a : albumDao.getAlbumsByArtist(0)) {
            if (a.getId() == albumId) {
                album = a;
                break;
            }
        }

        model.addAttribute("songs", songs);
        model.addAttribute("albumName", album != null ? album.getTitle() : "Unknown Album");
        return "music-view";
    }

}

