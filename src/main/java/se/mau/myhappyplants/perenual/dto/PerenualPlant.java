package se.mau.myhappyplants.perenual.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record PerenualPlant(
        int id,
        @JsonProperty("common_name") String commonName,
        @JsonProperty("scientific_name") List<String> scientificName,
        @JsonProperty("default_image") DefaultImage defaultImage,
        @JsonProperty("watering_general_benchmark") WateringFrequency wateringFrequency,
        @JsonProperty("description") String description
) {
    public record DefaultImage(
            @JsonProperty("thumbnail") String thumbnail,
            @JsonProperty("regular_url") String regularUrl
    ) {}
    public record WateringFrequency(
            @JsonProperty("value") String wateringFrequencyDays
    ){}
}