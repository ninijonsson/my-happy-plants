package se.mau.myhappyplants.perenual;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "perenual")
public record PerenualProperties(String baseUrl, String apiKey) {}
