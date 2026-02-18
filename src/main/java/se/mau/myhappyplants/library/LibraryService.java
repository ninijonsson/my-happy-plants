package se.mau.myhappyplants.library;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
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
    private final AccountUserPlantRepository accountUserPlantRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private UserRepository userRepository;

    public LibraryService(AccountUserPlantRepository accountUserPlantRepository) {
        this.accountUserPlantRepository = accountUserPlantRepository;
    }

    /**
     * Method for sorting in Ascending and Descending order
     * @param userId is the id of the relevant user
     * @param sortDir is the sorting direction
     * @return the way it is supposed to be sorted based on the user
     */
    public List<AccountUserPlant> getUserLibrary(int userId, String sortDir) {
        Sort sort;
        String plantName = "plantName";

        if ("desc".equalsIgnoreCase(sortDir)) {
            // Z - A sorting
            sort = Sort.by(Sort.Direction.DESC, plantName);
        } else if ("water".equalsIgnoreCase(sortDir)) {
            //sort by water status
            //sorting by asc should put the oldest dates at the top
            sort = Sort.by(Sort.Direction.ASC, "nextWateringDate");
        } else {
            // A - Z sorting
            //default sorting
            sort = Sort.by(Sort.Direction.ASC, plantName);
            }
        return accountUserPlantRepository.findByUserId(userId, sort);
    }

    /**
     * Lägg till en ny växt till användarens bibliotek
     */
    public AccountUserPlant addPlantToLibrary(int userId, String plantName, String perenualId) {
        // Hitta användaren
        AccountUser user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        // Skapa en ny växt
        AccountUserPlant plant = new AccountUserPlant(plantName, perenualId);

        // Koppla växten till användaren
        user.addUserPlant(plant);

        // Spara växten i databasen
        return accountUserPlantRepository.save(plant);
    }

    /**
     * Lägg till eller ändra tagg på en växt
     */
    public AccountUserPlant setTagOnPlant(Long plantId, String tagLabel) {
        // Hitta växten
        AccountUserPlant plant = accountUserPlantRepository.findById(plantId)
                .orElseThrow(() -> new RuntimeException("Plant not found with id: " + plantId));

        // Hitta eller skapa taggen
        Tag tag = tagRepository.findByLabel(tagLabel)
                .orElseGet(() -> tagRepository.save(new Tag(tagLabel)));

        // Sätt taggen på växten
        plant.setTag(tag);

        // Spara växten med den nya taggen
        return accountUserPlantRepository.save(plant);
    }

    /**
     * Ta bort tagg från en växt
     */
    public AccountUserPlant removeTagFromPlant(Long plantId) {
        AccountUserPlant plant = accountUserPlantRepository.findById(plantId)
                .orElseThrow(() -> new RuntimeException("Plant not found with id: " + plantId));

        plant.setTag(null);
        return accountUserPlantRepository.save(plant);
    }

    /**
     * Ta bort en växt från biblioteket
     */
    public void removePlant(Long plantId, Long userId) {
        // Hitta och kolla att den tillhör userId
        AccountUserPlant plant = accountUserPlantRepository.findByIdAndUserId(plantId, userId)
                .orElseThrow(() -> new RuntimeException("Plant not found with id: " + plantId + " for user id: " + userId));

        // Ta bort växten
        accountUserPlantRepository.delete(plant);
    }

    /**
     * Hämta alla växter för en användare
     */
    public List<AccountUserPlant> getAllPlantsForUser(Long userId) {
        return accountUserPlantRepository.findByUserId(userId);
    }

    /**
     * Filtrera växter baserat på tagg
     */
    public List<AccountUserPlant> getPlantsByTag(Long userId, Long tagId) {
        return accountUserPlantRepository.findByUserIdAndTagId(userId, tagId);
    }

    /**
     * Sök växter baserat på namn
     */
    public List<AccountUserPlant> searchPlantsByName(Long userId, String searchTerm) {
        return accountUserPlantRepository.findByUserIdAndPlantNameContainingIgnoreCase(userId, searchTerm);
    }
}
