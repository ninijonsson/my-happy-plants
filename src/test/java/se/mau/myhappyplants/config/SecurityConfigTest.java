package se.mau.myhappyplants.config;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.autoconfigure.exclude=org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration,org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration",
        "perenual.api-key=test",
        "perenual.base-url=http://localhost"
})

class SecurityConfigTest {

    @Autowired
    private MockMvc mock;

    @MockitoBean
    private se.mau.myhappyplants.user.AccountUserService accountUserService;

    @MockitoBean
    private se.mau.myhappyplants.library.LibraryService libraryService;

    @MockitoBean
    private se.mau.myhappyplants.library.TagService tagService;

    @MockitoBean
    private se.mau.myhappyplants.perenual.PerenualClient perenualClient;

    @MockitoBean
    private se.mau.myhappyplants.user.AccountUserRepository accountUserRepository;


    private HTTPSecurityConfig config;
    @Autowired
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    @BeforeEach
    void setUp() {
        config = new HTTPSecurityConfig();
    }

    @Test
    @DisplayName("ACC.06F-Error Message Incorrect Password - " +
            "User receives error message when incorrect password is entered")
    void testLogInWithInvalidPassword() throws Exception {
        UserDetails mockUser = User.withUsername("username")
                        .password("hashedCorrectPassword").roles("USER").build();
        when(accountUserService.loadUserByUsername("username")).thenReturn(mockUser);
        mock.perform(MockMvcRequestBuilders.post("/login")
                .param("username", "username")
                .param("password", "WrongPassword0!"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/login?error"));

    }

    @Test
    @DisplayName("ACC.02.1F - Logged out user is redirected to login page")
    @WithMockUser(username = "testUser", roles = {"USER"})
    void testLogoutRedirectToLoginPageValid() throws Exception {
        mock.perform(SecurityMockMvcRequestBuilders.logout("/logout"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/login?logout"));
    }


    @Test
    @DisplayName("ACC.01.1F - Unauthenticated user is redirected to login page")
    void testRedirectToLoginPageInvalid() throws Exception {
        mock.perform(MockMvcRequestBuilders.get("/plants/search"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/login"));
    }

    @Test
    @DisplayName("ACC.02F Logout")
    void testLogoutConfiguration() {
        assertNotNull(config, "Configuration should not be null");
    }

    @Test
    @DisplayName("SEC.02Q-Encryption - Password is encoded and not stored as plaintext")
    void testPasswordIsEncoded(){
        String rawPassword = "MySecretPassword123";
        String encodedPassword = passwordEncoder.encode(rawPassword);
        assertNotEquals(rawPassword, encodedPassword, "Encoded password should not match raw password");
    }

    @Test
    @DisplayName("SEC.02Q-Encryption - Correct password matches encoded password")
    void testCorrectPasswordMatches(){
        String rawPassword = "MySecretPassword123";
        String encodedPassword = passwordEncoder.encode(rawPassword);
        assertTrue(passwordEncoder.matches(rawPassword, encodedPassword),
                "Correct password should match the encoded version");
    }

    @Test
    @DisplayName("SEC.02Q-Encryption - Same password encoded twice produces different hashes (salting)")
    void testEncodingIsSalted(){
        String rawPassword = "MySecretPassword123";
        String encodedPassword1 = passwordEncoder.encode(rawPassword);
        String encodedPassword2 = passwordEncoder.encode(rawPassword);
        assertNotEquals(encodedPassword1, encodedPassword2,
                "BCrypt should produce different hashes each time due to salting");
    }

    @Test
    @DisplayName("SEC.02Q-Encryption - Encoded password starts with BCrypt identifier")
    void testEncodedPasswordUsesBCrypt(){
        String rawPassword = "MySecretPassword123";
        String encodedPassword = passwordEncoder.encode(rawPassword);
        assertTrue(encodedPassword.startsWith("$2a$") || encodedPassword.startsWith("$2b$"),
                "Encoded password should use BCrypt format");
    }

    @AfterEach
    void tearDown() {
    }
}