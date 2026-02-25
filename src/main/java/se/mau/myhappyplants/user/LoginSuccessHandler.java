package se.mau.myhappyplants.user;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;

/**
 * Handles successful authentication events in a Spring Security context.
 * This class is invoked when a user successfully logs in and performs post-login operations,
 * such as retrieving and storing the user's account details in the HTTP session.
 *
 * Responsibilities:
 * - Retrieves the authenticated user's details using their username.
 * - Stores the user details in the current HTTP session under the attribute "user".
 * - Redirects the user to the "/library" page after successful authentication.
 *
 * This class interacts with the `AccountUserService` for retrieving user details.
 * Implements the `AuthenticationSuccessHandler` interface provided by Spring Security.
 */
@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final AccountUserService accountUserService;

    public LoginSuccessHandler(AccountUserService accountUserService) {
        this.accountUserService = accountUserService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        AccountUser user = accountUserService.getUserByUsername(authentication.getName());
        request.getSession().setAttribute("user", user);
        response.sendRedirect("/library");
    }
}
