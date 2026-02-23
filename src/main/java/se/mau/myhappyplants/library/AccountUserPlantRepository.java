package se.mau.myhappyplants.library;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


/**
 * Database queries to get user's plants
 * Data access layer for AccountUserPlant entities.
 * Provides queries to list and manage a user's plant collection.
 */
@Repository
public interface AccountUserPlantRepository extends JpaRepository<AccountUserPlant, Integer> {
    /**
     * Hitta alla växter som tillhör en specifik användare
     */
    List<AccountUserPlant> findByUserId(int userId);


    /**
     * Hitta en specifik växt för en användare
     */
    Optional<AccountUserPlant> findByIdAndUserId(int id, int userId);
    
    /**
     * Hitta växter baserat på namn (för en specifik användare)
     */
    List<AccountUserPlant> findByUserIdAndPlantNameContainingIgnoreCase(int userId, String plantName);

    /**
     * Hitta växter med en specifik tagg
     */
    List<AccountUserPlant> findByUserIdAndTagId(int userId, int tagId);

    /**
     * Räkna antal växter för en användare
     */
    long countByUserId(int userId);

    /**
     * Hitta växter baserat på Perenual API ID
     */
    List<AccountUserPlant> findByPerenualId(String perenualId);

    /**
     * Hämta växter sorterade alfabetiskt (A-Z)
     */
    List<AccountUserPlant> findByUserIdOrderByPlantNameAsc(int userId);

    /**
     * Hämta växter sorterade omvänt alfabetiskt (Z-A)
     */
    //List<AccountUserPlant> findByUserIdOrderByPlantNameDesc(Long userId);

    //TODO: se om de två metoderna ovan fortfarande behövs, denna är mer generisk
    List<AccountUserPlant> findByUserId(int userId, Sort sort);

    List<AccountUserPlant> findByUserIdOrderByPlantNameDesc(int userId);
}
