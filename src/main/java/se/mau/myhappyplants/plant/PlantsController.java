package se.mau.myhappyplants.plant;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import se.mau.myhappyplants.library.AccountUserPlant;
import se.mau.myhappyplants.library.LibraryService;
import se.mau.myhappyplants.perenual.PerenualClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import se.mau.myhappyplants.perenual.PerenualPlantDetailsResponse;
import se.mau.myhappyplants.user.AccountUser;

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

        //send the plant and user to the database
        model.addAttribute("user", user);
        model.addAttribute("plant", plant);
        model.addAttribute("details", apiDetails);
        return "plant-details";
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
}
