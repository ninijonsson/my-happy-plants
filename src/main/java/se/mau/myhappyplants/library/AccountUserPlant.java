package se.mau.myhappyplants.library;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import se.mau.myhappyplants.user.AccountUser;
import java.time.LocalDate;
import java.time.LocalDateTime;



/**
 * Entity connecting User + AccountUserPlant + tag(?)
 * JPA entity representing a plant owned by a user ("My Plants").
 *
 */
@Entity
@Table(name = "user_plants")
public class AccountUserPlant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "perenual_id")
    private String perenualId;

    @Column(name = "last_watered")
    private LocalDateTime lastWatered;

    @NotBlank(message = "Växtnamn får inte vara tomt")
    @Column(name = "plant_name", nullable = false)
    private String plantName;

    // ManyToOne: Många växter kan tillhöra en användare
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private AccountUser user;

    // ManyToOne: En växt kan ha EN tagg
    @ManyToOne
    @JoinColumn(name = "tag_id")
    private Tag tag;

    @Column (name="next_watering_date")
    private LocalDate nextWateringDate;

    @Column (name = "watering_frequency_date")
    private Integer wateringFrequencyDate;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "scientific_name")
    private String scientificName;
    
    
    // Constructors
    public AccountUserPlant() {
    }

    public AccountUserPlant(String plantName, String perenualId) {
        this.plantName = plantName;
        this.perenualId = perenualId;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPerenualId() {
        return perenualId;
    }

    public void setPerenualId(String perenualId) {
        this.perenualId = perenualId;
    }

    public String getPlantName() {
        return plantName;
    }

    public void setPlantName(String plantName) {
        this.plantName = plantName;
    }

    public AccountUser getUser() {
        return user;
    }

    public void setUser(AccountUser user) {
        this.user = user;
    }

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }

    public void setLastWatered(LocalDateTime lastWatered) {
        this.lastWatered = lastWatered;
    }

    public LocalDateTime getLastWatered() {
        return lastWatered;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getScientificName() {
        return scientificName;
    }

    public void setScientificName(String scientificName) {
        this.scientificName = scientificName;
    }

    public Integer getWateringFrequencyDays() {
        return wateringFrequencyDate;
    }

    public void setWateringFrequencyDays(Integer wateringFrequencyDays) {
        this.wateringFrequencyDate = wateringFrequencyDays;
    }

    public double getWateringProgressPercentage() {
        if (lastWatered == null || wateringFrequencyDate <= 0) {
            return 0;
        }
        long daysSinceWatered = java.time.Duration.between(lastWatered, java.time.LocalDateTime.now())
                .toDays();

        double percent = (double) daysSinceWatered / wateringFrequencyDate * 100;
        return Math.min(percent, 100);
    }

    public long getDaysSinceLastWatered() {
        if (lastWatered == null)
            return 0;
        return java.time.Duration.between(lastWatered, java.time.LocalDateTime.now()).toDays();
    }

    public double getDaysUntilNextWatering(){
        if (lastWatered == null || wateringFrequencyDate <= 0)
            return 0;

        long daysSinceWatered = getDaysSinceLastWatered();
        double percent = (double) daysSinceWatered / wateringFrequencyDate * 100;
        return Math.min(percent, 100);
    }
}
