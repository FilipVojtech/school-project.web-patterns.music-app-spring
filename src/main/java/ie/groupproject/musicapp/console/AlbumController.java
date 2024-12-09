package ie.groupproject.musicapp.console;

import ie.groupproject.musicapp.business.Album;
import ie.groupproject.musicapp.persistence.AlbumDao;
import ie.groupproject.musicapp.util.Input;

import java.sql.SQLException;
import java.util.List;

public class AlbumController extends TextInterface {

    private AlbumDao albumDao;

    public AlbumController() {
        super();
    }

    @Override
    public void listOptions() {
        System.out.println("1. View all albums by artist");
        System.out.println("2. Exit");
    }

    @Override
    public void handleCommand(String choice) {
        switch (choice) {
            case "1" -> viewAlbumsByArtist();
            case "2" -> System.out.println("Exiting");
            default -> System.out.println("Invalid choice, please try again.");
        }
    }

    private void viewAlbumsByArtist() {
        try {
            System.out.print("Enter artist ID: ");
            int artistId = Integer.parseInt(Input.command());

            List<Album> albums = albumDao.getAlbumsByArtist(artistId);
            if (albums.isEmpty()) {
                System.out.println("No albums found for this artist");
            } else {
                System.out.println("Albums by artist ID " + artistId + ":");
                albums.forEach(album -> System.out.println(" - " + album.getTitle()));
            }
        } catch (SQLException e) {
            System.out.println("Database error:" + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Invalid artist ID. Please enter a valid number");
        }
    }

}

