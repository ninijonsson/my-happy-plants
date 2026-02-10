package se.mau.myhappyplants.library;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import se.mau.myhappyplants.user.User;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;


/**
 * Entity connecting User + UserPlant + tag(?)
 * JPA entity representing a plant owned by a user ("My Plants").
 * 
 */
@Entity
@Table(name = "user_plants")
public class UserPlant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "perenual_id")
    private String perenualId;

    @NotBlank(message = "Växtnamn får inte vara tomt")
    @Column(name = "plant_name", nullable = false)
    private String plantName;

    // ManyToOne: Många växter kan tillhöra en användare
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // ManyToOne: En växt kan ha EN tagg
    @ManyToOne
    @JoinColumn(name = "tag_id")
    private Tag tag;

    // Constructors
    public UserPlant() {
    }

    public UserPlant(String plantName, String perenualId) {
        this.plantName = plantName;
        this.perenualId = perenualId;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }
}
