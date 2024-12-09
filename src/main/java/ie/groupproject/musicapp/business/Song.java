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
    private String title;
    private int artist_id;
    private int albumId;
    private int rating;
    private int id;
    private int playlistCount;
    private Double averageRating;


}
