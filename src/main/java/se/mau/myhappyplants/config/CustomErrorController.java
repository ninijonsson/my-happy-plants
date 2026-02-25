package se.mau.myhappyplants.config;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.webmvc.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * CustomErrorController is responsible for handling application-wide errors and rendering an error page.
 * Implements Spring's {@link ErrorController} interface to intercept error requests and provide contextual information.
 *
 * When an error occurs, the corresponding status code and message are extracted from the
 * {@link HttpServletRequest} and passed to the model attributes. These attributes can then
 * be displayed on the error page by the view layer.
 *
 * Key functionality:
 * - Extracts error details such as status code and error message from the request.
 * - Adds extracted error details to the {@link Model} object as "status" and "message".
 * - Returns the path to the error view template ("/error/error").
 *
 * The error page can be customized to present user-friendly messages based on status codes
 * such as 404, 500, etc., as defined in the corresponding view template.
 */

@Controller
public class CustomErrorController implements ErrorController {
    
    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        
        Object message = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);

        model.addAttribute("status", status != null ? status.toString() : "Unknown");
        model.addAttribute("message", message != null ? message.toString() : "Mysterious spores found.");
        
        return "/error/error";
    }
}
