package acrosticsleuth;

import java.io.*;
import java.util.HashMap;

import static java.lang.System.exit;


/** A unigram character model */
public class CharModel {

    private final HashMap<Character, Long> counts;
    private long charsTotal;
    public static final String FILE_POSTFIX = "_cm.csv";

    public CharModel(InputStream inputStream) {
        charsTotal = 0;
        counts = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] lineSplit = line.split(",");
                if (lineSplit.length == 0) return;
                counts.put(lineSplit[0].charAt(0), Long.parseLong(lineSplit[1]));
                charsTotal += counts.get(lineSplit[0].charAt(0));
            }
        } catch (Exception e) {
            System.err.println("Failed to read character model");
            exit(1);
        }
    }

    /**
     * Get the probability of encountering a given sequence of characters
     */
    public double p(String token) {
        double p = 1;
        for (int i = 0; i < token.length(); i++) {
            p *= (double) counts.getOrDefault(token.charAt(i), 0L) / charsTotal;
        }
        return p;
    }

    /** Adds all characters in a text to the model */
    public void addNextWord(String word) {
        charsTotal += word.length();
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            counts.put(c, counts.getOrDefault(c, 0L) + 1);
        }
    }
}
