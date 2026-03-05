package se.mau.myhappyplants.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import se.mau.myhappyplants.perenual.PerenualClient;
import se.mau.myhappyplants.perenual.PerenualProperties;

@Configuration
public class PerenualConfig {

    @Bean
    public WebClient perenualWebClient(WebClient.Builder builder, PerenualProperties props) {
        return builder.baseUrl(props.baseUrl()).build();
    }
}
