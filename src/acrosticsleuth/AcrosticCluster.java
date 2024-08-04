package acrosticsleuth;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.*;

import static acrosticsleuth.LanguageModel.SPECIAL;

public class AcrosticCluster implements Comparable<AcrosticCluster> {

    final private ArrayList<Acrostic> acrostics;
    private Acrostic bestAcrostic;

    private int start, end;
    private String prefix, postfix, toStringResult;

    public static boolean concise = false;
    public static final String HEADER_CONCISE = "file\tacrostic\trank";
    public static final String HEADER = "file\tacrostic\trank\tfrom\tto\tcluster\tfrom\tto\tprefix\tpostfix\tsourceText";

    @Override
    public int compareTo(AcrosticCluster acrosticCluster) {
        return -this.bestAcrostic.compareTo(acrosticCluster.bestAcrostic);
    }

    private AcrosticCluster(Acrostic acrostic) {
        bestAcrostic = acrostic;
        acrostics = new ArrayList<>();
        acrostics.add(acrostic);
        this.start = acrostic.start;
        this.end = acrostic.end;
        this.prefix = acrostic.prefix;
        this.postfix = acrostic.postfix;
        this.toStringResult = null;
    }

    private boolean intersects(Acrostic acrostic) {
        return (this.end > acrostic.start) && (this.start < acrostic.end);
    }

    private void addAcrostic(Acrostic acrostic) { // TODO: Make sure to restrict the size of the cluster
        acrostics.add(acrostic);
        if (acrostic.compareTo(bestAcrostic) < 0) {
            bestAcrostic = acrostic;
        }
        if (acrostic.start < this.start) {
            this.start = acrostic.start;
            this.prefix = acrostic.prefix;
        }
        if (acrostic.end > this.end) {
            this.end = acrostic.end;
            this.postfix = acrostic.postfix;
        }
    }

    public static ArrayList<AcrosticCluster> clusterAcrostics(Iterable<Acrostic> acrostics, boolean loadToString) {
        HashMap<Text, ArrayList<Acrostic>> acrosticsByText = new HashMap<>();
        for (Acrostic acrostic:
             acrostics) {
            acrosticsByText.putIfAbsent(acrostic.text, new ArrayList<>());
            acrosticsByText.get(acrostic.text).add(acrostic);
        }
        ArrayList<AcrosticCluster> result = new ArrayList<>();
        for (Text text:
             acrosticsByText.keySet()) {
            ArrayList<Acrostic> acrosticList = acrosticsByText.get(text);
            acrosticList.sort(Comparator.comparingInt(one -> one.start));
            AcrosticCluster current = new AcrosticCluster(acrosticList.get(0));
            for (int i = 1; i < acrosticList.size(); i++) {
                if (current.intersects(acrosticList.get(i))) {
                    current.addAcrostic(acrosticList.get(i));
                } else {
                    result.add(current);
                    if (loadToString) {
                        current.toString();
                    }
                    current = new AcrosticCluster(acrosticList.get(i));
                }
            }
            result.add(current);
            if (loadToString) {
                current.toString();
            }
            text.clearText();
            text.clearEncoded();
        }
        return result;
    }

    private String acrosticClusterString() {
        String[] result = new String[end - start];
        Arrays.fill(result, "");
        acrostics.sort(Acrostic::compareTo);
        for (Acrostic acrostic:
             acrostics) {
            int spaces = 0;
            boolean previouslyEmpty = false;
            for (int i = acrostic.start; i < acrostic.start + acrostic.acrostic.length(); i++) {
                char c = acrostic.acrostic.charAt(i - acrostic.start);
                if (c == SPECIAL) {
                    spaces += 1;
                }
                if (result[i - start - spaces].equals("") || (previouslyEmpty && c == SPECIAL)) {
                    result[i - start - spaces] += c;
                    previouslyEmpty = true;
                } else {
                    previouslyEmpty = false;
                }
            }
        }
        for (int i = start; i < end; i++) {
            if (result[i - start].equals("")) {
                result[i - start] += bestAcrostic.text.at(i);
            }
        }
        return String.join("", result);
    }

    private static String formatSignificant(double value) {
        BigDecimal bd = new BigDecimal(value, new MathContext(8));
        return bd.toString();
    }

    @Override
    public String toString() {
        if (toStringResult != null) {
            return toStringResult;
        }
        String[] data;
        if (concise) {
            data = new String[] {
                    bestAcrostic.documentName,
                    bestAcrostic.acrostic,
                    formatSignificant(bestAcrostic.p)};
        } else {
            data = new String[] {
                    bestAcrostic.documentName,
                    bestAcrostic.acrostic,
                    formatSignificant(bestAcrostic.p),
                    String.valueOf(bestAcrostic.start),
                    String.valueOf(bestAcrostic.end),
                    acrosticClusterString(),
                    String.valueOf(start),
                    String.valueOf(end),
                    prefix,
                    postfix,
                    bestAcrostic.text.textAtSubString(start, end)
            };
        }
        toStringResult = String.join("\t", data);
        return toStringResult;
    }
}
