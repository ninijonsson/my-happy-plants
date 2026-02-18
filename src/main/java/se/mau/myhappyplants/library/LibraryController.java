package se.mau.myhappyplants.library;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import se.mau.myhappyplants.user.AccountUser;

import java.util.List;

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

        //This is used for getting the currently logged in user
        AccountUser currentUser = (AccountUser) session.getAttribute("user");

        if (currentUser == null) {
            return "redirect:/login";
        }

        List<UserPlant> plants = libraryService.getUserLibrary(currentUser.getId(), sort);

        model.addAttribute("plants", plants);
        model.addAttribute("currentSort", sort);


        return "library/my-plants"; //Html page for overview
    }
}
