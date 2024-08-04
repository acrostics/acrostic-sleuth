package acrosticsleuth;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;

import acrosticsleuth.CommandLine.Option;
import acrosticsleuth.CommandLine.Command;

@Command(name = "CLO", mixinStandardHelpOptions = true, description = "Search a corpus for acrostics")
public class CLO implements Callable<Integer> {

    @Option(names = {"-input", "--input"}, required = true, description = "Directory with input texts")
    private String input; // list of all texts to be processed
    public File[] texts;

    @Option(names = {"-outputSize", "--outputSize"}, description = "Max number of results to print")
    public int outputSize = OUTPUT_SIZE_DEFAULT;

    public LanguageModel languageModel;
    public CharModel charModel;

    @Option(names = {"-maxLength", "--maxLength"}, description = "Maximum allowed length of an acrostic", hidden = true)
    public int maxLength = MAX_LENGTH_DEFAULT;

    @Option(names = {"-workers", "--workers"}, description = "Number of threads to use")
    public int workers = WORKERS_DEFAULT;

    @Option(names = {"-mode", "--mode"}, description = "Look for acrostics formed by the first letters of each LINE or WORD", hidden = true)
    public Mode mode = MODE_DEFAULT;

    @Option(names = {"-charset", "--charset"}, description = "utf-8 or windows-1251")
    public Charset charset = CHARSET_DEFAULT;

    @Option(names = {"-language", "--language"}, required = true, description = "EN, LA, RU, or FR")
    public Language language;

    @Option(names = {"-concise", "--concise"}, description = "Report results concisely")
    public boolean concise;

    @Option(names = {"-wikisource", "--wikisource"}, description = "Use if running on WikiSource")
    public boolean wikisource;

    @Option(names = {"-help", "--help"}, description = "Show this help message", usageHelp = true)
    public boolean help;

    public static final int MAX_LENGTH_DEFAULT = 50;
    public static final int OUTPUT_SIZE_DEFAULT = 10000;
    public static final int WORKERS_DEFAULT = 1;
    public static final Mode MODE_DEFAULT = Mode.LINE;
    public static final Charset CHARSET_DEFAULT = StandardCharsets.UTF_8;

    public static final int OUTPUT_MARGIN = 3;

    @Override
    public Integer call() {
        this.texts = listFiles(input);
        if (this.texts == null) {
            System.err.println("Could not find file or directory: " + input);
            return 1;
        } else if (this.texts.length == 0) {
            System.err.println("Directory is empty: " + input);
            return 1;
        }

        AcrosticCluster.concise = concise;

        ClassLoader classLoader = Main.class.getClassLoader();
        InputStream lmResource, cmResource;
        try {
            lmResource = classLoader.getResourceAsStream(language.label + LanguageModel.FILE_POSTFIX);
            cmResource = classLoader.getResourceAsStream(language.label + CharModel.FILE_POSTFIX);
        } catch (Exception e) {
            System.err.println("Could not load resource.\n" + e.getMessage());
            return 1;
        }
        if (lmResource == null || cmResource == null) {
            System.err.println("Loaded resource is null.\n");
            return 1;
        }
        languageModel = new LanguageModel(lmResource);
        charModel = new CharModel(cmResource);

        return 0;
    }

    private static File[] listFiles(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return null;
        }
        List<File> fileList = new ArrayList<>();
        if (file.isFile()) {
            fileList.add(file);
        } else {
            listFilesRecursive(file, fileList);
        }
        return fileList.toArray(new File[] {});
    }

    private static void listFilesRecursive(File directory, List<File> fileList) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    fileList.add(file);
                } else if (file.isDirectory()) {
                    listFilesRecursive(file, fileList);
                }
            }
        }
    }

    public enum Mode {
        LINE("LINE"), WORD("WORD");

        private final String label;

        Mode(String label) {
            this.label = label;
        }

        public static Mode get(String label) {
            for (Mode mode: Mode.values()) {
                if (mode.label.equals(label))
                    return mode;
            }
            return null; // TODO: Maybe throw an exception here
        }

    }

    public enum Language {

        EN("EN"), LA("LA"), RU("RU"), FR("FR");

        private final String label;

        Language(String label) {
            this.label = label;
        }

        public static Language get(String label) {
            for (Language language: Language.values()) {
                if (language.label.equals(label))
                    return language;
            }
            return null; // TODO: Maybe throw an exception here
        }

        public String preprocess(String text) {
            String preprocessed = text.toLowerCase(Locale.ROOT);
            switch (this) {
                case EN:
                    return preprocessed.replaceAll("[^a-z ]", " ");
                case LA:
                    return preprocessed.replaceAll("[^a-z ]", " ")
                        .replaceAll("v", "u")
                        .replaceAll("j", "i");
                case RU:
                    return preprocessed.replaceAll("[^ЁёѣiА-яaeopxc ]", " ")
                        .replaceAll("[ёѣe]", "е")
                        .replaceAll("a", "а")
                        .replaceAll("o", "о")
                        .replaceAll("p", "р")
                        .replaceAll("x", "х")
                        .replaceAll("c", "с")
                        .replaceAll("[йi]", "и")
                        .replaceAll("[ьъ]", "");
                case FR:
                    return preprocessed.replaceAll("[àâä]", "a")
                        .replaceAll("[éèêë]", "e")
                        .replaceAll("[îï]", "i")
                        .replaceAll("[ôö]", "o")
                        .replaceAll("[ùûü]", "u")
                        .replaceAll("v", "u")
                        .replaceAll("j", "i")
                        .replaceAll("ç", "c")
                        .replaceAll("œ", "oe")
                        .replaceAll("æ", "ae")
                        .replaceAll("'", "")
                        .replaceAll("[^a-zA-Z ]", " ");
            };
            return preprocessed;
        }

    }

}
