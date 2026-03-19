package se.mau.myhappyplants.user;

import org.junit.jupiter.api.*;
import se.mau.myhappyplants.library.AccountUserPlant;

import static org.junit.jupiter.api.Assertions.*;

class AccountUserTest {

    private AccountUser user;

    @BeforeEach
    void setUp() {
        user = new AccountUser();
        user.setId(1);
        user.setUsername("TestUser");
    }

    @Test
    @DisplayName("LIB.01F - addUserPlant() adds plant to user's plant list")
    void testAddUserPlantAddsPlants(){
        AccountUserPlant plant = new AccountUserPlant("Monstera", "1");

        user.addUserPlant(plant);

        assertEquals(1, user.getUserPlants().size());
        assertTrue(user.getUserPlants().contains(plant));
    }

    @Test
    @DisplayName("LIB.01F - addUserPlant() sets user reference on plant")
    void testAddUserPlantSetsUserOnPlant() {
        AccountUserPlant plant = new AccountUserPlant("Monstera", "1");

        user.addUserPlant(plant);

        assertEquals(user, plant.getUser());
    }

    @Test
    @DisplayName("LIB.01F - addUserPlant() can add multiple plants")
    void testAddUserPlant_MultiiplePlants() {
        AccountUserPlant plant1 = new AccountUserPlant("Monstera", "1");
        AccountUserPlant plant2 = new AccountUserPlant("Cactus", "2");

        user.addUserPlant(plant1);
        user.addUserPlant(plant2);

        assertEquals(2, user.getUserPlants().size());
    }

    @Test
    @DisplayName("LIB.04F - removeUserPlant() remove plant from user's plant lits")
    void testRemovePlantRemovesPlant(){
        AccountUserPlant plant = new AccountUserPlant("Monstera", "1");

        user.removeUserPlant(plant);

        assertEquals(0, user.getUserPlants().size());
        assertFalse(user.getUserPlants().contains(plant));
    }

    @Test
    @DisplayName("LIB.04F - removeUserPlant() sets user reference to null on plant")
    void testRemoveUserPlantNullUserOnPlant(){
        AccountUserPlant plant = new AccountUserPlant("Monstera", "1");

        user.addUserPlant(plant);

        user.removeUserPlant(plant);

        assertNull(plant.getUser());
    }

    @Test
    @DisplayName("LIB.04F - removeUserPlant() Only removes the correct plant")
    void testRemoveUserPlantOnlyRemovesCorrect(){
        AccountUserPlant plant1 = new AccountUserPlant("Monstera", "1");
        AccountUserPlant plant2 = new AccountUserPlant("Cactus", "2");

        user.addUserPlant(plant1);
        user.addUserPlant(plant2);

        user.removeUserPlant(plant1);

        assertEquals(1, user.getUserPlants().size());
        assertTrue(user.getUserPlants().contains(plant2));

    }
    @AfterEach
    void tearDown() {
        user = null;
    }
}