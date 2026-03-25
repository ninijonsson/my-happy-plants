package se.mau.myhappyplants.error;

import jakarta.servlet.RequestDispatcher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

@WebMvcTest(CustomErrorController.class)
public class CustomErrorControllerMvcTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private CacheManager cacheManager;
    
    @Test
    @DisplayName("INF.08F - Error Page returns the correct view")
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
    @DisplayName("INF.08F - Error Page returns the correct view with correct status and message")
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
    @DisplayName("INF.08F - Error Page returns correctly when status is null")
    void testMvcHandleErrorWithStatusNull() throws Exception {
        mvc.perform(get("/error")
                        .requestAttr(RequestDispatcher.ERROR_MESSAGE, "Something went wrong."))
                .andExpect(status().isOk())
                .andExpect(view().name("/error/error"))
                .andExpect(model().attribute("status", "Unknown"))
                .andExpect(model().attribute("message", "Something went wrong."));
    }
    
    @Test
    @DisplayName("INF.08F - Error Page returns correctly when message is null")
    void testMvcHandleErrorWithMessageNull() throws Exception {
        mvc.perform(get("/error")
                        .requestAttr(RequestDispatcher.ERROR_STATUS_CODE, "403"))
                .andExpect(status().isOk())
                .andExpect(view().name("/error/error"))
                .andExpect(model().attribute("status", "403"))
                .andExpect(model().attribute("message", "Mysterious spores found."));
    }
    
    @Test
    @DisplayName("INF.08F - Error Page returns correctly when status and message are null")
    void testMvcHandleErrorStatusAndMessageNull() throws Exception {
        mvc.perform(get("/error"))
                .andExpect(status().isOk())
                .andExpect(view().name("/error/error"))
                .andExpect(model().attribute("status", "Unknown"))
                .andExpect(model().attribute("message", "Mysterious spores found."));
    }
}
