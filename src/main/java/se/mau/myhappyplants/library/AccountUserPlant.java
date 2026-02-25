package se.mau.myhappyplants.library;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import se.mau.myhappyplants.user.AccountUser;
import java.time.LocalDate;
import java.time.LocalDateTime;


/**
 * The AccountUserPlant class represents a plant that is associated with a user account.
 * It contains information about the plant such as its name, scientific name, description,
 * watering schedule, and other attributes. The class is annotated as an entity for persistence
 * in a database table named "user_plants".
 *
 * This class supports automatic tracking of creation and update timestamps, along with
 * calculations related to watering schedules.
 *
 * An AccountUserPlant is associated with an AccountUser and may optionally be tagged with a Tag
 * to provide categorization or additional metadata.
 */
@Entity
@Table(name = "user_plants")
public class AccountUserPlant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    //TODO: Ändra till int istället?
    @Column(name = "perenual_id")
    private String perenualId;

    @Column(name = "last_watered")
    private LocalDateTime lastWatered;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    //automatically set the date when the plant is first saved
    @PrePersist
    private void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.lastWatered == null) {
            this.lastWatered = LocalDateTime.now();
        }
        calculateNextWateringDate();
    }
    @PreUpdate
    private void onUpdate() {
        calculateNextWateringDate();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @NotBlank(message = "Växtnamn får inte vara tomt")
    @Column(name = "plant_name", nullable = false)
    private String plantName;

    // ManyToOne: Många växter kan tillhöra en användare
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"userPlants", "password", "role"})
    private AccountUser user;

    // ManyToOne: En växt kan ha EN tagg
    @ManyToOne
    @JoinColumn(name = "tag_id")
    @JsonIgnoreProperties({"accountUserPlants", "userPlants"})
    private Tag tag;

    @Column (name="next_watering_date")
    private LocalDate nextWateringDate;

    @Column (name = "watering_frequency_days")
    private Integer wateringFrequencyDays;

    @Column(name = "image_url", length = 700)
    private String imageUrl;

    @Column(name = "scientific_name")
    private String scientificName;

    @Column(name = "description", length = 5000)
    private String description;


    // Constructors
    public AccountUserPlant() {
    }

    public AccountUserPlant(String plantName, String perenualId) {
        this.plantName = plantName;
        this.perenualId = perenualId;
    }

    // Getters and Setters
    public int getId() {
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
        return wateringFrequencyDays;
    }

    public void setWateringFrequencyDays(Integer wateringFrequencyDays) {
        this.wateringFrequencyDays = wateringFrequencyDays;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDate getNextWateringDate() {
        return nextWateringDate;
    }

    public void setNextWateringDate(LocalDate nextWateringDate) {
        this.nextWateringDate = nextWateringDate;
    }

    /**
     * Calculates and updates the next watering date for the plant based on the last watered date
     * and the watering frequency in days.
     *
     * If both the `lastWatered` and `wateringFrequencyDays` fields are non-null and
     * `wateringFrequencyDays` is greater than zero, the `nextWateringDate` is calculated
     * by adding the watering frequency (in days) to the `lastWatered` date.
     *
     * If any required field is null or `wateringFrequencyDays` is not positive,
     * the method does not calculate or update the `nextWateringDate`.
     */
    public void calculateNextWateringDate() {
        if (lastWatered != null && wateringFrequencyDays != null && wateringFrequencyDays > 0) {
            this.nextWateringDate = lastWatered.toLocalDate().plusDays(wateringFrequencyDays);
        }
    }

    /**
     * Calculates the number of days that have elapsed since the plant was last watered.
     *
     * If the field `lastWatered` is null, it returns 0.
     *
     * @return the number of days since the plant was last watered, or 0 if no watering date is available
     */
    public long getDaysSinceLastWatered() {
        if (lastWatered == null)
            return 0;
        return java.time.Duration.between(lastWatered, java.time.LocalDateTime.now()).toDays();
    }

    public double getDaysUntilNextWatering(){

        if (lastWatered == null || wateringFrequencyDays == null || wateringFrequencyDays <= 0)
            return 0;

        long daysSinceWatered = getDaysSinceLastWatered();
        double percent = (double) daysSinceWatered / wateringFrequencyDays * 100;
        return Math.min(percent, 100);
    }
}
