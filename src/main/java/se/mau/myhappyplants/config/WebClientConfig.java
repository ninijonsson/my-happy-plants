package se.mau.myhappyplants.config;

// OBS kanske tas bort :P

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Web/MVC configuration.
 * Used for application-wide MVC setup (view controllers, formatters, resource handling, etc.).
 */
@Configuration
public class WebClientConfig {
    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }
}
