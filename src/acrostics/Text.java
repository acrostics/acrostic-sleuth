package acrostics;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** This class represents a text processed by the program */
public class Text {

    private ArrayList<String> text; // the full text, word after word or line after line depending on the mode
    private String encoded; // only the part of the text tested for acrostics
    private int length; // length == text.size() == encoded.length()
    public final String location; // path to associated file on disk
    private final CLO clo;
    public ArrayList<DocumentBreak> documentBreaks; // for wikisource files only

    /**
     * Read text from disk, preprocess it, and build words and line breaks arrays
     */
    public Text(String location, CLO clo) {
        this.location = location;
        this.clo = clo;
        length = -1;
        if (clo.wikisource) {
            documentBreaks = null;
        } else {
            documentBreaks = new ArrayList<>();
            documentBreaks.add(new DocumentBreak(location, 0));
        }
    }

    public void clearText() {
        this.text = null;
    }

    public void clearEncoded() {
        this.encoded = null;
    }

    public void load(CharModel cm, boolean loadText, boolean loadEncoded) {
        ArrayList<String> text = readText(location, clo.charset);
        StringBuilder encoded = new StringBuilder();
        int length = text.size();
        if (loadText)
            this.text = text;

        for (String unit: text) {
            for (String word: unit.split(" ")) { // TODO: Y o u  s h o u l d  t a k e  t h i s  i n t o  a c c o u
                if (cm != null) {
                    cm.addNextWord(word);
                }
            }
            if (loadEncoded) {
                encoded.append(unit.charAt(0));
            }
        }

        if (loadEncoded)
            this.encoded = encoded.toString();
        this.length = length;
    }

    public int length() {
        if (length == -1) {
            load(null,  false, true);
        }
        return length;
    }

    public boolean isEmpty() {
        return length == 0;
    }

    public char at(int i) {
        if (encoded == null) {
            load(null,  false, true);
        }
        return encoded.charAt(i);
    }

    public String subString(int start, int end) {
        if (encoded == null) {
            load(null,  false, true);
        }
        return encoded.substring(start, end);
    }

    public String textAtSubString(int start, int end) {
        if (text == null) {
            load(null, true, false);
        }
        StringBuilder builder = new StringBuilder();
        for (int i = start; i < end; i++) {
            builder.append(text.get(i));
            if (i == end - 1) {
                continue;
            }
            if (clo.mode == CLO.Mode.WORD) {
                builder.append(" ");
            } else {
                builder.append(" // ");
            }
        }
        return builder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Text)) return false;
        Text text = (Text) o;
        return location.equals(text.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(location);
    }

    private boolean coalesceLastLetters(ArrayList<String> text, boolean coalescedBefore) {
        int last = text.size();;
        if (last < 3 || text.get(last - 1).length() != 1) {
            return false;
        }
        if (coalescedBefore) {
            text.set(last - 2, text.get(last - 2) + text.get(last - 1));
            text.remove(last - 1);
            return true;
        }
        if (text.get(last - 2).length() == 1 && text.get(last - 3).length() == 1) {
            text.set(last - 3, text.get(last - 3) + text.get(last - 2) + text.get(last - 1));
            text.remove(last - 2);
            text.remove(last - 2);
            return true;
        }
        return false;
    }

    /** Read the file into a list of lines */
    private ArrayList<String> readText(String filename, Charset charset) {
        try {
            BufferedReader sc = new BufferedReader(new InputStreamReader(new FileInputStream(filename), charset));
            ArrayList<String> text = new ArrayList<>();
            boolean lineBreak;
            boolean coalesced = false;
            Pattern documentStart = Pattern.compile("^<doc id=\"[0-9]+\" url=\"[^\"]+\" title=\"(.*)\">$");
            boolean findDocumentBreaks = documentBreaks == null;
            if (findDocumentBreaks) {
                documentBreaks = new ArrayList<>();
            }
            String line = sc.readLine();
            while (line != null) {
                if (findDocumentBreaks) {
                    Matcher documentStartMatcher = documentStart.matcher(line);
                    if (documentStartMatcher.matches()) {
                        documentBreaks.add(new DocumentBreak(documentStartMatcher.group(1), text.size()));
                    }
                }
                line = clo.language.preprocess(line);
                lineBreak = true;
                for (String word: line.split(" ")) {
                    if (word.equals("")) {
                        continue;
                    }
                    if (lineBreak || clo.mode == CLO.Mode.WORD) {
                        coalesced = coalesceLastLetters(text, coalesced);
                        text.add(word);
                    } else {
                        text.set(text.size() - 1, text.get(text.size() - 1) + ' ' + word);
                    }
                    lineBreak = false;
                }
                line = sc.readLine();
            }
            return text;
        } catch (Exception e) {
            System.err.println("Failed to read file: " + filename);
            return null;
        }
    }

    public static class DocumentBreak {

        String name;
        int line;

        public DocumentBreak(String name, int line) {
            this.name = name;
            this.line = line;
        }

    }
}
