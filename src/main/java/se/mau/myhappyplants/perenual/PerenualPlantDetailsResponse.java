package se.mau.myhappyplants.perenual;
import java.util.List;
/**
 * DTO mapping the JSON response from Perenual API.
 * Mirrors the external API structure (not the internal domain model).
 */
public class PerenualPlantDetailsResponse {
    private int id;
    private String common_name;
    private List<String> scientific_name;
    private String family;
    private String watering;
    private List<String> sunlight;
    private String description;
    private String cycle;
    private String maintenance;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCommon_name() {
        return common_name;
    }

    public void setCommon_name(String common_name) {
        this.common_name = common_name;
    }

    public List<String> getScientific_name() {
        return scientific_name;
    }

    public void setScientific_name(List<String> scientific_name) {
        this.scientific_name = scientific_name;
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
}
