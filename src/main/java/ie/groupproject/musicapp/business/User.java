package ie.groupproject.musicapp.business;

import lombok.*;

import java.time.LocalDate;

/**
 * @author Filip Vojtěch
 */
@AllArgsConstructor
@Data
@Builder
public class User {
    private int id;
    private @NonNull String displayName;
    private @NonNull String email;
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private String password;
    private LocalDate subscriptionSince;
    private int subscriptionForDays;

    public User(@NonNull String email, String password, @NonNull String displayName, LocalDate subscriptionSince, int subscriptionForDays) {
        this.email = email;
        this.password = password;
        this.displayName = displayName;
        this.subscriptionSince = subscriptionSince;
        this.subscriptionForDays = subscriptionForDays;
    }

    /**
     * Copy constructor
     */
    public User(User original) {
        this(original.getId(), original.getEmail(), original.getPassword(), original.getDisplayName(), original.getSubscriptionSince(), original.getSubscriptionForDays());
    }

    public LocalDate getSubscriptionEnd() {
        return subscriptionSince.plusDays(subscriptionForDays);
    }
}
