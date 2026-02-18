package se.mau.myhappyplants.library;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import se.mau.myhappyplants.user.User;
import se.mau.myhappyplants.user.UserService;

import java.util.List;

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
        model.addAttribute("plants", plants);
        model.addAttribute("user", user);
        return "library/my-plants"; //Thymeleaf template
    }

}
