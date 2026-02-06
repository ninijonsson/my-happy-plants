package se.mau.myhappyplants;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ApplicationFailTest {

    @Test
    void testFail() {
        assertTrue(false, "This test should fail");
    }
}
