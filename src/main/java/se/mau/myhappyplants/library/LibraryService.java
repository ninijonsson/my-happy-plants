package se.mau.myhappyplants.library;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import se.mau.myhappyplants.user.User;
import se.mau.myhappyplants.user.AccountUser;
import se.mau.myhappyplants.user.UserRepository;

import java.util.List;

/**
 * Add plant to library, update watering of plant, filter by tag, remove etc.
 * Service for managing a user's plant library.
 * Handles adding/removing plants, watering updates, and tag assignments.
 */
@Service
public class LibraryService {
    @Autowired
    private final UserPlantRepository userPlantRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private UserRepository userRepository;

    public LibraryService(UserPlantRepository userPlantRepository) {
        this.userPlantRepository = userPlantRepository;
    }

    /**
     * Method for sorting in Ascending and Descending order
     * @param userId is the id of the relevant user
     * @param sortDir is the sorting direction
     * @return the way it is supposed to be sorted based on the user
     */
    public List<UserPlant> getUserLibrary(Long userId, String sortDir) {
        Sort sort;
        String plantName = "plantName";

        if ("desc".equalsIgnoreCase(sortDir)) {
            // Z - A sorting
            sort = Sort.by(Sort.Direction.DESC, plantName);
        } else {
            // A - Z sorting
            sort = Sort.by(Sort.Direction.ASC, plantName);
        }
        return userPlantRepository.findByUserId(userId, sort);
    }

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


    //TODO se över om dessa fortfarande behövs när sorting fungerar
    /**
     * Hämta växter sorterade alfabetiskt
     */
//    public List<UserPlant> getPlantsAlphabetically(Long userId) {
//        return userPlantRepository.findByUserIdOrderByPlantNameAsc(userId);
//    }
//
//    public List<UserPlant> getPlantsReverseAlphabetically(Long userId) {
//        return userPlantRepository.findByUserIdOrderByPlantNameDesc(userId);
//    }
    //kolla vad som ska behållas och inte
}
