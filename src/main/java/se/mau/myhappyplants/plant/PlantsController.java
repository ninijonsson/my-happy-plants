package se.mau.myhappyplants.plant;

import jakarta.servlet.http.HttpServletRequest;
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

@Controller
@RequestMapping("/plants")
public class PlantsController {

    @Autowired
    private PerenualClient perenualClient;

    @Autowired
    private LibraryService libraryService;

    @Autowired
    private HttpServletRequest request;

    /**
     * Handles the request to display details of a specific plant from the user's library.
     */
    @GetMapping("/plant-details/{id}")
    public String showLibraryPlantDetails(@PathVariable int id, Model model, HttpSession session) {
        AccountUserPlant plant = libraryService.getPlantById(id);

        AccountUser user = (AccountUser) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        PerenualPlantDetailsResponse apiDetails = perenualClient.fetchPlantDetails(plant.getPerenualId());

        model.addAttribute("user", user);
        model.addAttribute("details", apiDetails);
        model.addAttribute("plant", plant);

        session.setAttribute("fromLibrary", true);

        session.removeAttribute("lastSearchQuery");

        return "plant-details";
    }

    /**
     * Handles the request to preview details of a specific plant from search.
     */
    @GetMapping("/preview/{perenualId}")
    public String previewSearchPlant(@PathVariable String perenualId,
                                     Model model,
                                     HttpSession session) {
        AccountUser user = (AccountUser) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        PerenualPlantDetailsResponse apiDetails = perenualClient.fetchPlantDetails(perenualId);

        model.addAttribute("user", user);
        model.addAttribute("details", apiDetails);
        model.addAttribute("plant", null);

        // Ta bort library-flaggan om den finns
        session.removeAttribute("fromLibrary");

        // Hämta sökfrågan från sessionen (sparas i showPlants)
        String lastSearchQuery = (String) session.getAttribute("lastSearchQuery");
        if (lastSearchQuery != null) {
            model.addAttribute("lastSearchQuery", lastSearchQuery);
        }

        return "plant-details";
    }

    /**
     * Handles the request to display a plant search page.
     * DENNA METOD RÖRS INTE - exakt som testerna vill ha den!
     */
    @GetMapping("/search")
    public String showPlants(@RequestParam(required = false) String q,
                             Model model,
                             HttpSession session) {
        AccountUser user = (AccountUser) session.getAttribute("user");

        // Spara den aktuella sökfrågan i sessionen om den finns
        if (q != null && !q.isEmpty()) {
            session.setAttribute("lastSearchQuery", q);
        }

        // Hämta referer från autowired request-objektet
        String referer = request.getHeader("referer");
        if (referer != null && referer.contains("/plants/preview/")) {
            model.addAttribute("fromPlantDetails", true);
        }

        model.addAttribute("query", q == null ? "" : q);
        model.addAttribute("plants", perenualClient.fetchPlants(q));
        model.addAttribute("user", user);
        model.addAttribute("currentPage", "search");
        return "plant/plant-search";
    }

    /**
     * Handles HTTP POST requests to add a plant to the user's library.
     */
    @PostMapping("/add")
    public String addPlant(@RequestParam String perenualPlantId,
                           RedirectAttributes redirectAttributes,
                           HttpSession session) {

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

    /**
     * Retrieves plant data by its unique identifier.
     */
    @GetMapping("/{pPlantId}")
    @ResponseBody
    public ResponseEntity<Void> getPlantById(@PathVariable String pPlantId, Model model) {
        var result = perenualClient.fetchPlants(pPlantId);
        model.addAttribute("result", result);
        return ResponseEntity.ok().build();
    }
}