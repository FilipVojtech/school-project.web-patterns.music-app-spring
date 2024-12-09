package ie.groupproject.musicapp.persistence;

import ie.groupproject.musicapp.business.Song;

import java.sql.SQLException;
import java.util.List;

/**
 * @author Dylan Habis
 */
public interface SongDao {
    List<Song> getSongsByAlbum(int albumId) throws SQLException;

    List<Song> searchSongs(String keyword) throws SQLException;

}
