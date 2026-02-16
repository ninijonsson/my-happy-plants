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
public interface UserRepository extends JpaRepository<AccountUser, Integer> {
    /**
     * Hitta användare baserat på username
     */
    Optional<AccountUser> findByUsername(String username);
    
    Optional<AccountUser> findById(int id);
}
