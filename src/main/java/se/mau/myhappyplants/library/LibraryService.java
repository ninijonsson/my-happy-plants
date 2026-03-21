package se.mau.myhappyplants.library;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import se.mau.myhappyplants.perenual.PerenualClient;
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

    @Autowired
    private PerenualClient perenualClient;
    
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
     *                - "asctag": Sorted by the Tags alphabetical order (A-Z).
     *                - "destag": Sorted by the Tags alphabetical order (Z-A).
     *                - "water": Sort by closest to needing water. Default if null or invalid.
     *                - "Default": I don´t think the "water" is ever used, default however is
     *                used and has the same intended functinality as water, but it also separates the
     *                wishlist items from the normal ones with a label in the front end.
     *                And that is what the unique sorting functionality assists with.
     *
     * @return A list of {@code AccountUserPlant} objects representing the plants in the user's library
     *         sorted according to the specified criteria.
     */
    public List<AccountUserPlant> getUserLibrary(int userId, String sortDir) {
        Sort sort;
        String plantName = "plantName";

        String criteria = (sortDir == null) ? "water" : sortDir;

        switch(criteria) {
            case "asc":
                sort = Sort.by(Sort.Direction.ASC, plantName);
                return accountUserPlantRepository.findByUserId(userId, sort);

            case "desc":
                sort = Sort.by(Sort.Direction.DESC, plantName);
                return accountUserPlantRepository.findByUserId(userId, sort);

            case "recent":
                sort = Sort.by(Sort.Direction.DESC, "createdAt");
                return accountUserPlantRepository.findByUserId(userId, sort);

            case "water":
                return accountUserPlantRepository.findByUserId(userId, Sort.by(
                        Sort.Direction.ASC,"nextWateringDate"));

            case "asctag":
                return getPlantsSortedByTag(userId,false);

            case "desctag":
                return getPlantsSortedByTag(userId,true);

            default:
                List<AccountUserPlant> plants = accountUserPlantRepository.findByUserId(userId, Sort.unsorted());

                plants.forEach(AccountUserPlant::calculateNextWateringDate);

                plants.sort((p1, p2) -> {

                    boolean p1Wishlist = p1.getTag() != null && "Wishlist".equals(p1.getTag().getLabel());
                    boolean p2Wishlist = p2.getTag() != null && "Wishlist".equals(p2.getTag().getLabel());

                    if (p1Wishlist && !p2Wishlist) return 1;
                    if (!p1Wishlist && p2Wishlist) return -1;

                    if (p1.getNextWateringDate() == null && p2.getNextWateringDate() == null) return 0;
                    if (p1.getNextWateringDate() == null) return 1;
                    if (p2.getNextWateringDate() == null) return -1;

                    return p1.getNextWateringDate().compareTo(p2.getNextWateringDate());

                });

                return plants;
        }
    }

    /**
     * A method saving some lines of code. Sorts plants by tag, (a-z)(z-a) depending on the boolean.
     * @param userId The ID of the user whose plant library is being retrieved.
     * @param way The sorting direction or criteria. Possible values: true, false.
     */

    private List<AccountUserPlant> getPlantsSortedByTag(int userId,boolean way) {
        List<AccountUserPlant> TagPlants = accountUserPlantRepository.findByUserId(userId, Sort.unsorted());

        TagPlants.sort((p1, p2) -> {
            String tag1 = p1.getTag() != null ? p1.getTag().getLabel() : null;
            String tag2 = p2.getTag() != null ? p2.getTag().getLabel() : null;

            if (tag1 == null && tag2 == null) return 0;
            if (tag1 == null) return 1;
            if (tag2 == null) return -1;

            if(way){
                return tag2.compareToIgnoreCase(tag1);
            }else {
                return tag1.compareToIgnoreCase(tag2);
            }
        });
        return TagPlants;
    }

    /**
     * Add a new plant to the user library
     * @param plantDetails - plant info
     * @param userId - the user.
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
     *
     * @param plantId - plant which tag is going to be changed or added.
     * @param tagId - id of the tag.
     *
     */
    public boolean setTagOnPlant(int plantId, int tagId) {
        
        if(tagId == -1) {
            return removeTagFromPlant(plantId);
        }
        
        AccountUserPlant plant = accountUserPlantRepository.findById(plantId)
                .orElseThrow(() -> new RuntimeException("Plant not found with id: " + plantId));

        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new RuntimeException("Tag not found with id: " + tagId));

        plant.setTag(tag);
        
        AccountUserPlant newPlant = accountUserPlantRepository.save(plant);
        
        return newPlant.equals(plant);
    }

    /**
     * Add or change custom tag on a plant
     * @param plantId - plant which tag is going to be changed or added.
     * @param label - the actual string that will become the new tag.
     */
    public boolean setTagOnPlantByLabel(int plantId, String label) {
        if (label == null || label.trim().isEmpty()) {
            return false;
        }

        String cleaned = label.trim();

        AccountUserPlant plant = accountUserPlantRepository.findById(plantId)
                .orElseThrow(() -> new RuntimeException("Plant not found with id: " + plantId));

        Tag tag = tagRepository.findByLabel(cleaned)
                .orElseGet(() -> tagRepository.save(new Tag(cleaned)));

        plant.setTag(tag);

        accountUserPlantRepository.save(plant);

        return true;
    }

    /**
     * Remove tag from a plant
     * @param plantId, the plant
     */
    public boolean removeTagFromPlant(int plantId) {
        AccountUserPlant plant = accountUserPlantRepository.findById(plantId)
                .orElseThrow(() -> new RuntimeException("Plant not found with id: " + plantId));

        plant.setTag(null);
        
        AccountUserPlant newPlant = accountUserPlantRepository.save(plant);

        return newPlant.equals(plant);
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
        AccountUserPlant plant = accountUserPlantRepository.findByIdAndUserId(plantId, userId)
                .orElseThrow(() -> new RuntimeException("Plant not found with id: " + plantId + " for user id: " + userId));

        accountUserPlantRepository.delete(plant);
    }

    /**
     * Get all plant for a user,
     * @param userId - the user.
     * @return all plants the user has in their library.
     */
    public List<AccountUserPlant> getAllPlantsForUser(int userId) {
        return accountUserPlantRepository.findByUserId(userId, Sort.unsorted());
    }

    /**
     * Filter plants by tag.
     * @param userId - the user.
     * @param tagId - tag to be checked aginst plants
     * @return plants with tagid as tag.
     */
    public List<AccountUserPlant> getPlantsByTag(int userId, int tagId) {
        return accountUserPlantRepository.findByUserIdAndTagId(userId, tagId);
    }

    /**
     * Search plants which are added to the personal library.
     * @param userId - the user.
     * @param searchTerm - searchterm.
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

    /**
     * Seemingly unused method for retereiveing plants in a reverse alphabetical order.
     *
     * @param userId - the user.
     * @return plants in reverser order.
     */

    public List<AccountUserPlant> getPlantsReverseAlphabetically(int userId) {
        return accountUserPlantRepository.findByUserIdOrderByPlantNameDesc(userId);
    }

    /**
     * Updates the last watered time for a specific plant in the user's library to the current time.
     * Ensures the plant exists and belongs to the specified user before updating.
     *
     * @param userId The ID of the user who owns the plant.
     * @param plantId The ID of the plant to be updated.
     * @throws RuntimeException If the plant does not exist or does not belong to the specified user.
     */

    public void waterPlant(int userId, int plantId, LocalDateTime wateringdate) {
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
        List<AccountUserPlant> plants = accountUserPlantRepository.findByUserId(userId, Sort.unsorted());

        LocalDate today = LocalDate.now();
        long count = 0;

        for (AccountUserPlant plant : plants) {

            if (plant.getLastWatered() == null)
                continue;
            if (plant.getTag() != null && plant.getTag().getLabel().equals("Wishlist"))
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

    /**
     * Generates a summary of watering activity for a specific user.
     *
     * Retrieves all watering history entries for the given user and groups them by date.
     * For each day, the total number of watering events is calculated.
     *
     * The result is transformed into a list of maps, where each map contains:
     * - "date": the date of watering (as a String, format: yyyy-MM-dd)
     * - "count": the number of times plants were watered on that date
     *
     * The data is sorted chronologically (oldest to newest) using a TreeMap.
     *
     * @param userId the ID of the user whose watering history should be summarized
     * @return a list of date-count pairs used for rendering charts in the frontend
     */

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

    public String refreshPlantImage(int plantId) {
        AccountUserPlant plant = accountUserPlantRepository.findById(plantId)
                .orElseThrow(() -> new RuntimeException("Plant not found with id: " + plantId));

        String perenualId = plant.getPerenualId();
        if (perenualId == null || perenualId.isBlank()) {
            return null;
        }

        PlantDetailsView freshPlant = perenualClient.fetchPlantById(perenualId);
        if (freshPlant == null || freshPlant.imageUrl() == null || freshPlant.imageUrl().isBlank()) {
            System.out.println("api url image error");
            return null;
        }

        plant.setImageUrl(freshPlant.imageUrl());
        accountUserPlantRepository.save(plant);

        return freshPlant.imageUrl();
    }

}
