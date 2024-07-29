package acrostics;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;
import static java.lang.System.exit;

public class LanguageModel {

    private static final String FILE_POSTFIX = ".vocab";
    public final static String[] TOKENS_TO_IGNORE = {"<s>", "<unk>", "</s>"};
    public final static char SPECIAL = '▁'; // special character to indicate space
    public final HashMap<String,Double> tokenFrequencies = new HashMap<>();
    public final int longestToken;

    public LanguageModel(String filename) {
        filename = filename + FILE_POSTFIX;
        System.err.println("In constructor");
        try {
            Scanner sc = new Scanner(new File(filename), StandardCharsets.UTF_8);
            System.err.println("Reading file: " + filename);
            System.err.println("Reading file: " + new File(filename).getAbsolutePath());
            while (sc.hasNextLine()) {
                String[] line = sc.nextLine().split("\t");
                if ((line.length == 0) || Arrays.stream(TOKENS_TO_IGNORE).anyMatch(token -> token.equals(line[0]))) {
                    System.err.println("Skipping line");
                    continue;
                }
                System.err.println("Adding a token");
                tokenFrequencies.put(line[0], Math.pow(Math.E, Double.parseDouble(line[1])));  // TODO: Use logs
            }
        } catch (Exception e) {
            System.err.println("Failed to read file: " + filename);
            exit(1);
        }
        longestToken = getLongest();
    }

    private int getLongest() {
        int longest = 0;
        for (String token: tokenFrequencies.keySet()) {
            if (token.length() > longest) {
                longest = token.length();
            }
        }
        return longest;
    }

    public double p(String s, boolean withSpace) {
        if (withSpace) {
            s = SPECIAL + s;
        }
        double p = 0;
        if (tokenFrequencies.containsKey(s)) {
            p = tokenFrequencies.get(s);
        }
        return p;
    }
}
