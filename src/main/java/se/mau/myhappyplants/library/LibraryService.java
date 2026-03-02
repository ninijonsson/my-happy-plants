package se.mau.myhappyplants.library;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import se.mau.myhappyplants.library.dto.PlantWateringData;
import se.mau.myhappyplants.plant.dto.PlantDetailsView;
import se.mau.myhappyplants.user.AccountUser;
import se.mau.myhappyplants.user.AccountUserRepository;
import se.mau.myhappyplants.util.WateringFrequencyParser;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Add plant to the library, update watering of the plant, filter by tag remove etc.
 * Service for managing a user's plant library.
 * Handles adding/removing plants, watering updates, and tag assignments.
 */
@Service
public class LibraryService {
    
    @Autowired
    private AccountUserPlantRepository accountUserPlantRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private AccountUserRepository accountUserRepository;

    @Autowired
    private WateringHistoryRepository wateringHistoryRepository;
    
    public LibraryService() {}

    public LibraryService(AccountUserPlantRepository accountUserPlantRepository) {
        this.accountUserPlantRepository = accountUserPlantRepository;
    }
    
    // For test purposes only
    public LibraryService(AccountUserPlantRepository plantRepository, TagRepository tagRepository) {
        this.accountUserPlantRepository = plantRepository;
        this.tagRepository = tagRepository;
    }

    /**
     * Retrieves a list of plants from the user's library based on the specified sorting criteria.
     * The sorting criteria can include alphabetical (asc, desc), most recently added (recent),
     * or priority based on the next watering date (default if criteria is not valid or null).
     *
     * @param userId The ID of the user whose plant library is being retrieved.
     * @param sortDir The sorting direction or criteria. Possible values:
     *                - "asc": Alphabetical order (A-Z).
     *                - "desc": Reverse alphabetical order (Z-A).
     *                - "recent": Sort by most recently added.
     *                - "water": Sort by closest to needing water. Default if null or invalid.
     * @return A list of {@code AccountUserPlant} objects representing the plants in the user's library
     *         sorted according to the specified criteria.
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
     * Add a new plant to the user library
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
        plant.setDescription(plantDetails.description());
        plant.setPerenualId(String.valueOf(plantDetails.id()));
        plant.setLastWatered(LocalDateTime.now());
        int wateringFreq = WateringFrequencyParser.parseWateringFrequency(plantDetails.wateringFrequency());
        if(wateringFreq > 0) {
            plant.setWateringFrequencyDays(wateringFreq);
        }else {
            plant.setWateringFrequencyDays(7);
        }
        return accountUserPlantRepository.save(plant);
    }

    /**
     * Add or change tag on a plant
     */
    public boolean setTagOnPlant(int plantId, int tagId) {
        
        if(tagId == -1) {
            removeTagFromPlant(plantId);
            return true;
        }

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
     * Remove tag from a plant
     */
    public AccountUserPlant removeTagFromPlant(int plantId) {
        AccountUserPlant plant = accountUserPlantRepository.findById(plantId)
                .orElseThrow(() -> new RuntimeException("Plant not found with id: " + plantId));

        plant.setTag(null);
        return accountUserPlantRepository.save(plant);
    }

    /**
     * Removes a plant from the user's library.
     * This method ensures that the plant belongs to the specified user
     * before attempting to remove it from the database.
     *
     * @param plantId The ID of the plant to be removed.
     * @param userId The ID of the user who owns the plant.
     * @throws RuntimeException If the plant with the specified ID does not exist
     *                          or does not belong to the specified user.
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

    /**
     * Retrieves a plant from the repository using its unique ID.
     *
     * @param plantId The unique identifier of the plant to retrieve.
     * @return The {@code AccountUserPlant} object if found, or {@code null} if no plant
     *         with the given ID exists in the repository.
     */
    public AccountUserPlant getPlantById(int plantId) {
        return accountUserPlantRepository.findById(plantId).orElse(null);
    }

    public List<AccountUserPlant> getPlantsReverseAlphabetically(int userId) {
        return accountUserPlantRepository.findByUserIdOrderByPlantNameDesc(userId);
    }

    public void waterPlant(int userId, int plantId, LocalDateTime wateringdate) {
    /**
     * Updates the last watered time for a specific plant in the user's library to the current time.
     * Ensures the plant exists and belongs to the specified user before updating.
     *
     * @param userId The ID of the user who owns the plant.
     * @param plantId The ID of the plant to be updated.
     * @throws RuntimeException If the plant does not exist or does not belong to the specified user.
     */
        AccountUserPlant plant = accountUserPlantRepository
                .findByIdAndUserId(plantId, userId)
                .orElseThrow(() ->
                        new RuntimeException("Plant not found for this user"));

        //LocalDateTime now = LocalDateTime.now();
        plant.setLastWatered(wateringdate);
        accountUserPlantRepository.save(plant);

        //Save in history
        AccountUser user = plant.getUser();
        WateringHistory history = new WateringHistory(user, plant, wateringdate);
        wateringHistoryRepository.save(history);
    }

    /**
     * Counts the number of plants associated with a user that need watering.
     *
     * @param userId the unique identifier for the user whose plants are to be checked
     * @return the number of plants that require watering
     */
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

    public List<Map<String, Object>> getUserWateringSummary(int userId) {
        List<WateringHistory> history = wateringHistoryRepository.findByUserIdOrderByWateredAtDesc(userId);
        Map<LocalDate, Long> waterCountPerDay = history.stream()
                .collect(Collectors.groupingBy(
                        h -> h.getWateredAt().toLocalDate(),
                        TreeMap::new,
                        Collectors.counting()
                ));

        return waterCountPerDay.entrySet().stream()
                .map(e -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("date", e.getKey().toString());
                    m.put("count", e.getValue());
                    return m;
                })
                .collect(Collectors.toList());
    }
}
