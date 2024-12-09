package ie.groupproject.musicapp.business;
/**
 * @author Alex Clinton
 */
import lombok.*;

/**
 * Represents a rating given by a user to a song.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)

public class Rating {
    /**
     *  unique identifier for the rating.
     */
    @EqualsAndHashCode.Include
    private int ratingId;

    /**
     *  ID of the user who gave the rating.
     */
    private int userId;

    /**
     *  song ID of the song being rated.
     */
    private int songId;

    /**
     *  rating value (1 to 5).
     */
    private int ratingValue;
}
