package readability;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Main {
    public static int characters;
    public static int words;
    public static int sentences;
    public static int syllables;
    public static int polySyllables;
    public static String fileText;

    public static void main(String[] args) {
        fileText = readFile(args[0]);
        System.out.println("The text is:");
        System.out.println(fileText);
        System.out.println();
        characters = fileText.replaceAll("\\s", "").length();
        words = fileText.split("\\s+").length;
        sentences = fileText.trim().split("[!.?]+").length;
        syllables = syllablesInText(fileText);
        polySyllables = polySyllablesInText(fileText);

        System.out.printf("Words: %d\n", words);
        System.out.printf("Sentences: %d\n", sentences);
        System.out.printf("Characters: %d\n", characters);
        System.out.printf("Syllables: %d\n", syllables);
        System.out.printf("Polysyllables: %d\n", polySyllables);
        System.out.println("Enter the score you want to calculate (ARI, FK, SMOG, CL, all):");
        String selection = new Scanner(System.in).nextLine();
        System.out.println();
        printScores(selection);
    }

    private static void printScores(String selection) {
        switch (selection.toLowerCase()) {
            case "ari":
                printARI();
                break;
            case "fk":
                printFK();
                break;
            case "SMOG":
                printSMOG();
            case "CL":
                printCL();
            default:
                printARI();
                printFK();
                printSMOG();
                printCL();
                break;
        }
    }

    private static void printCL() {
        double L = characters / ((double) words / 100); // average number of characters per 100 words
        double S = sentences / ((double) words / 100); // average number of sentences per 100 words
        double score = 0.0588 * L - 0.296 * S - 15.8;
        int age = getAgeRange(score);
        String text = "Coleman–Liau index: %.2f (about %d-year-olds).\n";
        System.out.printf(text, score, age);
    }

    private static void printSMOG() {
        double score = 1.043 * Math.sqrt(polySyllables * (30.0 / sentences)) + 3.1291;
        int age = getAgeRange(score);
        String text = "Simple Measure of Gobbledygook: %.2f (about %d-year-olds).\n";
        System.out.printf(text, score, age);
    }

    private static void printFK() {
        double score = 0.39 * ((double) words / sentences) + 11.8 * ((double) syllables / words) - 15.59;
        int age = getAgeRange(score);
        String text = "Flesch–Kincaid readability tests: %.2f (about %d-year-olds).\n";
        System.out.printf(text, score, age);
    }

    public static void printARI() {
        double score = 4.71 * ((double) characters / words) + 0.5 * ((double) words / sentences) - 21.43;
        int age = getAgeRange(score);
        String format = "Automated Readability Index: %.2f (about %d-year-olds).\n";
        System.out.printf(format, score, age);
    }


    public static int syllablesInText(String text) {
        String[] words = text.replaceAll("[0-9,]", "").split("[!.?\\s]+");
        int totalSyllables = 0;
        for (String word : words) {
            if (word.isEmpty()) {
                continue;
            }
            totalSyllables += syllablesInWord(word);
        }

        return totalSyllables;
    }

    public static int syllablesInWord(String word) {
        int syllables = (int) Pattern.compile("[aeiouyAEIOUY]{2}|[aeiouyAEIOUY][^aeiouyAEIOUY\\s]|[aiouyAIOUY]$")
                .matcher(word.toLowerCase())
                .results()
                .count();

        return Math.max(syllables, 1);
    }

    public static int polySyllablesInText(String text) {
        String[] words = text.split("[!.?\\s]+");
        int totalSyllables = 0;
        for (String word : words) {
            if (word.isEmpty()) {
                continue;
            }

            boolean wordIsPolySyllable = syllablesInWord(word) > 2;
            if (wordIsPolySyllable) {
                totalSyllables += 1;
            }
        }

        return totalSyllables;
    }

    private static int getAgeRange(double score) {
        int rangeValue = (int) Math.ceil(score);
        int[] scoreAge = {6, 7, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 24};
        if (rangeValue > scoreAge.length) {
            rangeValue = scoreAge.length;
        }
        return scoreAge[rangeValue - 1];
    }

    public static String readFile(String path) {
        try {
            return new String(Files.readAllBytes(Paths.get(path)));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }
}
