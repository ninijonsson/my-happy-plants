package se.mau.myhappyplants.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.cache.CacheManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import se.mau.myhappyplants.config.PasswordValidatorConfig;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LoginController.class)
public class LoginControllerMvcTest {
    
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    CacheManager cacheManager;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private PasswordValidatorConfig passwordValidatorConfig;

    @MockitoBean
    private AccountUserRepository accountUserRepository;

    @MockitoBean
    private AccountUserService accountUserService;

    @Test
    @WithMockUser
    @DisplayName("ACC.01F-Login - Login function return correctly")
    void testLogInReturnCorrect() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("/auth/login"));
    }

    @Test
    void testCreateUserBadRequest() throws Exception {
        when(passwordValidatorConfig.isValid("123")).thenReturn("Password too short");

        mockMvc.perform(post("/register")
                        .param("username", "Kalle")
                        .param("password", "123"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Password too short"));
    }

    @Test
    void testCreateNewUserSuccess() throws Exception {
        when(accountUserService.createUser(any(), any())).thenReturn(true);
        when(passwordValidatorConfig.isValid(any())).thenReturn("OK");

        mockMvc.perform(post("/register")
                        .param("username", "username")
                        .param("password", "password"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("User created successfully"));
    }

    @Test
    void testCreateExistingUser() throws Exception {
        when(accountUserService.createUser(any(), any())).thenReturn(false);
        when(passwordValidatorConfig.isValid(any())).thenReturn("OK");

        mockMvc.perform(post("/register")
                        .param("username", "test")
                        .param("password", "test"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Username already exists"));
    }
}
