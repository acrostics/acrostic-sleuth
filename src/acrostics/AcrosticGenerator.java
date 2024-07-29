package acrostics;

import static java.lang.Integer.max;

/** A thread that generates acrostics from a text and inserts them into a
 * PriorityBlockingQueue so that they are ranked by probability */
public class AcrosticGenerator implements Runnable {

    private final int MAX_REPETITION = 2;
    private final BoundedPriorityBlockingQueue<Acrostic> heap;
    private final Text text;
    private final CLO clo;
    private final CharModel cm;
    private final LanguageModel lm;

    public AcrosticGenerator(BoundedPriorityBlockingQueue<Acrostic> heap, Text text,
                             CLO clo, CharModel cm, LanguageModel lm) {
        this.heap = heap;
        this.text = text;
        this.clo = clo;
        this.cm = cm;
        this.lm = lm;
    }


    @Override
    public void run() {
        Acrostic currentBest = heap.peek();
        Acrostic[][] dynamicProgrammingArray = new Acrostic[clo.maxLength+1][];
        for (int i = 0; i < clo.maxLength + 1; i++) {
            dynamicProgrammingArray[i] = new Acrostic[lm.longestToken];
        }
        int[] repetitions = {0, 0, 0, 0, 0, 0, 0};
        int lastRepetition = 0;
        int documentBreak = 0;
        for (int end = 1; end <= text.length(); end++) { // end is the rightmost end of potential acrostic (exclusive)
            if ((documentBreak + 1 < text.documentBreaks.size()) && (text.documentBreaks.get(documentBreak + 1).line < end)) {
                documentBreak++;
                repetitions = new int[] {0, 0, 0, 0, 0, 0, 0};
                for (int i = 0; i < clo.maxLength + 1; i++) {
                    dynamicProgrammingArray[i] = new Acrostic[lm.longestToken];
                }
                lastRepetition = text.documentBreaks.get(documentBreak).line;
            }
            boolean text_has_repetitions = false;
            for (int periodSize = 1; periodSize < repetitions.length; periodSize++) {
                if (end - periodSize * 2 < text.documentBreaks.get(documentBreak).line) {
                    continue;
                }
                if (text.subString(end - periodSize * 2, end - periodSize).equals(text.subString(end - periodSize, end))) {
                    repetitions[periodSize] += 1;
                    if (repetitions[periodSize] >= MAX_REPETITION) {
                        text_has_repetitions = true;
                    }
                } else {
                    repetitions[periodSize] = 0;
                }
            }
            if (text_has_repetitions) {
                lastRepetition = end;
                continue;
            }
            double[] lastLMP = new double[lm.longestToken+1];
            double[] lastLMPWithSpace = new double[lm.longestToken+1];
            String[] lastWord = new String[lm.longestToken+1];
            for (int start = max(0, end - lm.longestToken); start < end; start++) {
                lastWord[end-start] = text.subString(start, end);
                lastLMP[end-start] = lm.p(lastWord[end-start], false);
                lastLMPWithSpace[end-start] = lm.p(lastWord[end-start], true);
            }

            for (int start = max(max(0, end - clo.maxLength), lastRepetition); start < end; start++) { // start is the leftmost end (inclusive)
                double CMP = cm.p(text.subString(start, end));
                int len = end - start; // length of the potential acrostic
                String bestStr = null;
                double bestLMP = 0;
                for (int k = max(0, len - lm.longestToken); k < len; k++) {
                    // k is the index, within the acrostic string, of the first character after the last token
                    double prevLMP = 0;
                    if (k == 0) {
                        prevLMP = 1;
                    } else if (dynamicProgrammingArray[k][(start+k)%(lm.longestToken)] != null) {
                        prevLMP = dynamicProgrammingArray[k][(start+k)%(lm.longestToken)].LMP;
                    }
                    boolean hasSpace = false;
                    double LMP = 0;
                    if (k == 0 || dynamicProgrammingArray[k][(start+k)%(lm.longestToken)] == null || lastLMPWithSpace[end - start - k] > lastLMP[end - start - k]) {
                        hasSpace = true;
                        LMP = prevLMP * lastLMPWithSpace[end - start - k];
                    } else {
                        LMP = prevLMP * lastLMP[end - start - k];
                    }
                    if ((bestStr == null) || (LMP > bestLMP)) {
                        if (k == 0 || dynamicProgrammingArray[k][(start+k)%(lm.longestToken)] == null) {
                            bestStr = lastWord[end-start-k];
                            bestLMP = LMP;
                        } else if (!dynamicProgrammingArray[k][(start+k)%(lm.longestToken)].lastWord.equals(lastWord[end-start-k])) {
                            if (hasSpace) {
                                bestStr = dynamicProgrammingArray[k][(start+k)%(lm.longestToken)].acrostic + LanguageModel.SPECIAL + lastWord[end-start-k];
                            } else {
                                bestStr = dynamicProgrammingArray[k][(start+k)%(lm.longestToken)].acrostic + lastWord[end-start-k];
                            }
                            bestLMP = LMP;
                        }
                    }
                }

                Acrostic acrostic = null;
                if (bestStr != null) {
                    int acrosticDocumentStart = text.documentBreaks.get(documentBreak).line;
                    int acrosticDocumentEnd = text.length();
                    if (text.documentBreaks.size() > documentBreak + 1) {
                        acrosticDocumentEnd = text.documentBreaks.get(documentBreak + 1).line;
                    }
                    String acrosticDocumentName = text.documentBreaks.get(documentBreak).name;
                    acrostic = new Acrostic(text, bestLMP, CMP, start, end, bestStr, acrosticDocumentStart, acrosticDocumentEnd, acrosticDocumentName);
                    if ((currentBest == null || currentBest.compareTo(acrostic) > 0)) {
                        heap.offer(acrostic);
                    }
                }
                dynamicProgrammingArray[len][end % lm.longestToken] = acrostic;
            }
        }
    }

}
