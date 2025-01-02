package ie.groupproject.musicapp.business;

import lombok.*;

/**
 * @author Filip Vojtěch
 */
@AllArgsConstructor
@Data
@Builder
public class User {
    private int id;
    private @NonNull String email;
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private String password;
    private @NonNull String displayName;

    public User(@NonNull String email, String password, @NonNull String displayName) {
        this.email = email;
        this.password = password;
        this.displayName = displayName;
    }
}
