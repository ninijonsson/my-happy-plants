package se.mau.myhappyplants.library;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


/**
 * Database queries to get user's plants
 * Data access layer for UserPlant entities.
 * Provides queries to list and manage a user's plant collection.
 */
@Repository
public interface UserPlantRepository extends JpaRepository<UserPlant, Integer> {
    /**
     * Hitta alla växter som tillhör en specifik användare
     */
    List<UserPlant> findByUserId(int userId);

    /**
     * Hitta en specifik växt för en användare
     */
    Optional<UserPlant> findByIdAndUserId(int id, int userId);

    /**
     * Hitta växter baserat på namn (för en specifik användare)
     */
    List<UserPlant> findByUserIdAndPlantNameContainingIgnoreCase(int userId, String plantName);

    /**
     * Hitta växter med en specifik tagg
     */
    List<UserPlant> findByUserIdAndTagId(int userId, int tagId);

    /**
     * Räkna antal växter för en användare
     */
    long countByUserId(int userId);

    /**
     * Hitta växter baserat på Perenual API ID
     */
    List<UserPlant> findByPerenualId(String perenualId);

    /**
     * Hämta växter sorterade alfabetiskt (A-Z)
     */
    List<UserPlant> findByUserIdOrderByPlantNameAsc(int userId);

    /**
     * Hämta växter sorterade omvänt alfabetiskt (Z-A)
     */
    List<UserPlant> findByUserIdOrderByPlantNameDesc(int userId);
}
