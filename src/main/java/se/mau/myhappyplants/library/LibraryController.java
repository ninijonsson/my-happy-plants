package se.mau.myhappyplants.library;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import se.mau.myhappyplants.user.User;
import se.mau.myhappyplants.user.UserService;

@Controller
@RequestMapping("/library")
public class LibraryController {

    private LibraryService libraryService;
    private UserService userService;

    public LibraryController(LibraryService libraryService, UserService userService) {
        this.libraryService = libraryService;
        this.userService = userService;
    }

    @GetMapping
    public String showLibrary(Model model, @RequestParam Long userId) {
        var plants = libraryService.getAllPlantsForUser(userId);
        User user = userService.getUserById(userId);
        long needsWatering = libraryService.countPlantsNeedingWater(userId);

        model.addAttribute("plants", plants);
        model.addAttribute("user", user);
        model.addAttribute("needsWatering", needsWatering);
        return "library/my-plants"; //Thymeleaf template
    }

    @DeleteMapping("/{userId}/plants/{plantId}")
    @ResponseBody
    public ResponseEntity<Void> deletePlant(@PathVariable Long userId, @PathVariable Long plantId) {
        libraryService.removePlant(plantId, userId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{userId}/plants/{plantId}/water")
    public ResponseEntity<Void> waterPlant(@PathVariable Long userId, @PathVariable Long plantId) {
        libraryService.waterPlant(userId, plantId);
        return ResponseEntity.ok().build();
    }

}
