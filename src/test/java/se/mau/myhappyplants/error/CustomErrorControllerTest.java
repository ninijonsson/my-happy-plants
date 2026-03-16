package se.mau.myhappyplants.error;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CustomErrorControllerTest {
        
    @Mock
    private HttpServletRequest httpServletRequest;

    @Mock
    private Model model;

    @InjectMocks
    private CustomErrorController customErrorController;

    @Test
    void testHandleErrorReturnsCorrectView() {
        when(httpServletRequest.getAttribute(RequestDispatcher.ERROR_STATUS_CODE))
                .thenReturn("404");
        when(httpServletRequest.getAttribute(RequestDispatcher.ERROR_MESSAGE))
                .thenReturn("Not Found");
        String view = customErrorController.handleError(httpServletRequest, model);
        assertEquals("/error/error", view);
    }
    
    @Test
    void testHandleErrorWithStatusAndMessage() {
        when(httpServletRequest.getAttribute(RequestDispatcher.ERROR_STATUS_CODE))
                .thenReturn("500");
        when(httpServletRequest.getAttribute(RequestDispatcher.ERROR_MESSAGE))
                .thenReturn("Internal Server Error");
        customErrorController.handleError(httpServletRequest, model);
        verify(model).addAttribute("status", "500");
        verify(model).addAttribute("message", "Internal Server Error");
    }
    
    @Test
    void testHandleErrorWithStatusNull() {
        when(httpServletRequest.getAttribute(RequestDispatcher.ERROR_STATUS_CODE))
                .thenReturn(null);
        when(httpServletRequest.getAttribute(RequestDispatcher.ERROR_MESSAGE))
                .thenReturn("Something went wrong");
        customErrorController.handleError(httpServletRequest, model);
        verify(model).addAttribute("status", "Unknown");
        verify(model).addAttribute("message", "Something went wrong");
    }
    
    @Test
    void testHandleErrorWithMessageNull() {
        when(httpServletRequest.getAttribute(RequestDispatcher.ERROR_STATUS_CODE))
                .thenReturn("403");
        when(httpServletRequest.getAttribute(RequestDispatcher.ERROR_MESSAGE))
                .thenReturn(null);
        customErrorController.handleError(httpServletRequest, model);
        verify(model).addAttribute("status", "403");
        verify(model).addAttribute("message", "Mysterious spores found.");
    }
    
    @Test
    void testHandleErrorStatusAndMessageNull() {
        when(httpServletRequest.getAttribute(RequestDispatcher.ERROR_STATUS_CODE))
                .thenReturn(null);
        when(httpServletRequest.getAttribute(RequestDispatcher.ERROR_MESSAGE))
                .thenReturn(null);
        customErrorController.handleError(httpServletRequest, model);
        verify(model).addAttribute("status", "Unknown");
        verify(model).addAttribute("message", "Mysterious spores found.");
    }
}
