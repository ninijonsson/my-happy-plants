import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AppTestFail {

    @Test
    void testShouldFail() {
        assertTrue(false, "Detta test misslyckades met flit för att test GitHub Action");
    }
}
