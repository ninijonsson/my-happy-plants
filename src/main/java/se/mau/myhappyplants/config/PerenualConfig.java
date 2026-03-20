package se.mau.myhappyplants.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import se.mau.myhappyplants.perenual.PerenualClient;
import se.mau.myhappyplants.perenual.PerenualProperties;

/**
 * Configuration class for setting up communication with the Perenual API.
 *
 * This class defines a {@link WebClient} bean configured with the base URL
 * provided in {@link PerenualProperties}. The WebClient is used throughout
 * the application to perform HTTP requests to the external Perenual API.
 *
 * By declaring the WebClient as a Spring Bean, it can be injected into
 * other components (such as {@link PerenualClient}) and reused, ensuring
 * consistent configuration and efficient resource usage.
 */

@Configuration
public class PerenualConfig {

    @Bean
    public WebClient perenualWebClient(WebClient.Builder builder, PerenualProperties props) {
        return builder.baseUrl(props.baseUrl()).build();
    }
}
