package se.mau.myhappyplants.library;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import se.mau.myhappyplants.user.AccountUser;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * The LibraryController handles requests related to the user's library of plants,
 * including viewing their plants, managing watering schedules, searching,
 * and managing tags for the plants.
 */
@Controller
@RequestMapping("/library")
public class LibraryController {
    
    @Autowired
    private LibraryService libraryService;
    
    @Autowired
    private TagService tagService;

    /**
     * Handles the retrieval and display of the user's library of plants.
     * The method determines how the library should be sorted, optionally handles
     * search filtering, sets the necessary model attributes, and returns the view to display.
     *
     * @param sort the sorting criteria for the plant library, defaulting to "water" if not provided
     * @param search an optional search query that filters plants by name, requiring a minimum of 3 characters
     * @param model the model used to set attributes for rendering the view
     * @param session the HTTP session used to retrieve the currently logged-in user
     * @return the path to the view displaying the user's plant library or a redirect to the login page if the user is not authenticated
     */
    @GetMapping
    public String showLibrary(
            @RequestParam(required = false, defaultValue = "water") String sort,
            @RequestParam(required = false) String search,
            Model model,
            HttpSession session
    ) {
        AccountUser user = (AccountUser) session.getAttribute("user");

        if (user == null) {
            return "redirect:/login";
        }

        List<AccountUserPlant> plants;
        if (search != null && search.length() >= 3) {
            plants = libraryService.searchPlantsByName(user.getId(), search);
        } else {
            plants = libraryService.getUserLibrary(user.getId(), sort);
        }

        long needsWatering = libraryService.countPlantsNeedingWater(user.getId());

        List<AccountUserPlant> regularPlants = new ArrayList<>();
        List<AccountUserPlant> wishlistPlants = new ArrayList<>();

        if ("water".equals(sort)) {
            for (AccountUserPlant plant : plants) {
                if (plant.getTag() != null && "Wishlist".equals(plant.getTag().getLabel())) {
                    wishlistPlants.add(plant);
                } else {
                    regularPlants.add(plant);
                }
            }
        }

        plants.forEach(AccountUserPlant::calculateNextWateringDate);
        model.addAttribute("plants", plants);
        model.addAttribute("regularPlants", regularPlants);
        model.addAttribute("wishlistPlants", wishlistPlants);
        model.addAttribute("user", user);
        model.addAttribute("needsWatering", needsWatering);
        model.addAttribute("currentSort", sort);
        model.addAttribute("currentPage", "library");
        return "/library/my-plants";
    }

    /**
     * Deletes a plant associated with a specific user from the library.
     *
     * @param userId the ID of the user owning the plant
     * @param plantId the ID of the plant to be deleted
     * @return a ResponseEntity indicating the completion of the delete operation
     */
    @DeleteMapping("/{userId}/plants/{plantId}")
    @ResponseBody
    public ResponseEntity<Void> deletePlant(@PathVariable int userId, @PathVariable int plantId) {
        libraryService.removePlant(plantId, userId);
        return ResponseEntity.ok().build();
    }

    /**
     * Waters the specified plant for a given user.
     * Updates the plant's watering schedule or status.
     *
     * @param userId the ID of the user who owns the plant
     * @param plantId the ID of the plant to be watered
     * @return a ResponseEntity with a status indicating the successful completion of the watering action
     */
    @PutMapping("/{userId}/plants/{plantId}/water")
    @ResponseBody
    public ResponseEntity<Void> waterPlant(@PathVariable int userId, @PathVariable int plantId,
                                           @RequestParam LocalDateTime wateringDate) {
        libraryService.waterPlant(userId, plantId, wateringDate);
        return ResponseEntity.ok().build();
    }

    /**
     * Retrieves all available tags for plants.
     *
     * @return a ResponseEntity containing a list of tags retrieved from the tagService, typically returned as a list of Tag objects
     */
    @GetMapping("/tags")
    @ResponseBody
    public ResponseEntity<?> getTags() {
       
        List<Tag> tags = tagService.getAllTags();
        
        return ResponseEntity.ok(tags);
    }

    /**
     * Updates the tag associated with a specific plant.
     * This method assigns a tag to a plant by their respective IDs.
     *
     * @param plantId the ID of the plant to which the tag will be updated
     * @param tagId the ID of the tag to be updated or assigned to the plant
     * @return a ResponseEntity containing a success message if the update is successful,
     *         or an error message with a 400 status code if the update fails
     */
    @PutMapping("/plants/{plantId}/tags/{tagId}")
    public ResponseEntity<?> updateTag(@PathVariable int plantId, @PathVariable int tagId) {
        boolean isUpdated = libraryService.setTagOnPlant(plantId, tagId);
        
        if(isUpdated) {
            return ResponseEntity.ok("Tag updated successfully");
        } else {
            return ResponseEntity.status(400).body("Tag update failed");
        }
    }

    /**
     * Updates the tag associated with a specific plant to a label of the users choosing.
     * This method assigns a tag to a plant by their respective IDs.
     *
     * @param plantId the ID of the plant to which the tag will be updated
     * @param body a JSON request body mapped to a key-value from the front end (textbox).
     * @return a ResponseEntity containing a success message if the update is successful,
     *         or an error message with a 400 status code if the update fails
     */

    @PutMapping("/plants/{plantId}/tag")
    public ResponseEntity<?> updateTagByLabel(@PathVariable int plantId, @RequestBody java.util.Map<String, String> body) {
        String label = body.get("label");
        boolean isUpdated = libraryService.setTagOnPlantByLabel(plantId, label);

        if (isUpdated) {
            return ResponseEntity.ok("Tag updated successfully");
        } else {
            return ResponseEntity.status(400).body("Tag update failed");
        }
    }

    /**
     * Handles requests for the watering history graph page.
     *
     * Retrieves the currently logged-in user from the session. If no user is found,
     * the request is redirected to the login page.
     *
     * Fetches aggregated watering data for the user and adds it to the model
     * Sets current page to graph for frontend page indicator.
     *
     * @param model the Spring MVC model used to pass data to the view
     * @param session the HTTP session containing the logged-in user
     * @return the name of the graph view template, or a redirect to login if no user is found
     */

    @GetMapping("/graph")
    public String getGraph(Model model, HttpSession session) {
        AccountUser user = (AccountUser) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        List<Map<String, Object>> chartData = libraryService.getUserWateringSummary(user.getId());
        model.addAttribute("wateringData", chartData);
        model.addAttribute("user", user);
        model.addAttribute("currentPage", "graph");
        return "/watering-graph";
    }

    @PutMapping("/plants/{plantId}/refresh-image")
    @ResponseBody
    public ResponseEntity<?> refreshPlantImage(@PathVariable int plantId) {
        String imageUrl = libraryService.refreshPlantImage(plantId);

        if (imageUrl != null) {
            return ResponseEntity.ok(java.util.Map.of("imageUrl", imageUrl));
        } else {
            return ResponseEntity.status(404).body("Could not refresh image");
        }
    }

}
