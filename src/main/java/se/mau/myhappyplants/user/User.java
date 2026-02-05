package se.mau.myhappyplants.user;

import jakarta.persistence.*;
import se.mau.myhappyplants.library.UserPlant;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
        name = "users",
        uniqueConstraints = @UniqueConstraint(name = "uk_users_username", columnNames = "username")
)

/**
 * JPA entity representing an application user.
 * Stores credentials (hashed password) and user-specific preferences/roles.
 */
public class User {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY) // BIGSERIAL i Postgres
        private Long id;

        @Column(name = "username", nullable = false, length = 100)
        private String username;

        @Column(name = "password_hash", nullable = false, columnDefinition = "text")
        private String passwordHash;

        @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
        //TODO: gör UserPlant till entity
        // private Set<UserPlant> plants = new HashSet<>();

        protected void User() { }

        public User() {

        }

        public User(String username, String passwordHash) {
            this.username = username;
            this.passwordHash = passwordHash;
        }

        // getters/setters (lägg in med Lombok?)
        public Long getId() { return id; }
        public String getUsername() { return username; }
        public String getPasswordHash() { return passwordHash; }
        // TODO: public Set<UserPlant> getPlants() { return plants; }

        public void setUsername(String username) { this.username = username; }
        public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    }
}
