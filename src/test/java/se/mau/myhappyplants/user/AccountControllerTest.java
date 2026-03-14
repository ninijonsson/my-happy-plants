package se.mau.myhappyplants.user;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.cache.CacheManager;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountController.class)
@WithMockUser
public class AccountControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private CacheManager cacheManager;

    @InjectMocks
    private AccountController accountController;

    @MockitoBean
    private AccountUserService accountUserService;

    @MockitoBean
    private AccountUserRepository accountUserRepository;

    @Test
    void testDeleteUserSuccess() throws Exception {
        AccountUser mockUser = new AccountUser();
        mockUser.setId(1);
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", mockUser);

        mvc.perform(post("/account/delete").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?deleted"));
        verify(accountUserService, times(1)).deleteUser(1);
    }

    @Test
    void testDeleteUserFail() throws Exception {
        MockHttpSession session = new MockHttpSession();

        mvc.perform(post("/account/delete").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void testShowWarningPage() throws Exception {
        mvc.perform(get("/account/delete"))
                .andExpect(status().isOk())
                .andExpect(view().name("account/delete-warning"));
    }

    @Test
    void testShowConfirmPage() throws Exception {
        mvc.perform(get("/account/delete/confirm"))
                .andExpect(status().isOk())
                .andExpect(view().name("account/delete-confirm"));
    }
}
