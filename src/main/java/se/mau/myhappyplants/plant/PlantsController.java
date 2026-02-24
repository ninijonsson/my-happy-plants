package se.mau.myhappyplants.plant;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import se.mau.myhappyplants.library.AccountUserPlant;
import ch.qos.logback.core.model.NamedModel;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import se.mau.myhappyplants.library.LibraryController;
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

    @GetMapping("/plant-details/{id}")
    public String showPlantDetails(@PathVariable int id, Model model, HttpSession session) {
        //Get the user from the session
        AccountUser user = (AccountUser) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        //Fetch the plant from the database
        AccountUserPlant plant = libraryService.getPlantById(id);

        PerenualPlantDetailsResponse apiDetails = perenualClient.fetchPlantDetails(plant.getPerenualId());

        model.addAttribute("user", user);
        model.addAttribute("plant", plant);
        model.addAttribute("details", apiDetails);
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
