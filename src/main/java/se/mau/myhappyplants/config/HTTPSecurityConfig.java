package se.mau.myhappyplants.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import se.mau.myhappyplants.user.LoginSuccessHandler;
import se.mau.myhappyplants.user.AccountUserService;

/**
 * HTTPSecurityConfig is responsible for configuring security settings in the application.
 * This class integrates with Spring Security to define authentication and authorization rules,
 * custom login handling, logout features, and password encryption mechanisms.
 *
 * Key Responsibilities:
 * - Defines authorization rules using {@link HttpSecurity}, allowing or restricting access to application resources based on roles.
 * - Configures custom login behavior, including a specified login page and a custom login success handler.
 * - Configures logout behavior with a custom logout URL and redirecting users to a specific page upon logout success.
 * - Provides an {@link AuthenticationProvider} bean to handle authentication logic using the {@link AccountUserService}.
 * - Provides a {@link PasswordEncoder} bean to securely encode passwords with bcrypt hashing.
 *
 * Components:
 * - AccountUserService: Handles user-related business logic and integrates with authentication.
 * - LoginSuccessHandler: Manages actions to be performed upon successful login.
 * - DaoAuthenticationProvider: Used for validating credentials and fetching user details during authentication.
 * - BCryptPasswordEncoder: Used to hash and verify passwords in a secure manner.
*/

@Configuration
public class HTTPSecurityConfig {

    @Autowired
    AccountUserService accountUserService;
    
    @Autowired
    LoginSuccessHandler loginSuccessHandler;

    /**
     * Configures the security filter chain for HTTP requests, specifying authorization rules,
     * login handling, logout functionality, and disabling CSRF and CORS for the application.
     *
     * @param http the {@link HttpSecurity} object used to define security configurations for HTTP requests
     * @return the configured {@link SecurityFilterChain} instance
     * @throws Exception if an error occurs during configuration
     */
    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(registry-> {
                    registry.requestMatchers("/login", "/register", "/logout").permitAll();
                    registry.requestMatchers("/images/**", "/css/**", "/js/**").permitAll();
                    registry.requestMatchers("/plants/**").hasRole("USER");
                    registry.requestMatchers("/library/**").hasRole("USER");
                    registry.requestMatchers("/error/**").permitAll();
                    registry.anyRequest().hasRole("USER");//TODO: Make this more secure. Currently allow anyone to any site.
                })
                .formLogin(formLogin -> formLogin
                        .loginPage("/login")
                        .successHandler(loginSuccessHandler)
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .permitAll()
                        .logoutSuccessUrl("/login?logout"))
                .csrf(AbstractHttpConfigurer::disable) //TODO: If we want a more secure site and mimic a real website we should turn these on before deployment.
                .cors(AbstractHttpConfigurer::disable) //TODO: If we want a more secure site and mimic a real website we should turn these on before deployment.
                .build();
    }

    /**
     * Configures and provides an AuthenticationProvider bean, which is responsible for
     * specifying the authentication mechanism used in the application. This implementation
     * uses a DaoAuthenticationProvider that integrates with a custom user service and
     * employs a specified password encoder for credential validation.
     *
     * @return the configured AuthenticationProvider instance
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(accountUserService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    /**
     * Provides a PasswordEncoder bean that uses the {@link BCryptPasswordEncoder} for encoding and verifying passwords.
     * This encoder applies the BCrypt hashing algorithm, which is widely regarded as secure and includes a built-in salting mechanism.
     *
     * @return an instance of {@link PasswordEncoder} configured with {@link BCryptPasswordEncoder}
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}