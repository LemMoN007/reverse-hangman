# Reverse-Hangman

## How it works
A Reverse Hangman game in which the user thinks of a word and the computer
tries to guess the letters in that word. The user tells the computer how many letters
the word contains.

The program outputs what the computer guessed on each turn, and show the
partially completed word. It also uses optimizations to reduce the number of possible words
based on the user's input. For example, it does not simply try all the letters in order, it 
finds the most common letter in the remaining available words.
