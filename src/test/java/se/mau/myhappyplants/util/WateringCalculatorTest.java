package se.mau.myhappyplants.util;

import org.junit.jupiter.api.*;
import se.mau.myhappyplants.library.UserPlant;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class WateringCalculatorTest {

    @BeforeEach
    void setUp() {
    }

    @Disabled("Waiting for INF.03F - Calculation of watering status to be implemented")
    @Test
    @DisplayName("INF.03F - Calculation of watering status")
    void testWateringStatusLogic() {
        /**
         * Vet inte riktigt hur ofta Monstera ska vattnas
         * ändra gärna själv när du ska utföra testet
         */
//        UserPlant plant = new UserPlant("Monstera", 7);
//        plant.setLastWatered(LocalDate.now().minusDays(8));
//
//        assertTrue(plant.needsWater(), "The plant should need water if 8 days have passed with a 7-day interval");
    }

    @AfterEach
    void tearDown() {
    }
}