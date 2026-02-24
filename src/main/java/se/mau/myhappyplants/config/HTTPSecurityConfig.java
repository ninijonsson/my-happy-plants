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

@Configuration
public class HTTPSecurityConfig {

    @Autowired
    AccountUserService accountUserService;
    
    @Autowired
    LoginSuccessHandler loginSuccessHandler;

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
    
    
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(accountUserService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}