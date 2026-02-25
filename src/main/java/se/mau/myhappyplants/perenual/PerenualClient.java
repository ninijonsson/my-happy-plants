package se.mau.myhappyplants.perenual;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import se.mau.myhappyplants.perenual.dto.PerenualPlant;
import se.mau.myhappyplants.perenual.dto.PerenualSpeciesListResponse;
import se.mau.myhappyplants.plant.dto.PlantDetailsView;
import java.util.List;

/**
 * The PerenualClient class is a service component that provides methods for interacting
 * with the Perenual API to perform operations related to plant data. It allows fetching
 * plant search results, detailed information about specific plants, and additional plant
 * details using API endpoints. The client is configured with a base URL and API key, which
 * are provided via the application's properties.
 *
 * This client leverages WebClient for making HTTP requests and supports caching for
 * some of its methods to improve performance and reduce redundant API calls.
 */

@Service
public class PerenualClient {
    private final WebClient webClient;
    private final PerenualProperties props;

    public PerenualClient(WebClient.Builder builder, PerenualProperties props) {
        this.props = props;
        this.webClient = builder.baseUrl(props.baseUrl()).build();
    }

    /**
     * Fetches a list of plants based on the provided query string. This method sends a request to
     * an external API to retrieve plant data and returns a mapped list of `PlantDetailsView` objects,
     * representing the details of each plant.
     *
     * If no query is provided or an error occurs, an empty list is returned.
     *
     * @param query the search term used to filter plant results. If null or blank, a default search is performed.
     * @return a list of `PlantDetailsView` objects containing details about each plant, or an empty list if no results are found.
     */
    @Cacheable(value = "plantSearch", key = "#query == null ? 'default' : #query.toLowerCase().trim()")
    public List<PlantDetailsView> fetchPlants(String query) {
        try {
            PerenualSpeciesListResponse response = webClient.get()
                    .uri(uri -> {
                        var b = uri.path("/species-list")
                                .queryParam("key", props.apiKey())
                                .queryParam("page", 1);
                        if (query != null && !query.isBlank()) {
                            b = b.queryParam("q", query);
                        }
                        return b.build();
                    })
                    .retrieve()
                    .bodyToMono(PerenualSpeciesListResponse.class)
                    .block();

            if (response == null || response.data() == null) return List.of();

            return response.data().stream()
                    .limit(100)
                    .map(p -> new PlantDetailsView(
                            p.id(),
                            p.commonName() != null && !p.commonName().isBlank() ? p.commonName() : "(no common name)",
                            p.scientificName() != null && !p.scientificName().isEmpty() ? p.scientificName().get(0) : null,
                            p.defaultImage() != null ? p.defaultImage().regularUrl() : "/images/plant.jpg",
                            p.wateringFrequency() != null && p.wateringFrequency().wateringFrequencyDays() != null && !p.wateringFrequency().wateringFrequencyDays().isBlank()
                                    ? p.wateringFrequency().wateringFrequencyDays() : "0",
                            p.description()!= null && !p.description().isBlank() ? p.description() : null
                    ))
                    .toList();

        } catch (WebClientResponseException.TooManyRequests e) {
            System.out.println("API limit reached: " + e.getMessage());
            return List.of();
        } catch (Exception e) {
            System.out.println("API error: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Fetches plant details by its unique identifier. This method sends a request to an external
     * API to retrieve detailed information about a specific plant and maps it into a
     * {@code PlantDetailsView} object.
     *
     * Returns {@code null} if no plant is found.
     *
     * @param query the unique identifier of the plant to fetch details for
     * @return a {@code PlantDetailsView} object containing detailed information about the plant,
     *         or {@code null} if the plant is not found
     */
    @Cacheable("plantDetails")
    public PlantDetailsView fetchPlantById(String query){

        PerenualPlant response = webClient.get()
                .uri(uri -> uri.path("/species/details/" + query)
                        .queryParam("key", props.apiKey())
                        .build())
                .retrieve()
                .bodyToMono(PerenualPlant.class)
                .block();

        if (response == null) return null;

        return new PlantDetailsView(
                response.id(),
                response.commonName(),
                response.scientificName() != null && !response.scientificName().isEmpty()
                        ? response.scientificName().get(0) : null,
                response.defaultImage() != null
                        ? response.defaultImage().regularUrl() : null,
                response.wateringFrequency() != null &&
                        response.wateringFrequency().wateringFrequencyDays() != null
                        ? response.wateringFrequency().wateringFrequencyDays() : null,
                response.description()
        );
    }

    /**
     * Fetches detailed plant information from the Perenual API using the provided unique identifier.
     * This method sends a request to an external API to retrieve detailed information about a specific
     * plant and returns a {@code PerenualPlantDetailsResponse} containing the plant's details.
     *
     * @param perenualId the unique identifier of the plant to fetch details for
     * @return a {@code PerenualPlantDetailsResponse} object containing detailed information about the plant,
     *         or {@code null} if the plant is not found
     */
    public PerenualPlantDetailsResponse fetchPlantDetails(String perenualId) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/species/details/" + perenualId)
                        .queryParam("key", props.apiKey())
                        .build())
                .retrieve()
                .bodyToMono(PerenualPlantDetailsResponse.class)
                .block();
    }
}
