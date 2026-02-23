package se.mau.myhappyplants.perenual;
import com.fasterxml.jackson.annotation.JsonProperty;
import se.mau.myhappyplants.perenual.dto.PerenualPlant;

import java.util.List;
/**
 * DTO mapping the JSON response from Perenual API.
 * Mirrors the external API structure (not the internal domain model).
 */
public class PerenualPlantDetailsResponse {
    private int id;
    @JsonProperty("common_name")
    private String commonName;

    @JsonProperty("scientific_name")
    private List<String> scientificName;
    private String family;
    private String watering;
    private List<String> sunlight;
    private String description;
    private String cycle;
    private String maintenance;

    @JsonProperty("default_image")
    private PerenualPlant.DefaultImage defaultImage;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getFamily() {
        return family;
    }

    public void setFamily(String family) {
        this.family = family;
    }

    public String getWatering() {
        return watering;
    }

    public void setWatering(String watering) {
        this.watering = watering;
    }

    public List<String> getSunlight() {
        return sunlight;
    }

    public void setSunlight(List<String> sunlight) {
        this.sunlight = sunlight;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCycle() {
        return cycle;
    }

    public void setCycle(String cycle) {
        this.cycle = cycle;
    }

    public String getMaintenance() {
        return maintenance;
    }

    public void setMaintenance(String maintenance) {
        this.maintenance = maintenance;
    }

    public String getCommonName() {
        return commonName;
    }

    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }

    public List<String> getScientificName() {
        return scientificName;
    }

    public void setScientificName(List<String> scientificName) {
        this.scientificName = scientificName;
    }

    public PerenualPlant.DefaultImage getDefaultImage() {
        return defaultImage;
    }

    public void setDefaultImage(PerenualPlant.DefaultImage defaultImage) {
        this.defaultImage = defaultImage;
    }
}
