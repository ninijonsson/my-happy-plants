package se.mau.myhappyplants.perenual;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;
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
     * Currently capped to get 5 plants
     * @param query
     * @return List of plants
     */
    @Cacheable(value = "plantSearch", key = "#query == null ? 'default_home' : #query")
    public List<PlantDetailsView> fetchPlants(String query) {
        PerenualSpeciesListResponse response = webClient.get()
                .uri(uri -> {
                    var b = uri.path("/species-list")
                            .queryParam("key", props.apiKey())
                            .queryParam("page", 1);
                    if (query != null && !query.isBlank()) b = b.queryParam("q", query);
                    return b.build();
                })
                .retrieve()
                .bodyToMono(PerenualSpeciesListResponse.class)
                .block();

        if (response == null || response.data() == null) return List.of();

        return response.data().stream()
                .limit(5)
                .map(p -> new PlantDetailsView(
                        p.id(),
                        (p.commonName() != null && !p.commonName().isBlank()) ? p.commonName() : "(no common name)",
                        (p.scientificName() != null && !p.scientificName().isEmpty()) ? p.scientificName().get(0) : null,
                        (p.defaultImage() != null)
                                ? (p.defaultImage().thumbnail() != null ? p.defaultImage().thumbnail() : p.defaultImage().regularUrl())
                                : null
                ))
                .toList();
    }

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
