package ie.groupproject.musicapp.console;

import ie.groupproject.musicapp.session.Session;

/**
 * @author Filip Vojtěch
 * @author Dylan Habis
 * @author Alex Clinton
 */
public class GeneralInterface extends TextInterface {
    @Override
    public void listOptions() {
        System.out.println("Logged in as " + Session.getUser().getDisplayName());
        System.out.println("1. Playlist");
        System.out.println("2. Rating");
        System.out.println("3. Artist");
        System.out.println("4. Album");
        System.out.println("5. Song");
        System.out.println("0. Logout");


    }

    @Override
    public void handleCommand(String choice) {
        switch (choice) {
            case "0" -> logout();
            case "1" -> setNextInterface(InterfaceType.PlaylistController);
            case "2" -> setNextInterface(InterfaceType.RatingController);
            case "3" -> setNextInterface(InterfaceType.ArtistController);
            case "4" -> setNextInterface(InterfaceType.AlbumController);
            case "5" -> setNextInterface(InterfaceType.SongController);
            default -> {
                System.out.println("Invalid choice. Please try again.");
            setNextInterface(InterfaceType.General);
            }
        }
    }
}
