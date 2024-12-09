package ie.groupproject.musicapp.console;

import ie.groupproject.musicapp.business.Artist;
import ie.groupproject.musicapp.persistence.ArtistDao;


import java.util.List;

public class ArtistController extends TextInterface {
    private ArtistDao ArtistDao;

    public ArtistController() {
        super();
    }

    @Override
    public void listOptions() {
        System.out.println("1. View all artists");
        System.out.println("2. Exit");
    }

    @Override
    public void handleCommand(String choice) {
        switch (choice) {
            case "1" -> viewAllArtists();
            case "2" -> System.out.println("Exiting");
            default -> System.out.println("Invalid choice, please try again.");
        }
    }

    private void viewAllArtists() {
        try {
            List<Artist> artists = ArtistDao.getAllArtists();

            if (artists.isEmpty()) {
                System.out.println("No artists found.");
            } else {
                System.out.println("Artists:");
                artists.forEach(artist -> System.out.println(" - " + artist.getName()));
            }
        } catch (Exception e) {
            System.out.println("Error retrieving artists: " + e.getMessage());
        }
    }
}
