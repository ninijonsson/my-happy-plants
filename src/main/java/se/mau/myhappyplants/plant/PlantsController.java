package se.mau.myhappyplants.plant;

import se.mau.myhappyplants.perenual.PerenualClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/plants")
public class PlantsController {

    private final PerenualClient perenualClient;

    public PlantsController(PerenualClient perenualClient)   {
        this.perenualClient = perenualClient;
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
