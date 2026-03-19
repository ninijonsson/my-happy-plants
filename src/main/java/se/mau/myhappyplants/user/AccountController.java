package se.mau.myhappyplants.user;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller responsible for handling account-related actions.
 *
 * This controller manages user account operations such as account deletion.
 * It provides endpoints for displaying warning and confirmation pages,
 * as well as processing the actual account deletion.
 *
 */

@Controller
@RequestMapping("/account")
public class AccountController {

    @Autowired
    private AccountUserService accountUserService;

    @Autowired
    private AccountUserRepository accountUserRepository;

    /**
     * Displays the account deletion warning page.
     *
     * @return the view name for the warning page
     */

    @GetMapping("/delete")
    public String showWarningPage() {
        return "account/delete-warning"; // Första sidan
    }

    /**
     * Displays the account deletion confirmation page.
     *
     * @return the view name for the confirmation page
     */

    @GetMapping("/delete/confirm")
    public String showConfirmPage() {
        return "account/delete-confirm"; // Andra sidan
    }

    /**
     * Handles the deletion of the currently logged-in user's account.
     *
     * If no user is found, the request is redirected to the login page.
     * Otherwise, the user's account is deleted, the session is invalidated,
     * and the user is redirected to the login page with a confirmation flag.
     *
     * @param session the current HTTP session containing the logged-in user
     * @return redirect to login page after deletion
     */

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