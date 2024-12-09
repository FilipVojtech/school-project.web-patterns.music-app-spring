package ie.groupproject.musicapp.console;

import ie.groupproject.musicapp.business.Song;
import ie.groupproject.musicapp.persistence.SongDao;
import ie.groupproject.musicapp.util.Input;

import java.util.List;

public class SongController extends TextInterface {

    private SongDao songDao;

    public SongController() {
        super();
    }
    @Override
    public void listOptions() {
        System.out.println("1. View songs by album");
        System.out.println("2. Search for songs by keyword");
        System.out.println("3. Exit");
    }

    @Override
    public void handleCommand(String choice) {
        switch (choice) {
            case "1" -> viewSongsByAlbum();
            case "2" -> searchSongs();
            case "3" -> System.out.println("Exiting");
            default -> System.out.println("Invalid choice, please try again.");
        }
    }

    private void viewSongsByAlbum() {
        try {
            System.out.print("Enter album ID: ");
            int albumId = Integer.parseInt(Input.command());

            List<Song> songs = songDao.getSongsByAlbum(albumId);
            if (songs.isEmpty()) {
                System.out.println("No songs found for this album.");
            } else {
                System.out.println("Songs in album ID " + albumId + ":");
                songs.forEach(song -> System.out.println(" - " + song.getTitle()));
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid album ID. Please enter a valid number.");
        } catch (Exception e) {
            System.out.println("Error retrieving songs: " + e.getMessage());
        }
    }

    private void searchSongs() {
        try {
            System.out.print("Enter search keyword: ");
            String keyword = Input.command();

            List<Song> songs = songDao.searchSongs(keyword);
            if (songs.isEmpty()) {
                System.out.println("No songs found matching the keyword \"" + keyword + "\".");
            } else {
                System.out.println("Songs matching \"" + keyword + "\":");
                songs.forEach(song -> System.out.println(" - " + song.getTitle()));
            }
        } catch (Exception e) {
            System.out.println("Error searching for songs: " + e.getMessage());
        }
    }
}
