package ie.groupproject.musicapp.persistence;

import ie.groupproject.musicapp.business.Playlist;
import ie.groupproject.musicapp.business.Song;

import java.util.List;


/**
 * Interface defining the data access methods for Playlist operations.
 *
 * @author Alex Clinton
 */
public interface PlaylistDao {

    /**
     * Creates a new playlist in the database.
     *
     * @param playlist the Playlist object containing playlist details
     * @return true if the playlist was created successfully, false otherwise
     */
    boolean createPlaylist(Playlist playlist);

    /**
     * Updates an existing playlist's details.
     *
     * @param playlist the Playlist object containing updated details
     * @return true if the playlist was updated successfully, false otherwise
     */
    boolean updatePlaylist(Playlist playlist);

    /**
     * Adds a song to a playlist.
     *
     * @param playlistId the ID of the playlist
     * @param songId     the ID of the song to add
     * @return true if the song was added successfully, false otherwise
     */
    boolean addSongToPlaylist(int playlistId, int songId);

    /**
     * Removes a song from a playlist.
     *
     * @param playlistId the ID of the playlist
     * @param songId     the ID of the song to remove
     * @return true if the song was removed successfully, false otherwise
     */
    boolean removeSongFromPlaylist(int playlistId, int songId);

//    /**
//     * Renames a playlist.
//     *
//     * @param playlistId the ID of the playlist
//     * @param newName    the new name for the playlist
//     * @return true if the playlist was renamed successfully, false otherwise
//     */
//    boolean renamePlaylist(int playlistId, String newName);

    /**
     * Retrieves a playlist by its ID.
     *
     * @param playlistId the ID of the playlist
     * @return the Playlist object if found, null otherwise
     */
    Playlist getPlaylistById(int playlistId);

    boolean renamePlaylist(int playlistId, String newName);

    /**
     * Retrieves all playlists belonging to a user.
     *
     * @param userId the ID of the user
     * @return a list of the user's playlists
     */
    List<Playlist> getUserPlaylists(int userId);

    /**
     * Retrieves all public playlists.
     *
     * @return a list of public playlists
     */
    List<Playlist> getPublicPlaylists();

    /**
     * Retrieves all songs in a playlist.
     *
     * @param playlistId the ID of the playlist
     * @return a list of songs in the playlist
     */
    List<Song> getSongsInPlaylist(int playlistId);
}
