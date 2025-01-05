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
    private LocalDate subscriptionEnd;

    public User(@NonNull String email, String password, @NonNull String displayName, LocalDate subscriptionSince, LocalDate subscriptionEnd) {
        this.email = email;
        this.password = password;
        this.displayName = displayName;
        this.subscriptionSince = subscriptionSince;
        this.subscriptionEnd = subscriptionEnd;
    }

    /**
     * Copy constructor
     */
    public User(User original) {
        this(original.getId(), original.getEmail(), original.getPassword(), original.getDisplayName(), original.getSubscriptionSince(), original.getSubscriptionEnd());
    }

    /**
     * Extends the user subscription by one year.
     */
    public void extendSubscription() {
        subscriptionEnd = subscriptionEnd.plusMonths(12);
    }
}
