package se.mau.myhappyplants.util;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;



public class WateringFrequencyParserTest {


    @Test
    @DisplayName("CAR.07F - Parses a single number correctly")
    void testParserSingleNumber(){
        assertEquals(7, WateringFrequencyParser.parseWateringFrequency("7"));
        assertEquals(14, WateringFrequencyParser.parseWateringFrequency("14"));
    }

    @Test
    @DisplayName("CAR.07F - Parses a range as the average of min and max")
    void testParserRange(){
        // "7-14" → (7 + 14) / 2 = 10
        assertEquals(10, WateringFrequencyParser.parseWateringFrequency("7-14"));
    }

    @Test
    @DisplayName("CAR.07F - Returns 0 for blank input")
    void testParserBlankInput(){
        assertEquals(0,WateringFrequencyParser.parseWateringFrequency(""));
        assertEquals(0,WateringFrequencyParser.parseWateringFrequency("  "));
    }

    @Test
    @DisplayName("CAR.07F - Returns 0 for null input")
    void testParserNullInput(){
        assertEquals(0,WateringFrequencyParser.parseWateringFrequency(null));
    }

    @Test
    @DisplayName("CAR.07F - Returns 0 for non-numeric input")
    void testParserNonNumericInput(){
        assertEquals(0,WateringFrequencyParser.parseWateringFrequency("abc"));
        assertEquals(0,WateringFrequencyParser.parseWateringFrequency("often"));
    }
}
