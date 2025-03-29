package workshop05code;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;
//Included for the logging exercise
import java.io.FileInputStream;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 *
 * @author sqlitetutorial.net
 */
public class App {
    static {
        try {
            LogManager.getLogManager().readConfiguration(new FileInputStream("resources/logging.properties"));
        } catch (SecurityException | IOException e) {
            e.printStackTrace();
        }
    }

    private static final Logger logger = Logger.getLogger(App.class.getName());
    // End code for logging exercise
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SQLiteConnectionManager wordleDatabaseConnection = new SQLiteConnectionManager("words.db");

        wordleDatabaseConnection.createNewDatabase("words.db");
        if (wordleDatabaseConnection.checkIfConnectionDefined()) {
            logger.info("Wordle created and connected.");
        } else {
            logger.severe("Not able to connect. Sorry!");
            return;
        }
        if (wordleDatabaseConnection.createWordleTables()) {
            logger.info("Wordle structures in place.");
        } else {
            logger.severe("Not able to launch. Sorry!");
            return;
        }

        // let's add some words to valid 4 letter words from the data.txt file

        try (BufferedReader br = new BufferedReader(new FileReader("resources/data.txt"))) {
            String line;
            int i = 1;
            while ((line = br.readLine()) != null) {
                if (line.matches("[a-z]{4}")) {
                    logger.info("Valid word added: " + line);
                    wordleDatabaseConnection.addValidWord(i, line);
                } else {
                    logger.severe("Invalid word in data.txt: " + line);
                }
                i++;
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to read data.txt.", e);
        }

        // let's get them to enter a word

        try (Scanner scanner = new Scanner(System.in)) {
            String guess;
            do {
                System.out.print("Enter a 4 letter word for a guess or q to quit: ");
                guess = scanner.nextLine();
    
                // Check if the guess is "q" to exit
                if (guess.equals("q")) break;
    
                // Validate the input (4 letters, lowercase a-z)
                if (guess.matches("[a-z]{4}")) {
                    System.out.println("You've guessed '" + guess + "'.");
    
                    if (wordleDatabaseConnection.isValidWord(guess)) {
                        System.out.println("Success! It is in the list.\n");
                    } else {
                        System.out.println("Sorry. This word is NOT in the list.\n");
                    }
                } else {
                    System.out.println("Invalid input, enter 4 digit word\n");
                    logger.warning("Invalid user guess: " + guess);
                }
    
            } while (!guess.equals("q"));
        } catch (NoSuchElementException | IllegalStateException e) {
            logger.log(Level.SEVERE, "Error handling user input.", e);
        }
    }
}
