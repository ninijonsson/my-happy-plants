package se.mau.myhappyplants.library;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import se.mau.myhappyplants.plant.dto.PlantDetailsView;
import se.mau.myhappyplants.user.AccountUser;
import se.mau.myhappyplants.user.AccountUserRepository;
import se.mau.myhappyplants.util.WateringFrequencyParser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Add plant to the library, update watering of the plant, filter by tag remove etc.
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
    private AccountUserRepository accountUserRepository;

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

        //This is a safety fallback in case sortDir is ever null
        String criteria = (sortDir == null) ? "water" : sortDir;

        switch(criteria) {
            case "asc":
                //A-Z
                sort = Sort.by(Sort.Direction.ASC, plantName);
                break;
            case "desc":
                //Z-A
                sort = Sort.by(Sort.Direction.DESC, plantName);
                break;
            case "recent":
                //most recently added
                sort = Sort.by(Sort.Direction.DESC, "createdAt");
                break;
            case "water":
                //simply a safety measure in case something doesnt work
            default:
                //Closest to needing water
                //default mode
                sort = Sort.by(Sort.Direction.ASC, "nextWateringDate");
                break;
        }
        return accountUserPlantRepository.findByUserId(userId, sort);
    }

    /**
     * Lägg till en ny växt till användarens bibliotek
     */
    public AccountUserPlant addPlantToLibrary(PlantDetailsView plantDetails, int userId) {
        // Hitta användaren
        AccountUser user = accountUserRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        AccountUserPlant plant = new AccountUserPlant();
        plant.setPlantName(plantDetails.name());
        plant.setUser(user);
        plant.setImageUrl(plantDetails.imageUrl());
        plant.setScientificName(plantDetails.scientificName());
        plant.setPerenualId(String.valueOf(plantDetails.id()));
        plant.setLastWatered(LocalDateTime.now());
        plant.setWateringFrequencyDays(WateringFrequencyParser.parseWateringFrequency(plantDetails.wateringFrequency()));

        return accountUserPlantRepository.save(plant);
    }

    /**
     * Lägg till eller ändra tagg på en växt
     */
    public boolean setTagOnPlant(int plantId, int tagId) {

        // Find the plant
        AccountUserPlant plant = accountUserPlantRepository.findById(plantId)
                .orElseThrow(() -> new RuntimeException("Plant not found with id: " + plantId));

        // Verify the tag exists (optional but recommended)
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new RuntimeException("Tag not found with id: " + tagId));

        // Set the tag ID on the plant (not the Tag object)
        plant.setTag(tag);

        // Save the plant with the new tag
        accountUserPlantRepository.save(plant);
        
        return accountUserPlantRepository.existsById(plantId);
    }

    /**
     * Ta bort tagg från en växt
     */
    public AccountUserPlant removeTagFromPlant(int plantId) {
        AccountUserPlant plant = accountUserPlantRepository.findById(plantId)
                .orElseThrow(() -> new RuntimeException("Plant not found with id: " + plantId));

        plant.setTag(null);
        return accountUserPlantRepository.save(plant);
    }

    /**
     * Ta bort en växt från biblioteket
     */
    public void removePlant(int plantId, int userId) {
        // Hitta och kolla att den tillhör userId
        AccountUserPlant plant = accountUserPlantRepository.findByIdAndUserId(plantId, userId)
                .orElseThrow(() -> new RuntimeException("Plant not found with id: " + plantId + " for user id: " + userId));

        // Ta bort växten
        accountUserPlantRepository.delete(plant);
    }

    /**
     * Hämta alla växter för en användare
     *
     */
    public List<AccountUserPlant> getAllPlantsForUser(int userId) {
        return accountUserPlantRepository.findByUserId(userId);
    }

    /**
     * Filtrera växter baserat på tagg
     */
    public List<AccountUserPlant> getPlantsByTag(int userId, int tagId) {
        return accountUserPlantRepository.findByUserIdAndTagId(userId, tagId);
    }

    /**
     * Sök växter baserat på namn
     */
    public List<AccountUserPlant> searchPlantsByName(int userId, String searchTerm) {
        return accountUserPlantRepository.findByUserIdAndPlantNameContainingIgnoreCase(userId, searchTerm);
    }

    public AccountUserPlant getPlantById(int plantId) {
        return accountUserPlantRepository.findById(plantId).orElse(null);
    }

    public List<AccountUserPlant> getPlantsReverseAlphabetically(int userId) {
        return accountUserPlantRepository.findByUserIdOrderByPlantNameDesc(userId);
    }

    public void waterPlant(int userId, int plantId) {
        AccountUserPlant plant = accountUserPlantRepository
                .findByIdAndUserId(plantId, userId)
                .orElseThrow(() ->
                        new RuntimeException("Plant not found for this user"));

        plant.setLastWatered(LocalDateTime.now());

        accountUserPlantRepository.save(plant);
    }

    public long countPlantsNeedingWater(int userId) {
        List<AccountUserPlant> plants = accountUserPlantRepository.findByUserId(userId);

        LocalDate today = LocalDate.now();
        long count = 0;

        for (AccountUserPlant plant : plants) {

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
