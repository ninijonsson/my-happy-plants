package se.mau.myhappyplants.library;

import jakarta.persistence.*;
import se.mau.myhappyplants.user.AccountUser;

import java.time.LocalDateTime;

@Entity
@Table(name = "water_history")
public class WateringHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private AccountUser user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plant_id", nullable = false)
    private AccountUserPlant plant;

    @Column(name ="watered_at", nullable = false)
    private LocalDateTime wateredAt;

    public WateringHistory() {}

    public WateringHistory(AccountUser user, AccountUserPlant plant, LocalDateTime wateredAt) {
        this.user = user;
        this.plant = plant;
        this.wateredAt = wateredAt;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public AccountUser getUser() {
        return user;
    }

    public void setUser(AccountUser user) {
        this.user = user;
    }

    public AccountUserPlant getPlant() {
        return plant;
    }

    public void setPlant(AccountUserPlant plant) {
        this.plant = plant;
    }

    public LocalDateTime getWateredAt() {
        return wateredAt;
    }

    public void setWateredAt(LocalDateTime wateredAt) {
        this.wateredAt = wateredAt;
    }
}
