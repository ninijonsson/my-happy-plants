package se.mau.myhappyplants.util;

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
