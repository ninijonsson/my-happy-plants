package se.mau.myhappyplants.library;

import org.junit.jupiter.api.*;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("INF.03F - Water Status Calculation")
class AccountUserPlantTest {

    private AccountUserPlant plant;


    @BeforeEach
    void setUp() {
        plant = new AccountUserPlant("Monstera", "123");
        plant.setWateringFrequencyDays(7);
    }

    @Test
    @DisplayName("INF.03F - Watering status is 100% when interval passes")
    void testWateringStatusWhenOverdue(){
        plant.setLastWatered(LocalDateTime.now().minusDays(8));

        double status = plant.getDaysUntilNextWatering();

        assertEquals(100.0, status, 0.01);
    }

    @Test
    @DisplayName("INF.03F - Watering status is 50% halfway through interval")
    void testWateringStatusHalfwayThrough(){
        plant.setWateringFrequencyDays(10);
        plant.setLastWatered(LocalDateTime.now().minusDays(5));

        double status = plant.getDaysUntilNextWatering();

        assertEquals(50, status, 1.0);
    }

    @Test
    @DisplayName("INF.03F - Watering status is 0 if lastWatered is not set")
    void testWateringStatusWhenLastWateredNull(){
        plant.setLastWatered(LocalDateTime.now().minusDays(3));
        plant.setWateringFrequencyDays(null);

        double status = plant.getDaysUntilNextWatering();

        assertEquals(0.0, status, 0.01);
    }

    @Test
    @DisplayName("INF.03F - Next Watering date is lastWatered plus frequency in days")
    void testCalculateNextWateringDateCorrectDate(){
        LocalDateTime lastWatered = LocalDateTime.now().minusDays(3);
        plant.setLastWatered(lastWatered);

        plant.calculateNextWateringDate();

        assertEquals(lastWatered.toLocalDate().plusDays(7), plant.getNextWateringDate());
    }

    @Test
    @DisplayName("INF.03F - calculateNextWateringDate() does nothing if lastWatered is not set")
    void testCalculateNextWateringDateWhenLastWateredNull(){
        assertDoesNotThrow(plant::calculateNextWateringDate);
        assertNull(plant.getNextWateringDate());
    }

    @Test
    @DisplayName("INF.03F - calculateNextWateringDate() does nothing frequency is not set")
    void testCalculateNextWateringDateWhenFrequencyNull(){
        plant.setLastWatered(LocalDateTime.now());
        plant.setWateringFrequencyDays(null);

        assertDoesNotThrow(plant::calculateNextWateringDate);
        assertNull(plant.getNextWateringDate());
    }

    @AfterEach
    void tearDown() {
        plant = null;
    }
}