package se.mau.myhappyplants.user;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;

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
