package se.mau.myhappyplants.plant;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/plants")
public class PlantController {

    @GetMapping("/all")
    public String allPlants(){
        return "plant/all-plants";
    }
}
