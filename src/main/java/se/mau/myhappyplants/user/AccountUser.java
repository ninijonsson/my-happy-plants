package se.mau.myhappyplants.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.jspecify.annotations.Nullable;
import se.mau.myhappyplants.library.UserPlant;
import java.util.ArrayList;
import java.util.List;



/**
 * JPA entity representing an application user.
 * Stores credentials (hashed password) and user-specific preferences/roles.
 */
@Entity
@Table(name = "users")
public class AccountUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank(message = "Username får inte vara tomt")
    @Size(min = 2, max = 50)
    @Column(nullable = false, unique = true)
    private String username;

    @NotBlank(message = "Lösenord får inte vara tomt")
    @Size(min = 12, message = "Lösenord måste vara minst 12 tecken")
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    // OneToMany: En användare kan ha många växter
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<UserPlant> userPlants = new ArrayList<>();
    
    @Nonnull
    private String role;
    
    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }

    public List<UserPlant> getUserPlants() {
        return userPlants;
    }

    public void setUserPlants(List<UserPlant> userPlants) {
        this.userPlants = userPlants;
    }

    // Helper methods för att hantera relationer
    public void addUserPlant(UserPlant userPlant) {
        userPlants.add(userPlant);
        userPlant.setUser(this);
    }

    public void removeUserPlant(UserPlant userPlant) {
        userPlants.remove(userPlant);
        userPlant.setUser(null);
    }

    public @Nullable String getPassword() {
        return passwordHash;
    }
}

