package se.mau.myhappyplants.user;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/account")
public class AccountController {

    @Autowired
    private AccountUserService accountUserService;

    @Autowired
    private AccountUserRepository accountUserRepository;

    @GetMapping("/delete")
    public String showWarningPage() {
        return "account/delete-warning"; // Första sidan
    }

    @GetMapping("/delete/confirm")
    public String showConfirmPage() {
        return "account/delete-confirm"; // Andra sidan
    }

    @PostMapping("/delete")
    public String deleteAccount(HttpSession session) {
        AccountUser sessionUser = (AccountUser) session.getAttribute("user");

        if (sessionUser == null) {
            return "redirect:/login";
        }

        accountUserService.deleteUser(sessionUser.getId());
        session.invalidate();
        return "redirect:/login?deleted";
    }
}