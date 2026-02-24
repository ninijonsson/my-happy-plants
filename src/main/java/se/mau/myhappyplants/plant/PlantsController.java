package se.mau.myhappyplants.plant;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
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

@Controller
@RequestMapping("/plants")
public class PlantsController {

    @Autowired
    private PerenualClient perenualClient;

    @Autowired
    private LibraryService libraryService;

    @GetMapping("/plant-details/{id}")
    public String showLibraryPlantDetails(@PathVariable int id, Model model, HttpSession session) {
        AccountUserPlant plant = libraryService.getPlantById(id);

        return prepareDetails(plant.getPerenualId(), plant, model, session);
    }

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

    @GetMapping("/search")
    public String showPlants(@RequestParam(required = false) String q, Model model, HttpSession session) {
        AccountUser user = (AccountUser) session.getAttribute("user");
        model.addAttribute("query", q == null ? "" : q);
        model.addAttribute("plants", perenualClient.fetchPlants(q));
        model.addAttribute("user", user);
        return "plant/plant-search";
    }

    @GetMapping("/{pPlantId}")
    @ResponseBody
    public ResponseEntity<Void> getPlantById(@PathVariable String pPlantId, Model model) {
        var result = perenualClient.fetchPlants(pPlantId);
        model.addAttribute("result", result);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/add/{pPlantId}")
    public String addPlant(@PathVariable String pPlantId, HttpSession session) {
        AccountUser user = (AccountUser) session.getAttribute("user");

        if (user == null) {
            return "redirect:/login";
        }
        PlantDetailsView plant = perenualClient.fetchPlantById(pPlantId);

        libraryService.addPlantToLibrary(plant, user.getId());

        return "redirect:/plants/search";
    }

}
