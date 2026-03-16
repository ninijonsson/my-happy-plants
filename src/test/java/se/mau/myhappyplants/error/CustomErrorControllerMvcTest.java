package se.mau.myhappyplants.error;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.ui.Model;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

@WebMvcTest(CustomErrorController.class)
public class CustomErrorControllerMvcTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private CacheManager cacheManager;

    @MockitoBean
    private HttpServletRequest httpServletRequest;

    @MockitoBean
    private Model model;

    @InjectMocks
    private CustomErrorController customErrorController;
    
    @Test
    void testMvcHandleErrorReturnsCorrectView() throws Exception {
        mvc.perform(get("/error")
                        .requestAttr(RequestDispatcher.ERROR_STATUS_CODE, "404")
                        .requestAttr(RequestDispatcher.ERROR_MESSAGE, "Not Found"))
                .andExpect(status().isOk())
                .andExpect(view().name("/error/error"))
                .andExpect(model().attribute("status", "404"))
                .andExpect(model().attribute("message", "Not Found"));
    }
    
    @Test
    void testMvcHandleErrorWithStatusAndMessage() throws Exception {
        mvc.perform(get("/error")
                        .requestAttr(RequestDispatcher.ERROR_STATUS_CODE, "500")
                        .requestAttr(RequestDispatcher.ERROR_MESSAGE, "Internal Server Error"))
                .andExpect(status().isOk())
                .andExpect(view().name("/error/error"))
                .andExpect(model().attribute("status", "500"))
                .andExpect(model().attribute("message", "Internal Server Error"));
    }
    
    @Test
    void testMvcHandleErrorWithStatusNull() throws Exception {
        mvc.perform(get("/error")
                        .requestAttr(RequestDispatcher.ERROR_MESSAGE, "Something went wrong."))
                .andExpect(status().isOk())
                .andExpect(view().name("/error/error"))
                .andExpect(model().attribute("status", "Unknown"))
                .andExpect(model().attribute("message", "Something went wrong."));
    }
    
    @Test
    void testMvcHandleErrorWithMessageNull() throws Exception {
        mvc.perform(get("/error")
                        .requestAttr(RequestDispatcher.ERROR_STATUS_CODE, "403"))
                .andExpect(status().isOk())
                .andExpect(view().name("/error/error"))
                .andExpect(model().attribute("status", "403"))
                .andExpect(model().attribute("message", "Mysterious spores found."));
    }
    
    @Test
    void testMvcHandleErrorStatusAndMessageNull() throws Exception {
        mvc.perform(get("/error"))
                .andExpect(status().isOk())
                .andExpect(view().name("/error/error"))
                .andExpect(model().attribute("status", "Unknown"))
                .andExpect(model().attribute("message", "Mysterious spores found."));
    }
}
