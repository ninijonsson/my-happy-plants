package se.mau.myhappyplants.library;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import se.mau.myhappyplants.user.AccountUser;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/library")
public class LibraryController {
    
    private final LibraryService libraryService;

    public LibraryController(LibraryService libraryService) {
        this.libraryService = libraryService;
    }

    
    @GetMapping
    public String showLibrary(
            @RequestParam(required = false, defaultValue = "asc") String sort,
            Model model,
            HttpSession session
    ) {
        AccountUser user = (AccountUser) session.getAttribute("user");
        var plants = libraryService.getAllPlantsForUser(user.getId());
        long needsWatering = libraryService.countPlantsNeedingWater(user.getId());

        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("plants", plants);
        model.addAttribute("user", user);
        model.addAttribute("needsWatering", needsWatering);
        model.addAttribute("currentSort", sort);
        return "library/my-plants"; //Thymeleaf template
    }

    @DeleteMapping("/{userId}/plants/{plantId}")
    @ResponseBody
    public ResponseEntity<Void> deletePlant(@PathVariable int userId, @PathVariable int plantId) {
        libraryService.removePlant(plantId, userId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{userId}/plants/{plantId}/water")
    public ResponseEntity<Void> waterPlant(@PathVariable int userId, @PathVariable int plantId) {
        libraryService.waterPlant(userId, plantId);
        return ResponseEntity.ok().build();
    }
}
