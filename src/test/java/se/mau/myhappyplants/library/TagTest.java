package se.mau.myhappyplants.library;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class TagTest {

    @BeforeEach
    void setUp() {
    }

    @Disabled
    @Test
    @DisplayName("LIB.05F - add with tag")
    void tagTest() {


        // String tag = "test tag";
        // alterativly Enum
        // Tag tag = Tag.values()[0];

        // UserPlant plant = new UserPlant("Monstera",7,tag );


        assertAll("Tag test results",
                () -> assertEquals("test tag", plant.getTag())
        );

    }

    @Disabled
    @Test
    @DisplayName("LIB.05.1F - create tag")
    void tagTest() {

        // String tag = "test tag";


        assertAll("Tag test results",
                () -> assertEquals("test tag", plant.getTag())
        );

    }

    @Disabled
    @Test
    @DisplayName("LIB.05.2F - add with wishlist tag")
    void tagTest() {


        // String tag = "test tag";
        // alterativly Enum
        // Tag tag = Tag.values()[1];

        // UserPlant plant = new UserPlant("Monstera",7,tag );


        assertAll("Tag test results",
                () -> assertEquals("test tag", plant.getTag())
        );
    }

    @Disabled
    @Test
    @DisplayName("LIB.05.3F - change tag")
    void tagTest() {


        // String tag = "test tag";
        // alterativly Enum
        // Tag tag = Tag.values()[1];

        // UserPlant plant = new UserPlant("Monstera",7,tag );
         // plant.setTag("changedTag");


        assertAll("Tag test results",
                () -> assertEquals("changedTag", plant.getTag())
        );
    }


    @AfterEach
    void tearDown() {
    }
}