package se.mau.myhappyplants.library.dto;

import java.time.LocalDateTime;

public class PlantWateringData {
    private String plantName;
    private LocalDateTime wateredAt;

    public PlantWateringData(String plantName, LocalDateTime wateredAt) {
        this.plantName = plantName;
        this.wateredAt = wateredAt;
    }

    public String getPlantName() {
        return plantName;
    }

    public LocalDateTime getWateredAt() {
        return wateredAt;
    }
}
