package se.mau.myhappyplants.library;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for managing WateringHistory entities.
 *
 * Provides database access for retrieving and storing watering history records.
 *
 * Custom query methods are used to fetch watering history:
 * - By plant: to view watering history for a specific plant
 * - By user: to retrieve all watering activity for a user (e.g., for statistics or graphs)
 */

@Repository
public interface WateringHistoryRepository extends JpaRepository<WateringHistory,Long> {
    List<WateringHistory> findByPlantIdOrderByWateredAtDesc(long plantId);
    List<WateringHistory> findByUserIdOrderByWateredAtDesc(long userId);

}
