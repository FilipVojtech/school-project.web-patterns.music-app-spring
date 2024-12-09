package ie.groupproject.musicapp.business;
/**
 * @author Alex Clinton
 */
import lombok.*;

import java.util.List;

/**
 * Represents a playlist created by a user.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)

public class Playlist {
    /**
     *  unique identifier for the playlist.
     */
    @EqualsAndHashCode.Include
    private int playlistId;

    /**
     *  name of the playlist.
     */
    private String name;

    /**
     * Indicates if playlist is public or private.
     */
    private boolean isPublic;

    /**
     * The user ID of the playlist owner.
     */
    private int userId;

    /**
     * The list of songs included in the playlist.
     */
    private List<Song> songs;
}
