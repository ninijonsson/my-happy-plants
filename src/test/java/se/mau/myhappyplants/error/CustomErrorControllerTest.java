package se.mau.myhappyplants.error;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.webmvc.error.ErrorController;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CustomErrorControllerTest {

    @InjectMocks
    private CustomErrorController customErrorController;

    @Mock
    private HttpServletRequest request;

    @Mock
    private Model model;

    @Test
    void testHandleErrorReturnsCorrectView() {
        when(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE))
                .thenReturn("404");
        when(request.getAttribute(RequestDispatcher.ERROR_MESSAGE))
                .thenReturn("Not Found");
        String view = customErrorController.handleError(request, model);
        assertEquals("/error/error", view);
    }

    @Test
    void testHandleErrorWithStatusAndMessage(){
        when(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE))
                .thenReturn("500");
        when(request.getAttribute(RequestDispatcher.ERROR_MESSAGE))
                .thenReturn("Internal Server Error");
        customErrorController.handleError(request, model);
        verify(model).addAttribute("status", "500");
        verify(model).addAttribute("message", "Internal Server Error");
    }

    @Test
    void testHandleErrorWithStatusNull(){
        when(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE))
                .thenReturn(null);
        when(request.getAttribute(RequestDispatcher.ERROR_MESSAGE))
                .thenReturn("Something went wrong");
        customErrorController.handleError(request, model);
        verify(model).addAttribute("status", "Unknown");
        verify(model).addAttribute("message", "Something went wrong");
    }

    @Test
    void testHandleErrorWithMessageNull(){
        when(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE))
                .thenReturn("403");
        when(request.getAttribute(RequestDispatcher.ERROR_MESSAGE))
                .thenReturn(null);
        customErrorController.handleError(request, model);
        verify(model).addAttribute("status", "403");
        verify(model).addAttribute("message", "Mysterious spores found.");
    }

    @Test
    void testHandleErrorStatusAndMessageNull(){
        when(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE))
                .thenReturn(null);
        when(request.getAttribute(RequestDispatcher.ERROR_MESSAGE))
                .thenReturn(null);
        customErrorController.handleError(request, model);
        verify(model).addAttribute("status", "Unknown");
        verify(model).addAttribute("message", "Mysterious spores found.");
    }

}
