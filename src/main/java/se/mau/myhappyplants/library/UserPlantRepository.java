package se.mau.myhappyplants.library;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


/**
 * Database queries to get user's plants
 * Data access layer for UserPlant entities.
 * Provides queries to list and manage a user's plant collection.
 */
@Repository
public interface UserPlantRepository extends JpaRepository<UserPlant, Long> {
    /**
     * Hitta alla växter som tillhör en specifik användare
     */
    List<UserPlant> findByUserId(Long userId);

    /**
     * Hitta en specifik växt för en användare
     */
    Optional<UserPlant> findByIdAndUserId(Long id, Long userId);

    /**
     * Hitta växter baserat på namn (för en specifik användare)
     */
    List<UserPlant> findByUserIdAndPlantNameContainingIgnoreCase(Long userId, String plantName);

    /**
     * Hitta växter med en specifik tagg
     */
    List<UserPlant> findByUserIdAndTagId(Long userId, Long tagId);

    /**
     * Räkna antal växter för en användare
     */
    long countByUserId(Long userId);

    /**
     * Hitta växter baserat på Perenual API ID
     */
    List<UserPlant> findByPerenualId(String perenualId);

    /**
     * Hämta växter sorterade alfabetiskt (A-Z)
     */
    List<UserPlant> findByUserIdOrderByPlantNameAsc(Long userId);

    /**
     * Hämta växter sorterade omvänt alfabetiskt (Z-A)
     */
    List<UserPlant> findByUserIdOrderByPlantNameDesc(Long userId);
}
