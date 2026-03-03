package se.mau.myhappyplants.user;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LoginSuccessHandlerTest{

    @Mock
    private AccountUserService accountUserService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private Authentication authentication;


    @InjectMocks
    private LoginSuccessHandler loginSuccessHandler;

    @Test
    void onAuthenticationSuccessTestValid() throws IOException {
        AccountUser user = new AccountUser();
        when(authentication.getName()).thenReturn("username");
        when(accountUserService.getUserByUsername("username")).thenReturn(user);
        when(request.getSession()).thenReturn(session);

        loginSuccessHandler.onAuthenticationSuccess(request, response, authentication);

        verify(accountUserService).getUserByUsername("username");
        verify(session).setAttribute("user", user);
        verify(response).sendRedirect("/library");
    }

    @Disabled("Implement the error handling for a null session")
    @Test
    void onAuthentication_userNotFound() throws IOException {
        when(authentication.getName()).thenReturn("unknownUser");
        when(accountUserService.getUserByUsername("unknownUser")).thenReturn(null);

        loginSuccessHandler.onAuthenticationSuccess(request, response, authentication);

        verify(response).sendRedirect("/login?error=userNotFound");
        verifyNoInteractions(session);
    }

    @Test
    void onAuthentication_sessionIsNull(){
        when(authentication.getName()).thenReturn("username");
        when(accountUserService.getUserByUsername("username")).thenReturn(new AccountUser());
        when(request.getSession()).thenReturn(null);

        assertThrows(NullPointerException.class,() -> loginSuccessHandler.onAuthenticationSuccess(request, response, authentication));
    }
}
