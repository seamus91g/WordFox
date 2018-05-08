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
 * Created by Gilroy on 4/17/2018.
 */

public class GamescreenPresenter implements GamescreenContract.Listener {
    private static final String MONITOR_TAG = "game_presenter";
    private final GamescreenContract.View view;
    private GameInstance gameInstance;
    private GameData gameData;
    private FoxSQLData foxData;
    private Diction dictionary;
    private Map<String, Boolean> allWordsSubmitted = new HashMap<>();
    private LinkedList<SingleCell> alreadyClicked = new LinkedList<>();
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
        if (gameInstance.getThisGameIndex() == 0) {
            givenLetters = dictionary.getGivenLetters();
            StringBuilder gameLetters = new StringBuilder();
            for (int i = 0; i < givenLetters.size(); i++) {
                gameLetters.append(givenLetters.get(i));
            }
            givenLettersSTR = gameLetters.toString();
            ArrayList<String> longestWordsPossible = dictionary.longestWordFromLetters(givenLettersSTR);
            gameInstance.setLongestPossible(longestWordsPossible.get(0));
            gameInstance.addListOfSuggestedWords(longestWordsPossible);
            RoundItem thisRound = new RoundItem(gameInstance.getRoundID(), givenLettersSTR, gameInstance.getLongestPossible());
            foxData.createRoundItem(thisRound);

        } else {      // If multi player game, re-use the same letters
            GameInstance playerOneInstance = MainActivity.allGameInstances.get(0);
            givenLettersSTR = playerOneInstance.getLetters(gameInstance.getRound());
            gameInstance.setLongestPossible(playerOneInstance.getRoundLongestPossible(gameInstance.getRound()));

            ArrayList<String> longestWordsPossibleForRound = playerOneInstance.getSuggestedWordsOfRound(gameInstance.getRound());
            gameInstance.addListOfSuggestedWords(longestWordsPossibleForRound);

            givenLetters = new ArrayList<>(
                    Arrays.asList(
                            Arrays.copyOfRange(
                                    givenLettersSTR.split(""), 1, givenLettersSTR.length() +1))
            );
        }
        gameInstance.setLetters(givenLettersSTR);
        ArrayList<SingleCell> gridCells = new ArrayList<>();

        for (int i = 0; i < givenLetters.size(); i++) {
            String allCellId = "guessGridCell" + (i + 1);
            gridCells.add(new SingleCell(allCellId, givenLetters.get(i)));
        }
        return gridCells;
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
        StringBuilder givenLettersSTR = new StringBuilder();
        for (int i = 0; i < listOfGridCells.size(); i++) {
            givenLettersSTR.append(listOfGridCells.get(i).letter);
        }
        return givenLettersSTR.toString();
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

    public void createWordItem(WordItem word) {
        foxData.createWordItem(word);
    }

    public void clearRoundScores() {
        gameInstance.clearRoundScores();  // TODO: ... is this needed ..?
    }

    public void completeGame() {
        int currentRound = gameInstance.getRound();
        switch (currentRound) {
            case 0:
                gameInstance.setRound1Word(gameInstance.getLongestWord());
                gameInstance.setRound1Length(gameInstance.getLongestWord().length());
//                Log.d(MONITOR_TAG, "CURRENT ROUND, WORD FOR THIS ROUND, " + currentRound + myGameInstance.getLongestWord());
                break;
            case 1:
                gameInstance.setRound2Word(gameInstance.getLongestWord());
                gameInstance.setRound2Length(gameInstance.getLongestWord().length());
//                Log.d(MONITOR_TAG, "CURRENT ROUND, WORD FOR THIS ROUND, " + currentRound + myGameInstance.getLongestWord());
                break;
            case 2:
                gameInstance.setRound3Word(gameInstance.getLongestWord());
                gameInstance.setRound3Length(gameInstance.getLongestWord().length());
//                Log.d(MONITOR_TAG, "CURRENT ROUND, WORD FOR THIS ROUND, " + currentRound + myGameInstance.getLongestWord());
                break;
            default:
                break;
        }
        startScoreScreen1Act();
    }

    // Check if word is valid & longer than current best. If so, set as longest attempt.
    public void submitCurrentAttempt(String attempt) {
        // Set all letters to be chooseable again.
        setGridClickable();
        onGoingAttempt = "";
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

    // Update the stored data with information on words found during the game  // TODO: .. run this on separate thread
    public void updateData() {
        for (Map.Entry<String, Boolean> word : allWordsSubmitted.entrySet()) {
            // The users best attempt at the end of the round is marked as the 'final' word
            String longestWord = gameInstance.getLongestWord();
            boolean isFinal = false;
            if (word.getKey().equals(longestWord)) {
                isFinal = true;
            }
            // Write word to sql database
            String wordId = UUID.randomUUID().toString();
            WordItem wrongWord = new WordItem(
                    wordId, word.getKey(), gameInstance.getPlayerID(), word.getValue(), isFinal, gameInstance.getRoundID(gameInstance.getRound())
            );
            createWordItem(wrongWord);
            // Update preferences file
            gameData.addWord(word.getKey());
            if (word.getValue()) {
                gameData.correctCountUp();
            } else {
                gameData.incorrectCountUp();
            }
        }
        gameData.gameCountUp();
        gameData.roundCountUp();
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
