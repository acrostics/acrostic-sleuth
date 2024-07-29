package acrostics;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class Main {

    public static void main(String[] args) {
        CLO clo = new CLO();
        int exitCode = new CommandLine(clo).execute(args);
        if (exitCode != 0 || clo.texts == null || clo.texts.length == 0) {
            System.exit(exitCode);
        }
        find(clo, System.out, null);
    }
    public static void find(CLO clo, PrintStream printStream, LanguageModel lm) {
        CharModel cm = new CharModel();
        Text[] texts = new Text[clo.texts.length];

        for (int i = 0; i < clo.texts.length; i++) {
            texts[i] = new Text(clo.texts[i].getPath(), clo);
        }

        if (clo.models == null
                || !new File(clo.models + CharModel.FILE_POSTFIX).exists()) {
            System.err.println("Building language models...");
            for (int i = 0; i < texts.length; i++) {
                System.err.print(i + " out of " +clo.texts.length + "\r");
                texts[i].load(cm,false, false);
                if (texts[i].isEmpty()) {
                    System.err.println(texts[i].location + " is empty");
                }
            }
            if (clo.models != null) {
                System.err.println("Saving language models...");
                cm.save(clo.models);
            }
        } else {
            System.err.println("Loading language models...");
            cm.load(clo.models);
        }
        lm = new LanguageModel(clo.models);

        System.err.println("Enumerating possible acrostics...");
        BoundedPriorityBlockingQueue<Acrostic> acrostics =
                new BoundedPriorityBlockingQueue<>(clo.outputSize);
        // TODO: Potentially initialize BPQ with a larger value
        ThreadPoolExecutor executor = (ThreadPoolExecutor)
                Executors.newFixedThreadPool(clo.workers);
        int taskCount = 0;
        for (Text text : texts) {
            taskCount++;
            AcrosticGenerator acrosticGenerator = new AcrosticGenerator(
                    acrostics, text, clo, cm, lm);
            executor.execute(acrosticGenerator);
        }
        executor.shutdown();

        // TODO: A better timeout system with feedback
        while (true) {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            long completedTaskCount = executor.getCompletedTaskCount();
            System.err.print(completedTaskCount + " out of " + taskCount + "\r");
            if (completedTaskCount == taskCount) {
                break;
            }
        }
        try {
            executor.awaitTermination(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        printResults(acrostics, printStream, clo);
    }

    /** Print results in the format specified by command line options */
    public static void printResults(BoundedPriorityBlockingQueue<Acrostic> acrostics, PrintStream printStream, CLO clo) {
        System.err.println("Clustering results...");
        ArrayList<Acrostic> allAcrostics = new ArrayList<>();
        Acrostic next;
        while ((next = acrostics.reversePull()) != null) {
            allAcrostics.add(next);
        }
        ArrayList<AcrosticCluster> acrosticClusters = AcrosticCluster.clusterAcrostics(allAcrostics, true);
        Collections.sort(acrosticClusters); // TODO: figure out the order
        System.err.println("Printing results...");
        if (clo.concise) {
            printStream.println(AcrosticCluster.HEADER_CONCISE);
        } else {
            printStream.println(AcrosticCluster.HEADER);
        }
        for (AcrosticCluster cluster: acrosticClusters) {
            printStream.println(cluster);
        }
    }

}
