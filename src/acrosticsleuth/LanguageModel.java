package acrosticsleuth;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;

import static java.lang.System.exit;

public class LanguageModel {

    public static final String FILE_POSTFIX = ".vocab";
    public final static String[] TOKENS_TO_IGNORE = {"<s>", "<unk>", "</s>"};
    public final static char SPECIAL = '_'; // special character to indicate space
    public final HashMap<String,Double> tokenFrequencies = new HashMap<>();
    public final int longestToken;

    public LanguageModel(InputStream inputStream) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] lineSplit = line.split("\t");
                if ((lineSplit.length == 0) || Arrays.stream(TOKENS_TO_IGNORE).anyMatch(token -> token.equals(lineSplit[0]))) {
                    continue;
                }
                tokenFrequencies.put(lineSplit[0], Math.pow(Math.E, Double.parseDouble(lineSplit[1])));
            }
        } catch (Exception e) {
            System.err.println("Failed to read language model");
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
