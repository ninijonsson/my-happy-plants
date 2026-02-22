package se.mau.myhappyplants.perenual;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import se.mau.myhappyplants.perenual.dto.PerenualPlant;
import se.mau.myhappyplants.perenual.dto.PerenualSpeciesListResponse;
import se.mau.myhappyplants.plant.dto.PlantDetailsView;
import org.springframework.http.HttpStatusCode;
import java.util.List;

/**
 * HTTP client for the Perenual API (base url, API key, endpoints).
 * Responsible for calling external endpoints and returning response DTOs.
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
     * Fetches the plants, used for home page
     * Currently capped to get 100 plants
     * @param query
     * @return List of plants
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
                            p.defaultImage() != null ? (p.defaultImage().thumbnail() != null ? p.defaultImage().thumbnail() : p.defaultImage().regularUrl()) : null,
                            p.wateringFrequency() != null && p.wateringFrequency().wateringFrequencyDays() != null && !p.wateringFrequency().wateringFrequencyDays().isBlank()
                                    ? p.wateringFrequency().wateringFrequencyDays() : "0"
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

    @Cacheable
    public PlantDetailsView fetchPlantById(String perenualPlantId){
        PerenualPlant response = webClient.get()
                .uri(uri -> uri.path("/species/details/" + perenualPlantId)
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
                        ? response.wateringFrequency().wateringFrequencyDays() : null
        );
    }
}
