package se.mau.myhappyplants.library;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.*;

/**
 * JPA entity for user-defined tags (e.g., "vardagsrum").
 * Tags can be associated with multiple user plants.
 */
@Entity
@Table(name = "tags")
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank(message = "Tag-namn får inte vara tomt")
    @Size(min = 1, max = 50)
    @Column(nullable = false, unique = true)
    private String label;

    // OneToMany: En tagg kan användas på många växter
    @OneToMany(mappedBy = "tag")
    @JsonIgnore
    private List<AccountUserPlant> accountUserPlants = new ArrayList<>();

    // Constructors
    public Tag() {
    }

    public Tag(String label) {
        this.label = label;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public boolean setLabel(String label) {
        if(label.isBlank()) return false;
        this.label = label;
        return true;
    }

    public List<AccountUserPlant> getUserPlants() {
        return accountUserPlants;
    }

    public void setUserPlants(List<AccountUserPlant> accountUserPlants) {
        this.accountUserPlants = accountUserPlants;
    }
}
