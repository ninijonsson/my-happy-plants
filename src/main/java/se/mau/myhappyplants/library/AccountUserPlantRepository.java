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
     * Retrieves a list of AccountUserPlant objects associated with a specific user,
     * sorted according to the provided sorting criteria.
     *
     * @param userId the unique identifier of the user whose plants are being retrieved
     * @param sort the sorting parameters to apply to the retrieved list
     * @return a list of AccountUserPlant objects associated with the provided user ID, sorted as specified
     */
    List<AccountUserPlant> findByUserId(int userId, Sort sort);

    /**
     * Retrieves a list of AccountUserPlant objects associated with a specific user,
     * ordered in descending order by the plant name.
     *
     * @param userId the unique identifier of the user whose plants are being retrieved
     * @return a list of AccountUserPlant objects sorted by plant name in descending order, associated with the specified user
     */
    List<AccountUserPlant> findByUserIdOrderByPlantNameDesc(int userId);
}
