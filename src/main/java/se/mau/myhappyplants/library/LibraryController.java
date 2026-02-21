package se.mau.myhappyplants.library;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import se.mau.myhappyplants.user.AccountUser;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Map;


@Controller
@RequestMapping("/library")
public class LibraryController {
    
    @Autowired
    private LibraryService libraryService;
    
    @Autowired
    private TagService tagService;
    
    @GetMapping
    public String showLibrary(
            @RequestParam(required = false, defaultValue = "water")
            String sort,
            Model model,
            HttpSession session
    ) {
        AccountUser user = (AccountUser) session.getAttribute("user");

        if (user.getRole() == null) {
            return "redirect:/login";
        }

        var plants = libraryService.getUserLibrary(user.getId(), sort);

        //Todo might need to update this call later for the sorting of water levels
        long needsWatering = libraryService.countPlantsNeedingWater(user.getId());

        model.addAttribute("plants", plants);
        model.addAttribute("user", user);
        model.addAttribute("needsWatering", needsWatering);
        model.addAttribute("currentSort", sort);
        return "/library/my-plants";
    }

    @DeleteMapping("/{userId}/plants/{plantId}")
    @ResponseBody
    public ResponseEntity<Void> deletePlant(@PathVariable int userId, @PathVariable int plantId) {
        libraryService.removePlant(plantId, userId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{userId}/plants/{plantId}/water")
    @ResponseBody
    public ResponseEntity<Void> waterPlant(@PathVariable int userId, @PathVariable int plantId) {
        libraryService.waterPlant(userId, plantId);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/tags")
    public ResponseEntity<?> getTags() {
        ArrayList<Tag> tags = (ArrayList<Tag>) tagService.getAllTags();
        return ResponseEntity.ok(Map.of("tags", tags));
    }
}
