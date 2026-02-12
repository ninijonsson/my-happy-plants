package se.mau.myhappyplants.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * JPA repository for database calls about User
 * Data access layer for User entities.
 * Provides queries used by authentication and user management.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Hitta användare baserat på username
     *
     */
    Optional<User> findByUsername(String username);

    /**
     * Kolla om ett username redan finns i databasen
     *
     */
    boolean existsByUsername(String username);
}
