package ie.groupproject.musicapp.business;

import lombok.*;
/**
 * @author Dylan Habis
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class Song {

    /**
     * The title of the song.
     */
    private String title;

    /**
     * The unique ID of the artist who performed the song.
     */
    private int artist_id;

    /**
     * The unique ID of the album the song belongs to.
     */
    private int albumId;

    /**
     * The rating of the song given by users.
     */
    private int rating;

    /**
     * Unique identifier for the song.
     */
    private int id;

    /**
     * The number of times the song has been added to playlists.
     */
    private int playlistCount;

    /**
     * The average rating of the song.
     */
    private Double averageRating;


}
