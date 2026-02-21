package se.mau.myhappyplants.plant;

import ch.qos.logback.core.model.NamedModel;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import se.mau.myhappyplants.library.LibraryController;
import se.mau.myhappyplants.library.LibraryService;
import se.mau.myhappyplants.perenual.PerenualClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import se.mau.myhappyplants.plant.dto.PlantDetailsView;
import se.mau.myhappyplants.user.AccountUser;

@Controller
@RequestMapping("/plants")
public class PlantsController {

    private final PerenualClient perenualClient;
    private final LibraryService libraryService;

    public PlantsController(PerenualClient perenualClient, LibraryService libraryService) {
        this.perenualClient = perenualClient;
        this.libraryService = libraryService;
    }

    @GetMapping("/test")
    public String showTestPlants(
            @RequestParam(required = false) String q,
            Model model
    ) {
        model.addAttribute("query", q == null ? "" : q);
        model.addAttribute("plants", perenualClient.fetchPlants(q)); // din fetch()
        return "plants-list";
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
    public String addPlant(@RequestParam String plantName, @RequestParam String pPlantId, HttpSession session) {
        AccountUser user = (AccountUser) session.getAttribute("user");

        if (user == null) {
            return "redirect:/login";
        }
        PlantDetailsView plant = perenualClient.fetchPlantById(pPlantId);

        libraryService.addPlantToLibrary(plant, user.getId());

        return "redirect:/plants/search";
    }

}
