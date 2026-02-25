package se.mau.myhappyplants.perenual;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Represents configuration properties specific to the Perenual API integration.
 * This class is used to bind properties prefixed with "perenual" from the
 * application's configuration file (e.g., application.yml or application.properties).
 *
 * The properties include:
 * - The base URL of the Perenual API.
 * - The API key required for authenticating requests to the Perenual API.
 *
 * These properties are used in other components, such as {@code PerenualClient},
 * to configure API requests and ensure proper operation of the integration.
 *
 * This record class simplifies the encapsulation and retrieval of these configuration
 * properties by providing an immutable data structure with default accessors.
 */

@ConfigurationProperties(prefix = "perenual")
public record PerenualProperties(String baseUrl, String apiKey) {}
