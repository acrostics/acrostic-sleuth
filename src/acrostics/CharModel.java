package acrostics;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Scanner;


/** A unigram character model */
public class CharModel {

    private final HashMap<Character, Long> counts;
    private long charsTotal;
    public static final String FILE_POSTFIX = "_cm.csv";

    public CharModel() {
        charsTotal = 0;
        counts = new HashMap<>();
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

    public void save(String filename) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filename + FILE_POSTFIX));
            for (char ch: counts.keySet()) {
                writer.write(ch + "," + counts.get(ch) + System.lineSeparator());
            }
            writer.close();
        } catch (Exception e) {
            System.err.println("Failed to write to file");
        }
    }

    /** Read the file into a list of lines */
    public void load(String filename) {
        try {
            Scanner sc = new Scanner(new File(filename + FILE_POSTFIX));
            while (sc.hasNextLine()) {
                String[] line = sc.nextLine().split(",");
                if (line.length == 0) return;
                counts.put(line[0].charAt(0), Long.parseLong(line[1]));
                charsTotal += counts.get(line[0].charAt(0));
            }
        } catch (Exception e) {
            System.err.println("Failed to read file: " + filename);
        }
    }
}
