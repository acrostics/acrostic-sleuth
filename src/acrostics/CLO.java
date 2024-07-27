package acrostics;

import org.apache.commons.cli.*;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CLO { // command line options

    // Required Options:
    public final File[] texts; // list of all texts to be processed

    // Optional Options:
    public final int outputSize; // max number of potential acrostics to print
    public final String models; // Prefix for a language model file (either to read from or to save to)
    public final int maxLength; // maximum length of an acrostic (in characters)
    public final int workers;  // number of threads to use
    public final Mode mode; // use first letter of each line, word, or smth else
    public final Charset charset;
    public final Language language; // determines what preprocessing to do
    public final boolean concise; // Report the lines that form the acrostic
    public final boolean wikisource; // parse wikisource files into separate articles and report results accordingly

    // Default Values:
    public static final int MAX_LENGTH_DEFAULT = 50;
    public static final int OUTPUT_SIZE_DEFAULT = 10000;
    public static final int WORKERS_DEFAULT = 1;
    public static final Mode MODE_DEFAULT = Mode.LINE;
    public static final Charset CHARSET_DEFAULT = StandardCharsets.UTF_8;

    // Custom Constants:
    // print that many tokens before and after each acrostic (not configurable)
    public static final int OUTPUT_MARGIN = 3;

    CLO(String[] args) {
        Options options = new Options();

        Option index = new Option("input", "input", true,
                "file or directory with all texts of interest");
        index.setRequired(true);
        index.setType(String.class);
        options.addOption(index);

        Option language = new Option("language", "language", true,
                "Determines the text preprocessing steps:\n" +
                "LA - latin alphabet + j=i and v=u \n" +
                "EN - latin alphabet \n" +
                "RU - russian alphabet - ьъ + ё=е.");
        language.setRequired(true);
        language.setType(String.class);
        options.addOption(language);

        Option outputSize = new Option("outputSize", "outputSize", true,
                "maximum number of potential acrostics to print." +
                        "Note that the actual number printed might be " +
                        "considerably lower if print is not set to ALL.");
        outputSize.setOptionalArg(true);
        outputSize.setType(Number.class);
        options.addOption(outputSize);

        Option models = new Option("models", "models", true,
                "prefix of the file from which to load/save to the models");
        models.setOptionalArg(true);
        models.setType(String.class);
        options.addOption(models);

        Option maxLength = new Option("maxLength", "maxLength", true,
                "maximum length of an acrostic (in characters)");
        maxLength.setOptionalArg(true);
        maxLength.setType(Number.class);
        options.addOption(maxLength);

        Option workers = new Option("workers", "workers", true,
                "number of threads to use");
        workers.setOptionalArg(true);
        workers.setType(Number.class);
        options.addOption(workers);

        Option mode = new Option("mode", "mode", true,
                "Determines which letters to form a phrase from:\n" +
                "LINE - first letter of each line, i.e. acrostic\n" +
                "WORD - first letter of each word.");
        mode.setOptionalArg(true);
        mode.setType(String.class);
        options.addOption(mode);

        Option charset = new Option("charset", "charset", true,
                "Name of the character encoding to use.\n" +
                        "Supports utf-8 and windows-1251");
        charset.setOptionalArg(true);
        charset.setType(String.class);
        options.addOption(charset);

        Option wikisource = new Option("wikisource", "wikisource", false,
                "Parse wikisource files into separate articles and report results accordingly");
        wikisource.setOptionalArg(true);
        options.addOption(wikisource);

        Option concise = new Option("concise", "concise", false,
                "Only report the acrostic, the file it comes from, and the rank, but omit all the extra information");
        concise.setOptionalArg(true);
        options.addOption(concise);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            formatter.printHelp("utility-name", options);
            System.exit(1);
        }

        this.texts = listFiles(cmd.getOptionValue("input"));
        if (this.texts == null) {
            System.err.println("Could not find file or directory: " + cmd.getOptionValue("input"));
            System.exit(1);
        } else if (this.texts.length == 0) {
            System.err.println("Directory is empty: " + cmd.getOptionValue("input"));
            System.exit(1);
        }

        this.language = Language.get(cmd.getOptionValue("language"));
        if (this.language == null) {
            System.err.println("Language " + cmd.getOptionValue("language") + " is not supported. Please use one of EN, LA, FR, or RU");
            System.exit(1);
        }

        this.outputSize = cmd.hasOption("outputSize") ?
                Integer.parseInt(cmd.getOptionValue("outputSize")) :
                OUTPUT_SIZE_DEFAULT;
        this.maxLength = cmd.hasOption("maxLength") ?
                Integer.parseInt(cmd.getOptionValue("maxLength")) :
                MAX_LENGTH_DEFAULT;
        this.workers = cmd.hasOption("workers") ?
                Integer.parseInt(cmd.getOptionValue("workers")) :
                WORKERS_DEFAULT;
        this.mode = cmd.hasOption("mode") ?
                Mode.get(cmd.getOptionValue("mode")) :
                MODE_DEFAULT;
        this.charset = cmd.hasOption("charset") ?
                Charset.forName(cmd.getOptionValue("charset")) :
                CHARSET_DEFAULT;
        this.models = cmd.hasOption("models") ?
                cmd.getOptionValue("models") :
                Paths.get("models", this.language.label).toString();
        this.wikisource = cmd.hasOption(wikisource);
        this.concise = cmd.hasOption(concise);
        AcrosticCluster.concise = this.concise;
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