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
public class Album {

    /**
     * Unique identifier for the album
     */
    private int id;

    /**
     * The title of the album.
     */
    private String title;

    /**
     * The ID of the artist who created the album.
     */
    private int artist_Id;

}

