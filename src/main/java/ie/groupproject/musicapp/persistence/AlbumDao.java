package ie.groupproject.musicapp.persistence;

import ie.groupproject.musicapp.business.Album;

import java.sql.SQLException;
import java.util.List;

/**
 * @author Dylan Habis
 */
public interface AlbumDao {
    List<Album> getAlbumsByArtist(int artistId) throws SQLException;

}
