package se.mau.myhappyplants.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import se.mau.myhappyplants.config.PasswordValidatorConfig;

import java.util.Map;

@Controller
public class LoginController {
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private PasswordValidatorConfig passwordValidatorConfig;
    
    @Autowired
    private AccountUserService accountUserService;
   
    
    @GetMapping("/login")
    public String login() {
        return "/auth/login";
    }
    
    
    @PostMapping("/register")
    public ResponseEntity<?> createUser(@RequestParam String username, @RequestParam String password) {
        
        String passwordValidation = passwordValidatorConfig.isValid(password);
        if(!passwordValidation.equals("OK")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", passwordValidation));
        }
        
        boolean isCreated = accountUserService.createUser(username, passwordEncoder.encode(password), "USER");
        if(isCreated) {
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "User created successfully"));
        }else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "Username already exists"));
        }
    }
}
