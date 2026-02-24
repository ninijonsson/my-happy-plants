package se.mau.myhappyplants.plant;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import se.mau.myhappyplants.library.AccountUserPlant;
import org.springframework.http.ResponseEntity;
import se.mau.myhappyplants.library.LibraryService;
import se.mau.myhappyplants.perenual.PerenualClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import se.mau.myhappyplants.perenual.PerenualPlantDetailsResponse;
import se.mau.myhappyplants.plant.dto.PlantDetailsView;
import se.mau.myhappyplants.user.AccountUser;
import java.security.Principal;

/**
 * Controller responsible for handling requests related to plants.
 * It provides endpoints for plant listing, details, search, and adding plants to the library.
 */
@Controller
@RequestMapping("/plants")
public class PlantsController {

    @Autowired
    private PerenualClient perenualClient;

    @Autowired
    private LibraryService libraryService;

    /**
     * Handles the request to display details of a specific plant from the user's library.
     * Fetches the plant details using its ID and prepares the view for rendering.
     *
     * @param id the unique identifier of the plant in the user's library
     * @param model the Model object used to populate attributes for the view
     * @param session the HTTP session object to retrieve session attributes
     * @return the name of the view to be rendered, containing the plant details
     */
    @GetMapping("/plant-details/{id}")
    public String showLibraryPlantDetails(@PathVariable int id, Model model, HttpSession session) {
        AccountUserPlant plant = libraryService.getPlantById(id);

        return prepareDetails(plant.getPerenualId(), plant, model, session);
    }

    /**
     * Handles the request to preview details of a specific plant identified by its unique perenual ID.
     * Fetches plant details using the provided ID and prepares the view for rendering.
     *
     * @param perenualId the unique identifier of the plant from the external service
     * @param model the Model object used to populate attributes for the view
     * @param session the HTTP session object to retrieve session attributes
     * @return the name of the view to be rendered, containing the plant details
     */
    @GetMapping("/preview/{perenualId}")
    public String previewSearchPlant(@PathVariable String perenualId, Model model, HttpSession session) {
        return prepareDetails(perenualId, null, model, session);
    }

    /**
     * Helper method for fetching plant details
     * to use it for the library plants and
     * the search plants
     */
    private String prepareDetails(String apiId, AccountUserPlant plant, Model model, HttpSession session) {
        AccountUser user = (AccountUser) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        PerenualPlantDetailsResponse apiDetails = perenualClient.fetchPlantDetails(apiId);

        model.addAttribute("user", user);
        model.addAttribute("details", apiDetails);
        model.addAttribute("plant", plant);

        return "plant-details";
    }

    /**
     * Handles the request to display a plant search page.
     * Populates the model with search results based on the query and the logged-in user details.
     *
     * @param q the optional search query to filter plants; can be null or empty to show all plants
     * @param model the Model object used to provide attributes for the view
     * @param session the HTTP session object to retrieve session-specific data, including user information
     * @return the name of the view to be rendered, which is "plant/plant-search"
     */
    @GetMapping("/search")
    public String showPlants(@RequestParam(required = false) String q, Model model, HttpSession session) {
        AccountUser user = (AccountUser) session.getAttribute("user");
        model.addAttribute("query", q == null ? "" : q);
        model.addAttribute("plants", perenualClient.fetchPlants(q));
        model.addAttribute("user", user);
        return "plant/plant-search";
    }

    /**
     * Retrieves plant data by its unique identifier and populates the provided model
     * with the fetched information.
     *
     * @param pPlantId the unique identifier for the plant to be fetched
     * @param model the model object used to store attributes for rendering the view
     * @return a ResponseEntity representing the HTTP response, with status 200 OK
     */
    @GetMapping("/{pPlantId}")
    @ResponseBody
    public ResponseEntity<Void> getPlantById(@PathVariable String pPlantId, Model model) {
        var result = perenualClient.fetchPlants(pPlantId);
        model.addAttribute("result", result);
        return ResponseEntity.ok().build();
    }

    /**
     * Handles HTTP POST requests to add a plant to the user's library.
     *
     * @param plantName          The name of the plant to add.
     * @param perenualPlantId    The unique identifier of the plant in the Perenual API.
     * @param redirectAttributes Redirect attributes to hold success or error messages after the operation.
     * @param principal          The security principal representing the current authenticated user.
     * @param session            The HTTP session to access user-specific data, such as the logged-in user.
     * @return A redirection string to the appropriate view, either the plant search page or the login page.
     */
    @PostMapping("/add")
    public String addPlant(@RequestParam String plantName, @RequestParam String perenualPlantId,
                           RedirectAttributes redirectAttributes, Principal principal, HttpSession session) {

        AccountUser user = (AccountUser) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        PlantDetailsView plant = perenualClient.fetchPlantById(perenualPlantId);
        try {
            libraryService.addPlantToLibrary(plant, user.getId());
            redirectAttributes.addFlashAttribute("successMessage", "Plant added to your library 🌿");
        }catch (Exception e){
            redirectAttributes.addFlashAttribute("errorMessage", "Plant could not be added ❌");
        }
        return "redirect:/plants/search";
    }

}
