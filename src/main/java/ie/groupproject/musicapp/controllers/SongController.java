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


    /**
     * Handles the request to search and view music (songs, albums, artists) based on a query string
     * @param query The search query string provided by the user
     * @param model The Spring model to which the results are added for rendering the view
     * @param session The current HTTP session
     * @return The name of the view template to render (in this case, "pages/songs")
     * @throws SQLException If there is a database error while fetching the data
     */

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
     * Handles the request to view albums by a specific artist identified by their artist ID
     * @param artistId The ID of the artist whose albums are to be displayed
     * @param model The Spring model to which the album data is added for rendering the view
     * @return The name of the view template to render
     * @throws SQLException If there is a database error while fetching the data
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


    /**
     * Handles the request to view songs within a specific album identified by its album ID
     * @param albumId The ID of the album whose songs are to be displayed
     * @param model The Spring model to which the song data is added for rendering the view
     * @return The name of the view template to render
     * @throws SQLException If there is a database error while fetching the data
     */
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
        return "pages/songs";
    }

}

