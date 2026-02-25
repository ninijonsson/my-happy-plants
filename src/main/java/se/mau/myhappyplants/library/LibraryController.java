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
import java.time.LocalDateTime;
import java.util.List;
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
            @RequestParam(required = false, defaultValue = "water") String sort,
            @RequestParam(required = false) String search,
            Model model,
            HttpSession session
    ) {
        AccountUser user = (AccountUser) session.getAttribute("user");

        if (user == null) {
            return "redirect:/login";
        }

        List<AccountUserPlant> plants;
        if (search != null && search.length() >= 3) {
            plants = libraryService.searchPlantsByName(user.getId(), search);
        } else {
            plants = libraryService.getUserLibrary(user.getId(), sort);
        }

        long needsWatering = libraryService.countPlantsNeedingWater(user.getId());
        plants.forEach(AccountUserPlant::calculateNextWateringDate);
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
    public ResponseEntity<Void> waterPlant(@PathVariable int userId, @PathVariable int plantId,
                                           @RequestParam LocalDateTime wateringDate) {
        libraryService.waterPlant(userId, plantId, wateringDate);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/tags")
    @ResponseBody
    public ResponseEntity<?> getTags() {
       
        List<Tag> tags = tagService.getAllTags();
        
        return ResponseEntity.ok(tags);
    }
    
    @PutMapping("/plants/{plantId}/tags/{tagId}")
    public ResponseEntity<?> updateTag(@PathVariable int plantId, @PathVariable int tagId) {
        boolean isUpdated = libraryService.setTagOnPlant(plantId, tagId);
        
        if(isUpdated) {
            return ResponseEntity.ok("Tag updated successfully");
        } else {
            return ResponseEntity.status(400).body("Tag update failed");
        }
    }

    @GetMapping("/graph")
    public String getGraph(Model model, HttpSession session) {
        AccountUser user = (AccountUser) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        List<Map<String, Object>> chartData = libraryService.getUserWateringSummary(user.getId());
        model.addAttribute("wateringData", chartData);
        return "/watering-graph";
    }
}
