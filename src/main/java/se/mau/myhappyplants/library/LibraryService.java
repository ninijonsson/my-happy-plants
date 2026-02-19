package se.mau.myhappyplants.library;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.mau.myhappyplants.user.AccountUser;
import se.mau.myhappyplants.user.UserRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Add plant to library, update watering of plant, filter by tag, remove etc.
 * Service for managing a user's plant library.
 * Handles adding/removing plants, watering updates, and tag assignments.
 */
@Service
public class LibraryService {
    @Autowired
    private UserPlantRepository userPlantRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Lägg till en ny växt till användarens bibliotek
     */
    public UserPlant addPlantToLibrary(int userId, String plantName, String perenualId) {
        // Hitta användaren
        AccountUser user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        // Skapa en ny växt
        UserPlant plant = new UserPlant(plantName, perenualId);

        // Koppla växten till användaren
        user.addUserPlant(plant);

        // Spara växten i databasen
        return userPlantRepository.save(plant);
    }

    /**
     * Lägg till eller ändra tagg på en växt
     */
    public UserPlant setTagOnPlant(Long plantId, String tagLabel) {
        // Hitta växten
        UserPlant plant = userPlantRepository.findById(plantId)
                .orElseThrow(() -> new RuntimeException("Plant not found with id: " + plantId));

        // Hitta eller skapa taggen
        Tag tag = tagRepository.findByLabel(tagLabel)
                .orElseGet(() -> tagRepository.save(new Tag(tagLabel)));

        // Sätt taggen på växten
        plant.setTag(tag);

        // Spara växten med den nya taggen
        return userPlantRepository.save(plant);
    }

    /**
     * Ta bort tagg från en växt
     */
    public UserPlant removeTagFromPlant(Long plantId) {
        UserPlant plant = userPlantRepository.findById(plantId)
                .orElseThrow(() -> new RuntimeException("Plant not found with id: " + plantId));

        plant.setTag(null);
        return userPlantRepository.save(plant);
    }

    /**
     * Ta bort en växt från biblioteket
     */
    public void removePlant(Long plantId, Long userId) {
        // Hitta och kolla att den tillhör userId
        UserPlant plant = userPlantRepository.findByIdAndUserId(plantId, userId)
                .orElseThrow(() -> new RuntimeException("Plant not found with id: " + plantId + " for user id: " + userId));

        // Ta bort växten
        userPlantRepository.delete(plant);
    }

    /**
     * Hämta alla växter för en användare
     *
     */
    public List<UserPlant> getAllPlantsForUser(Long userId) {
        return userPlantRepository.findByUserId(userId);
    }

    /**
     * Filtrera växter baserat på tagg
     */
    public List<UserPlant> getPlantsByTag(Long userId, Long tagId) {
        return userPlantRepository.findByUserIdAndTagId(userId, tagId);
    }

    /**
     * Sök växter baserat på namn
     */
    public List<UserPlant> searchPlantsByName(Long userId, String searchTerm) {
        return userPlantRepository.findByUserIdAndPlantNameContainingIgnoreCase(userId, searchTerm);
    }

    /**
     * Hämta växter sorterade alfabetiskt
     */
    public List<UserPlant> getPlantsAlphabetically(Long userId) {
        return userPlantRepository.findByUserIdOrderByPlantNameAsc(userId);
    }

    public List<UserPlant> getPlantsReverseAlphabetically(Long userId) {
        return userPlantRepository.findByUserIdOrderByPlantNameDesc(userId);
    }

    public void waterPlant(Long userId, Long plantId) {
        UserPlant plant = userPlantRepository
                .findByIdAndUserId(plantId, userId)
                .orElseThrow(() ->
                        new RuntimeException("Plant not found for this user"));

        plant.setLastWatered(LocalDateTime.now());

        userPlantRepository.save(plant);
    }

    public long countPlantsNeedingWater(Long userId) {
        List<UserPlant> plants = userPlantRepository.findByUserId(userId);

        LocalDate today = LocalDate.now();
        long count = 0;

        for (UserPlant plant : plants) {

            if (plant.getLastWatered() == null)
                continue;
            if (plant.getWateringFrequencyDays() == null)
                continue;
            LocalDate nextWateringDate = plant.getLastWatered().toLocalDate().plusDays(plant.getWateringFrequencyDays());

            if (!nextWateringDate.isAfter(today)) {
                count++;
            }
        }
        return count;
    }
}
