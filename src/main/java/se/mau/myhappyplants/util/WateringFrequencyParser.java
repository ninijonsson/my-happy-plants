package se.mau.myhappyplants.util;

/**
 * Utility class for parsing watering frequency strings into integer values.
 * The input string can represent a single number or a range expressed in
 * the form "min-max". If a range is provided, the method calculates the average.
 *
 * Invalid or malformed inputs will return a default value of 0.
 * This class is intended to help standardize and simplify frequency calculations.
 */
public class WateringFrequencyParser {
    public static Integer parseWateringFrequency(String frequency) {
        if (frequency == null || frequency.isBlank()) {
            return 0;
        }
        frequency = frequency.replace("\"", "").trim();

        if (frequency.contains("-")) {
            String[] parts = frequency.split("-");
            try {
                int min = Integer.parseInt(parts[0].trim());
                int max = Integer.parseInt(parts[1].trim());
                return (min + max) / 2;
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        try {
            return Integer.parseInt(frequency);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
