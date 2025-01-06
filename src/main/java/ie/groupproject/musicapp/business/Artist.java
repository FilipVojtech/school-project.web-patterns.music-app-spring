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
public class Artist {

    /**
     * Unique identifier for the artist.
     */
    private int id;


    /**
     * The name of the artist.
     */
    private String name;

}

