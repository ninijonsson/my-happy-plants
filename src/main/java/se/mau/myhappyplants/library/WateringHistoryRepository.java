package se.mau.myhappyplants.library;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WateringHistoryRepository extends JpaRepository<WateringHistory,Long> {
    List<WateringHistory> findByPlantIdOrderByWateredAtDesc(long plantId);
    List<WateringHistory> findByUserIdOrderByWateredAtDesc(long userId);

}
