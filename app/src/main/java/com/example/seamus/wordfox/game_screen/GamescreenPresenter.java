package com.example.seamus.wordfox.game_screen;

import android.widget.TextView;

import com.example.seamus.wordfox.GameData;
import com.example.seamus.wordfox.GameInstance;
import com.example.seamus.wordfox.MainActivity;
import com.example.seamus.wordfox.data.Diction;
import com.example.seamus.wordfox.database.FoxSQLData;
import com.example.seamus.wordfox.datamodels.RoundItem;
import com.example.seamus.wordfox.datamodels.WordItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;

import static com.example.seamus.wordfox.game_screen.GameActivity.GAME_TIME_SECONDS;

/**
 * Created by Gilroy.
 */

public class GamescreenPresenter implements GamescreenContract.Listener {
    private static final String MONITOR_TAG = "game_presenter";
    private final GamescreenContract.View view;
    private GameInstance gameInstance;
    private GameData gameData;
    private FoxSQLData foxData;
    private Diction dictionary;
    private Map<String, Boolean> allWordsSubmitted = new HashMap<>();
    private LinkedList<SingleCell> alreadyClicked = new LinkedList<>(); //TODO: LinkedHashMap so can search quickly
    private ArrayList<SingleCell> listOfGridCells;
    private String onGoingAttempt = "";

    GamescreenPresenter(GamescreenContract.View view, GameInstance gameInstance, Diction dictionary, FoxSQLData data, GameData gameData) {
        this.view = view;
        this.gameInstance = gameInstance;
        this.dictionary = dictionary;
        this.foxData = data;
        this.gameData = gameData;
        foxData.open();
        listOfGridCells = createLetters();
    }

    public void setup() {
        alreadyClicked.add(new SingleCell("", ""));      // TODO Fix this!
        // Clear longest word. Clear score for round but keep Total Score.
        clearRoundScores();
        // Write the letters to the heading
        printGridCells(listOfGridCells);
        view.updateHeaderLetters();
    }

    private ArrayList<SingleCell> createLetters() {
        // Generate a random sequence of 9 letters to use for the game
        ArrayList<String> givenLetters;
        String givenLettersSTR;
        if (gameInstance.getThisGameIndex() == 0 && gameInstance.getRoundLetters() == null) {
            givenLetters = dictionary.getGivenLetters();
            StringBuilder gameLetters = new StringBuilder();
            for (int i = 0; i < givenLetters.size(); i++) {
                gameLetters.append(givenLetters.get(i));
            }
            givenLettersSTR = gameLetters.toString();
            gameInstance.setLetters(givenLettersSTR);
            execBackgroundSetupTasks(givenLettersSTR);
        } else if (gameInstance.getRoundLetters() == null) {
            GameInstance playerOneInstance = MainActivity.allGameInstances.get(0);
            givenLettersSTR = playerOneInstance.getLetters(gameInstance.getRound());
            ArrayList<String> longestWordsPossibleForRound = playerOneInstance.getSuggestedWordsOfRound(gameInstance.getRound());
            // TODO: Instead, Create a partial copy constructor to take in values
            gameInstance.setLetters(givenLettersSTR);
            gameInstance.addListOfSuggestedWords(longestWordsPossibleForRound);
            gameInstance.setLongestPossible(playerOneInstance.getRoundLongestPossible(gameInstance.getRound()));

            givenLetters = getArrayFromLetters(givenLettersSTR);

        } else {      // If wifi multi player game
//            GameInstance playerOneInstance = MainActivity.allGameInstances.get(0);
            givenLettersSTR = gameInstance.getRoundLetters();
            givenLetters = getArrayFromLetters(givenLettersSTR);
            execBackgroundSetupTasks(givenLettersSTR);
        }
        ArrayList<SingleCell> gridCells = new ArrayList<>();

        for (int i = 0; i < givenLetters.size(); i++) {
            String allCellId = "guessGridCell" + (i + 1);
            gridCells.add(new SingleCell(allCellId, givenLetters.get(i)));
        }
        return gridCells;
    }

    public ArrayList<String> getArrayFromLetters(String letters){
        return new ArrayList<String>(
                Arrays.asList(
                        Arrays.copyOfRange(
                                letters.split(""), 1, letters.length() + 1))
        );
    }
    // Finding list of longest words is slow. Run on thread to not block the UI
    private void execBackgroundSetupTasks(String givenLettersSTR) {
        Thread thread = new Thread(() -> {
            calculateLongestPossibleWords(givenLettersSTR);
            createRoundItem(givenLettersSTR);
        });
        thread.start();
    }

    private void calculateLongestPossibleWords(String givenLettersSTR) {
        ArrayList<String> longestWordsPossible = dictionary.longestWordFromLetters(givenLettersSTR);
        gameInstance.setLongestPossible(longestWordsPossible.get(0));
        gameInstance.addListOfSuggestedWords(longestWordsPossible);
    }

    private void createRoundItem(String givenLettersSTR) {
        RoundItem thisRound = new RoundItem(gameInstance.getRoundID(), givenLettersSTR, gameInstance.getLongestPossible());
        foxData.createRoundItem(thisRound);
    }

    // Get the game letters
    public ArrayList<String> getLetters() {
        ArrayList<String> letters = new ArrayList<>();
        for (int i = 0; i < listOfGridCells.size(); i++) {
            letters.add(listOfGridCells.get(i).letter);
        }
        return letters;
    }

    // Get the game letters as one string
    public String getLettersSTR() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < listOfGridCells.size(); i++) {
            if (cellAlreadyClicked(listOfGridCells.get(i))) {
                stringBuilder.append(" ");
            } else {
                stringBuilder.append(listOfGridCells.get(i).letter);
            }
        }
        return stringBuilder.toString();
    }

    private boolean cellAlreadyClicked(SingleCell cell) {
        for (SingleCell clicked : alreadyClicked) {
            if (clicked.tag.equals(cell.tag)) {
                return true;
            }
        }
        return false;
    }

    private void startScoreScreen1Act() {
        view.startScoreScreen1Act(gameInstance.getThisGameIndex());
    }

    public int getRound() {
        return gameInstance.getRound();
    }

    // Create the timer segments
    public void addTimeBlocks(int height) {
        ArrayList<TextView> textViews = new ArrayList<>();
        int unitHeight = height / GAME_TIME_SECONDS;
        for (int i = 0; i < GAME_TIME_SECONDS; ++i) {
            textViews.add(
                    view.createTimeCounterSegment(unitHeight)
            );
        }
        view.createTimer(textViews);
    }

    private void createWordItem(WordItem word) {
        foxData.createWordItem(word);
    }

    private void clearRoundScores() {
        gameInstance.clearRoundScores();  // TODO: ... is this needed ..?
    }

    public void completeGame() {
        gameInstance.setRoundWord(gameInstance.getLongestWord());
        startScoreScreen1Act();
    }

    // Check if word is valid & longer than current best. If so, set as longest attempt.
    public void submitCurrentAttempt(String attempt) {
        // Set all letters to be chooseable again.
        setGridClickable();
        clearOnGoingAttempt();
        view.updateHeaderLetters();
        // All words in dictionary are lower case
        String lcAttempt = attempt.toLowerCase();
        // Keep track of what words the user has already tried
        boolean isValid = dictionary.checkWordExists(lcAttempt);
        allWordsSubmitted.put(lcAttempt, isValid);
        // Exit if not a valid real word
        if (!isValid) {
            view.makeToast("Word doesn't exist");
            return;
        }
        // If current attempt is longer than previous best attempt, accept word as new longest
        int longestStrLen = gameInstance.getLongestWord().length();
        int currentStrLen = lcAttempt.length();
        if (currentStrLen >= longestStrLen) {
            // Print longest word and its length to the screen
            view.setLongest(attempt);
            gameInstance.setLongestWord(attempt);
            gameInstance.setScore(currentStrLen);
        }
    }

    public void clearOnGoingAttempt() {
        onGoingAttempt = "";
    }

    // Update the stored data with information on words found during the game  // TODO: .. run this on separate thread
    public void updateData() {
        String longestWord = gameInstance.getLongestWord();
        gameData.gameCountUp();
        gameData.roundCountUp();
        boolean isBlankRound = true;
        for (Map.Entry<String, Boolean> word : allWordsSubmitted.entrySet()) {
            // The users best attempt at the end of the round is marked as the 'final' word
            boolean isFinal = false;
            if (word.getKey().toUpperCase().equals(longestWord)) {
                isFinal = true;
                isBlankRound = false;
            }
            // Write word to sql database
            UUID wordId = UUID.randomUUID();
            WordItem wordFoundByPlayer = new WordItem(
                    wordId, word.getKey(), gameInstance.getID(), word.getValue(), isFinal, gameInstance.getRoundID(gameInstance.getRound())
            );
            createWordItem(wordFoundByPlayer);
            // Update preferences file
            gameData.addWord(word.getKey());
            if (word.getValue()) {
                gameData.correctCountUp();
            } else {
                gameData.incorrectCountUp();
            }
        }
        if (isBlankRound) {
            WordItem wordFoundByPlayer = new WordItem(
                    UUID.randomUUID(), "<none>", gameInstance.getID(), false, true, gameInstance.getRoundID(gameInstance.getRound())
            );
            createWordItem(wordFoundByPlayer);
        }
    }

    // Detect if user clicks a cell in the 3x3 letter grid. Prevent choosing the same cell twice!
    public void gridCellClicked(String tag, String letter) {
        // Update the current attempt by appending the chosen letter
        String currentGuess = onGoingAttempt;
        String previousTag = "null";        // TODO do this better.
        if (!alreadyClicked.getLast().tag.equals("")) {
            previousTag = alreadyClicked.getLast().tag;
        }
        // If previous ID is same as new ID, that means user clicked same letter twice in a row
        if (!previousTag.equals(tag)) {      // If new color
            currentGuess += letter;             // Append the new letter
            if (!previousTag.equals("null")) {
                view.setCellOldClicked(previousTag);
            }
            alreadyClicked.add(new SingleCell(tag, letter));
            view.setCellNewlyClicked(tag);
        }
        // Since clicked twice in a row, set to 'not clicked'
        else {
            alreadyClicked.removeLast();
            view.setCellNotClicked(tag);
            String prePreviousTag = alreadyClicked.getLast().tag;
            if (!prePreviousTag.equals("")) {
                view.setCellNewlyClicked(prePreviousTag);
            }
            currentGuess = currentGuess.substring(0, currentGuess.length() - 1);
        }
        view.setCurrentAttempt(currentGuess);
        onGoingAttempt = currentGuess;
        view.updateHeaderLetters();
    }

    // Set the clickable attribute to true on each letter in the 3x3 grid. Run after word is submitted
    public void setGridClickable() {
        int cellCount = 9;
        for (int i = 0; i < cellCount; i++) {
            String allCellId = "guessGridCell" + (i + 1);
            view.setCellNotClicked(allCellId);
            alreadyClicked.clear();
            alreadyClicked.add(new SingleCell("", ""));      // TODO Fix this!
        }
    }

    // Print our 9 letters to the 9 cells in the 3x3 grid
    public void printGridCells(ArrayList<SingleCell> gridCells) {
        for (SingleCell cell : gridCells) {
            if (cell.letter.equals("")) {
                continue;
            }
            view.printGridCell(cell);
        }
    }

    // Randomly shuffle the locations of the letters
    // TODO: .. can't we keep a reference to the grid of textviews and shuffle those ..?!
    public void shuffleGivenLetters() {
        gameData.shuffleCountUp();
        // Shuffle the list containing the grid cells
        Collections.shuffle(listOfGridCells);
        // Map the old tags to the new ones. Set cells to 'Not Clicked' status
        HashMap<String, String> oldToNew = new HashMap<String, String>();
        for (int i = 0; i < listOfGridCells.size(); i++) {
            SingleCell singleCell = listOfGridCells.get(i);
            view.setCellNotClicked(singleCell.tag);
            String newCellTag = "guessGridCell" + (i + 1);
            oldToNew.put(singleCell.tag, newCellTag);   // Map changes to the resource IDs
            singleCell.tag = newCellTag;
        }
        // Highlight all the letters which have already been clicked
        for (SingleCell singleCellClicked : alreadyClicked) {
            if (singleCellClicked.tag.equals("")) {        // TODO: ... huh?!
                continue;
            }
            singleCellClicked.tag = oldToNew.get(singleCellClicked.tag);
            view.setCellOldClicked(singleCellClicked.tag);
        }
        // Most recently clicked cell is a different color
        if (!alreadyClicked.isEmpty()) {    // TODO: ... this can never be empty!  ..?
            view.setCellNewlyClicked(alreadyClicked.getLast().tag);
        }
        view.updateHeaderLetters();
        printGridCells(listOfGridCells);
    }
}
