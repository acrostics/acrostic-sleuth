package acrostics;

import static java.lang.Integer.*;

public class Acrostic implements Comparable<Acrostic> {

    final public Text text; // the text that acrostic comes from

    final public Double p, LMP, CMP; // probability assigned by AcrosticGenerator

    final public String prefix, acrostic, postfix;
    public final String lastWord;
    private final String[] words;
    public final String documentName;

    final public int start, end;

    Acrostic(Text text, double LMP, double CMP, int start, int end, String acrostic, int acrosticDocumentStart, int acrosticDocumentEnd, String acrosticDocumentName) {
        this.prefix = text.subString(max(acrosticDocumentStart, start - CLO.OUTPUT_MARGIN), start);
        this.acrostic = acrostic;
        this.postfix = text.subString(end, min(acrosticDocumentEnd, end + CLO.OUTPUT_MARGIN));
        this.start = start;
        this.end = end;
        this.text = text;
        this.LMP = LMP;
        this.CMP = CMP;
        this.p = LMP / CMP;
        this.words = acrostic.split(String.valueOf(LanguageModel.SPECIAL));
        this.lastWord = words[words.length - 1];
        this.documentName = acrosticDocumentName;
    }

    @Override
    public int compareTo(Acrostic o) {
        return -this.p.compareTo(o.p);
    }
}