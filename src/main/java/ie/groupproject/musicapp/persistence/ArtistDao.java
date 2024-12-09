package ie.groupproject.musicapp.persistence;


import ie.groupproject.musicapp.business.Artist;

import java.sql.SQLException;
import java.util.List;

/**
 * @author Dylan Habis
 */
public interface ArtistDao {
    List<Artist> getAllArtists() throws SQLException;

}
