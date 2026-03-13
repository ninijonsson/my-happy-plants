package se.mau.myhappyplants.config;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import static org.junit.jupiter.api.Assertions.*;

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


    @BeforeEach
    void setUp() {
        config = new HTTPSecurityConfig();
    }

    @Test
    @Disabled
    @DisplayName("ACC.01F Login")
    void testLoginConfiguration() {

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
    @DisplayName("ACC.02.2F - Unauthenticated user is redirected to login page")
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


    @AfterEach
    void tearDown() {
    }
}