package com.tim;

import java.io.*;
import java.util.*;

/**
 * A Reverse-Hangman game in which the user thinks of a word and the computer
 * tries to guess the letters in that word. The user tells the computer how many letters
 * the word contains and the guesses are made based on previous inputs. The computer has
 * only 8 guesses available.
 *
 * @version 1.0
 * @author Toader George-Catalin
 */
class Game {
    private final List<String> bank;
    private final List<Character> letters;
    private final int length;
    private final char[] wordFormed;
    private int guessesWrong;

    /**
     * The constructor of the class.
     *
     * @param length The length of the word chosen by the user.
     */
    public Game(final int length) {
        this.bank = new ArrayList<>();
        this.letters = new ArrayList<>();
        this.length = length;
        this.wordFormed = new char[length];
        for (int i = 0; i < length; i++) {
            wordFormed[i] = '_';
            System.out.print(wordFormed[i]);
        }
        System.out.println();
    }

    /**
     * Makes guesses using previous inputs.
     *
     * @return The result of the AskUserWord() method.
     */
    public boolean Play() {
        createBank();
        while (bank.size() != 1) {
            char key = GuessLetter();
            int[] indicies = AskUserLetter(key);
            RemoveWords(key, indicies);
            if (guessesWrong == 8 || bank.size() == 0)
                return false;
        }
        return AskUserWord();
    }

    /**
     * Makes the final guess with our found word.
     *
     * @return {@code true} if the word found is also the one chosen by the user
     *         {@code false} if not
     */
    private boolean AskUserWord() {
        System.out.println("Your chosen word is " + bank.get(0) + "?(y/n) ");
        return new Scanner(System.in).next().toLowerCase().contains("y");
    }

    /**
     * Checks if the word contains the letter.
     * If it does then it asks the user for all its positions in the chosen word and prints
     * the new formed word, else increment the wrong guesses.
     *
     * @param key The letter used as a guess.
     * @return An array containing the positions of the letter in the word.
     */
    private int[] AskUserLetter(final char key) {
        System.out.println("My guess is: " + key + " (y/n) ");
        Scanner s = new Scanner(System.in);
        String answer = s.next();
        List<Integer> indicies = new ArrayList<>();
        if (answer.toLowerCase().contains("y")) {
            int index = 0;
            while (index != -2) {
                boolean isNumeric = false;
                while(!isNumeric) {
                    try {
                        System.out.println("Enter the positions of the letter in your word (1-" + this.length + "). -1 to stop: ");
                        index = s.nextInt() - 1;
                        s.nextLine();
                        isNumeric = true;
                        System.out.println("Added letter.");
                        if (index != -2) {
                            wordFormed[index] = key;
                            indicies.add(index);
                        }
                    } catch(InputMismatchException e) {
                        /* Display Error message */
                        System.out.println("Invalid input, please try again:");
                        s.nextLine();
                    }
                }
            }
            for (int i = 0; i < length; i++)
                System.out.print(wordFormed[i]);
            System.out.println();
        } else {
            guessesWrong++;
            System.out.println("Currently have " + guessesWrong + " wrong guesses.");
        }

        return indicies.stream().filter(Objects::nonNull).mapToInt(i -> i).toArray();
    }

    /**
     * Finds the most common letter in the remaining words to make a guess with it.
     *
     * @return The letter to be used as a guess.
     */
    private char GuessLetter() {
        int[] frequency = new int[26];
        for (String word : bank)
            for (char chr : word.toCharArray())
                frequency[chr - 'a']++;
        char guess = FindMax(frequency);
        letters.add(guess);
        return guess;
    }

    /**
     * Finds the letter with the highest number of occurrences.
     * It is used as part of the GuessLetter() method.
     *
     * @param frequency The number of occurrences of each letter.
     * @return The most frequent/common letter.
     */
    private char FindMax(final int[] frequency) {
        int maxIndex = 0;
        int max = 0;
        for (int i = 0; i < 26; i++) {
            if (frequency[i] > max && !letters.contains((char)(i + 97))) {
                max = frequency[i];
                maxIndex = i;
            }
        }
        return (char)(maxIndex + 97);
    }

    /**
     * Removes words that do not meet the new conditions.
     * There are 2 cases, either the user's word does not contain the letter and the words
     * that contain it are removed, or it contains it and the words not respecting the indices are removed.
     *
     * @param key The letter used as a guess.
     * @param indices An array containing the positions of the letter in the word.
     */
    private void RemoveWords(final char key, final int[] indices) {
        List<String> copy = new ArrayList<>(bank);
        if (indices.length == 0) {
            copy.stream()
                    .filter(word -> word.contains(Character.toString(key)))
                    .forEach(bank::remove);
        }
        else {
            copy.forEach(word -> Arrays.stream(indices)
                    .filter(index -> word.charAt(index) != key)
                    // ATENTIE MARE AICI!!! mapToObj aici e ca si cum ar face "index -> return word", deci singurul rol
                    // e acela de a returna cuvantul mare pentru a-l putea sterge apoi, index nu face absolut nimic
                    .mapToObj(index -> word)
                    .forEach(bank::remove));
        }
        System.out.println(bank.size() + " remaining possibilities.");
    }

    /**
     * Reads the words from a file containing the English Dictionary and
     * adds the ones that meet the length condition to the list.
     */
    private void createBank() {
        try (BufferedReader br = new BufferedReader(new FileReader("wordBank.txt"))) {
            String word;
            while ((word = br.readLine()) != null) {
                if (word.length() == this.length)
                    bank.add(word);
            }
        } catch (Exception e) {
            System.out.println("File not available.");
        }
    }
}

public class Main {

    /**
     * The main method.
     * @param args Command line arguments.
     */
    public static void main(final String[] args) {
        System.out.println("Welcome, this program is named Reverse-Hangman.");
        System.out.println("Enter the number of letters your chosen word has: ");
	    Game game = new Game(new Scanner(System.in).nextInt());
        boolean result = game.Play();
        System.out.println(result ? "I won." : "I lost.");
        System.out.println("Thanks for playing, see you next time.");
    }
}
