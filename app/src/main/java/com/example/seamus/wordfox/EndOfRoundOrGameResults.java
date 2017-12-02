package com.example.seamus.wordfox;

import android.app.Activity;
import android.support.v7.app.ActionBar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Desmond on 03/11/2017.
 */

public class EndOfRoundOrGameResults{

    public static final String MONITOR_TAG = "EndOfRoundOrGameResults: ";
    public Activity activity;
    //declare a String for the heading at the top of the results page (Game Over OR Time Up!)
    private String resultsHeading = "";
    //declare a String to display who won
    private String victoryMessage = "";
    //declare a String for the greeting message (You Scored OR PlayerName Scored)
    private String GreetingMsg = "";
    //declare a String to display the players score relative to the max possible score
    private String resultsRatio = "";
    //declare a String to display the player's percentage
    private String resultsPercent;
    //declare a String to be used to combine the GreetingMsg, resultsRatio (and the resultsPercent if necessary)
    private String ResultMsg = "";

    //declare an integer for the number of players
    private int numPlayers;
    //declare an integer for the player's score at the end of the round
    private int score;
    //declare an integer for the max possible score at the end of the round
    private int maxPossibleRoundScore;
    //declare an integer for the player's totalScore at the end of the game
    private int totalScore;
    //declare an integer for the max possible score at the end of the game
    private int maxPossibleGameScore;
    //declare a Double (object) to be used to calculate the player's percentage
    private Double successPercent;

    private LinearLayout.LayoutParams lp;

    public LinearLayout.LayoutParams getLp() {
        return lp;
    }

    public void setLp(LinearLayout.LayoutParams lp) {
        this.lp = lp;
    }

    public EndOfRoundOrGameResults(Activity _activity, String roundOrGameEnd){
        this.activity = _activity;
        this.numPlayers = MainActivity.allGameInstances.size();

        // create a set of parameters for a linear layout setting the width and height to be
        // wrap content to be used on each textview created later on
       this.lp = new LinearLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT); // width is first then height

        if (roundOrGameEnd == "round"){
            //do what you need for the end of a round
            populateHeaderMsg(roundOrGameEnd);
            populateResultLL(roundOrGameEnd);
            createRoundSummary(roundOrGameEnd);

        }else if (roundOrGameEnd == "game"){
            //do what you need for the end of the game
            populateHeaderMsg(roundOrGameEnd);
            populateResultLL(roundOrGameEnd);

            // create an array of playersFinalScoresNNames objects for the players' scores and names
            // at the end of the game
            playersFinalScoresNNames[] playersFinalScoresNNamesArr = new playersFinalScoresNNames[numPlayers];

            for (int k=0; k<numPlayers; k++) {
                // for each player declare and initialize a new playersFinalScoresNNames object with
                // the associated totalScore and playername and add it into playersFinalScoresNNamesArr


                // create a reference to the object instance of the GameInstance class created in
                // the main activity
                GameInstance myGameInstance = MainActivity.allGameInstances.get(k);
                //declare and initialise an integer for the player's total score at the end of the game
                int totalScore = myGameInstance.getTotalScore();

                GameData myGameData = new GameData(this.activity, k);
                String playername = myGameData.getUsername();


                playersFinalScoresNNames myPlayersFinalScoresNNames = new playersFinalScoresNNames(totalScore, playername);
                playersFinalScoresNNamesArr[k] = myPlayersFinalScoresNNames;

            }

            Log.d("Hello", "xxxx 3");

            //get the name of the player with the highest score and set it to be the winner or if
            // there's a draw, say who drew
            if (numPlayers>1){

                //create an array of strings to hold the name(s) of the player(s) with the highest score
                ArrayList<String> winners = playersWithHighestScore(playersFinalScoresNNamesArr);

                String victoryMessage = "";
                Log.d("Hello", "xxxx 3.1");

                if (winners.size() == 1){
                    //if there's only one name in the list of winners then it wasn't a draw and that player won
                    Log.d("Hello", "xxxx 3.2");
                    victoryMessage = "Winner is " + winners.get(0) + "!";
                }else {
                    //if there's more than one name in the list of winners then it was a draw
                    victoryMessage = "It was a draw between ";

                    for (int f = 0; f<winners.size(); f++){
                        Log.d("Hello", "xxxx 3.3");

                        if (f == (winners.size() - 1)){
                            //if you're entering the name of the last player to tie the score,
                            // end with an !
                            victoryMessage = (victoryMessage + winners.get(f) + "!");
                        }else{
                            victoryMessage = (victoryMessage + winners.get(f) + " and ");
                        }

                    }

                }

                TextView winnerTV = createTVwithText(victoryMessage);
                Log.d("Hello", "xxxx 3.6");
                winnerTV.setGravity(Gravity.CENTER);

                //add the textview with the winner's name into the LinearLayout containing the Game Over message
                LinearLayout gameOverWinnerLL = (LinearLayout) this.activity.findViewById(R.id.gameOverWinnerLLEndScreen);
                winnerTV.setLayoutParams(lp);
                gameOverWinnerLL.addView(winnerTV);
            }

            createRoundSummary(roundOrGameEnd);

        }
    }

    private void createRoundSummary(String roundOrGameEnd) {
        LinearLayout playersLL = (LinearLayout) this.activity.findViewById(R.id.playersEndScreenLL);
        LinearLayout wordsLL = (LinearLayout) this.activity.findViewById(R.id.wordsEndScreenLL);

        int count = 0;

        // if this method is being called for the end of a game, you'll need to do the
        // below for loop for each player. If it's just the end of a round, you'll only need to do
        // the for loop once, for the player that just completed the round
        if(roundOrGameEnd == "game"){
            count = GameInstance.getMaxNumberOfRounds();
        }else if (roundOrGameEnd == "round"){
            count = 1;
        }

        int steps = 0;

        // if this method is being called for the end of a game, you'll need to do the
        // below for loop for each player. If it's just the end of a round, you'll only need to do
        // the for loop once, for the player that just completed the round
        if(roundOrGameEnd == "game"){
            steps = numPlayers;
        }else if (roundOrGameEnd == "round"){
            steps = 1;
        }

        //loop through the numbers of rounds, doing the following:
        //add margin at the top of the round summary
        // add the word 'Round' followed by the correct round number
        // add the words 'Best Word:'
        //Opposite do the following
        //add the round letters
        //add the best possible word
        for (int j=0; j<count; j++){
            // create and add a TextView displaying the current round


            String roundNo ="";
            if(roundOrGameEnd == "game"){
                roundNo = "Round " + String.valueOf(j+1) + ": ";
            }else if (roundOrGameEnd == "round"){
                roundNo = "Round " + String.valueOf(MainActivity.allGameInstances.get(j).getRound()) + ": ";
            }

            TextView roundTV = createTVwithText(roundNo);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, dpTOpx(50), 0, 0);
            roundTV.setLayoutParams(params);
            playersLL.addView(roundTV);
            Log.d("Hello", "xxxx 4 " + j);

            // create and add a TextView displaying the title 'Best Word: '
            TextView bestWordTitleTV = createTVwithText("Best Word: ");
            bestWordTitleTV.setLayoutParams(lp);
            playersLL.addView(bestWordTitleTV);
            Log.d("Hello", "xxxx 4.1 " + j);

            // create a reference to the object instance of the GameInstance class created in the main
            // activity so that you can get the letters for each round
            GameInstance thisGameInstance = MainActivity.allGameInstances.get(0);

            // create and add a TextView displaying the letters for the round
            TextView lettersTV = createTVwithText(thisGameInstance.getLetters(j));
            lettersTV.setLayoutParams(params);
            wordsLL.addView(lettersTV);
            Log.d("Hello", "xxxx 4.2 " + j);

            //get the best possible word
            // create and add a TextView displaying the best possible word for the round
            TextView bestPossibleWordTV = createTVwithText(thisGameInstance.getRoundLongestPossible(j) + " (" + String.valueOf(thisGameInstance.getRoundLongestPossible(j).length()) + ")");
            bestPossibleWordTV.setLayoutParams(lp);
            wordsLL.addView(bestPossibleWordTV);
            Log.d("Hello", "xxxx 4.3 " + j);


            // for the end of a round only do this step for the one player currently playing,
            // for the end of the game do this step for each player, doing the following:
            // adding a new TextView for the players' names
            // Opposite add a new TextView for the players' best words
            for (int i = 0 ; i<steps; i++){
                Log.d("Hello", "xxxx 5 " + i);

                // create an object instance of the GameData class to get the stored username(s)
                GameData myGameData = new GameData(this.activity, i);
                // declare and initialise a String to hold the player's name retrieved from the GameData class
                String name = myGameData.getUsername();

                TextView nameTV = createTVwithText(name + ": ");
                nameTV.setLayoutParams(lp);
                playersLL.addView(nameTV);

                // create a reference to the object instance of the GameInstance class created in the main activity
                GameInstance myGameInstance = MainActivity.allGameInstances.get(i);
                TextView bestGuessTV = createTVwithText(myGameInstance.getRoundXWord(j+1) + " (" + String.valueOf(myGameInstance.getRoundXWord(j+1).length()) +")");
                bestGuessTV.setLayoutParams(lp);
                wordsLL.addView(bestGuessTV);
            }

        }

    }

    private void populateHeaderMsg(String roundOrGameEnd) {
        // get the textview that will display the header message on the end screen, either
        // GAME OVER for the end of a game or TIME UP for the end of a round
        TextView gameOverTV = (TextView) this.activity.findViewById(R.id.gameOverEndScreenTV);

        if (roundOrGameEnd == "round"){
            //do what you need for the end of a round
            gameOverTV.setText("TIME UP YOU CUNT");
        }else if (roundOrGameEnd == "game") {
            //do what you need for the end of the game

            // if there's more than one player set the text of the gameOverTV on the end screen to say 'GAME OVER BITCHEZ'
            if (numPlayers > 1) {
                gameOverTV.setText("GAME OVER BITCHEZ");
            } else {
                //if there's only one player set the text of the gameOverTV on the end screen to say 'GAME OVER'
                //followed by their name
                // retrieve the player's name from the GameData class, store in a string to display in the heading
                GameData myGameData = new GameData(this.activity, 0);
                String playername = myGameData.getUsername();
                gameOverTV.setText("GAME OVER " + playername.toUpperCase());
            }
            Log.d("Hello", "xxxx 1");
        }
    }

    private void populateResultLL(String roundOrGameEnd) {

        LinearLayout resultsLL = (LinearLayout) this.activity.findViewById(R.id.resultEndScreenLL);

        int steps = 0;

        // if this method is being called for the end of a game, you'll need to do the
        // below for loop for each player. If it's just the end of a round, you'll only need to do
        // the for loop once, for the player that just completed the round
        if(roundOrGameEnd == "game"){
            steps = numPlayers;
        }else if (roundOrGameEnd == "round"){
            steps = 1;
        }


        //for the number of players add a line stating the score each player achieved to the
        // horizontal linear layout called resultsLL
        for (int k=0; k<steps; k++){

            //create empty strings to be filled with the individual components of the result
            String result = "";
            String playername = "";
            String greeting = "";

            if (numPlayers == 1 || roundOrGameEnd == "round"){
                // if there's only one player, or its just the end of a round, the greeting should
                // say, 'You scored '
                greeting = "You scored ";
            }else {
                // if there's more than one player, or it's the end of the game the greeting should
                // say, '#name scored'

                //get the player name
                GameData myGameData = new GameData(this.activity, k);
                playername = myGameData.getUsername();
                greeting = playername + " scored ";
            }
            Log.d("Hello", "xxxx 2 iteration no." + k);

            // create a reference to the object instance of the GameInstance class created in the main activity
            GameInstance myGameInstance = MainActivity.allGameInstances.get(k);
            //declare and initialise an integer for the player's total score at the end of the game
            int totalScore = myGameInstance.getTotalScore();
            //declare and initialise an integer for the max possible score at the end of the game
            int maxScore = myGameInstance.getHighestPossibleScore();
            //set the text of the resultsRatioTV on the end screen to show the users points
            String resultsRatio = String.valueOf(totalScore) + " out of " + String.valueOf(maxScore) + " = ";

            //declare and initialise a double for the percent success at the end of the game
            Double successPercent = (Double.valueOf(totalScore)/Double.valueOf(maxScore)*100);
            String resultsPercent = String.valueOf(successPercent.intValue()) + "%";

            //assemble the result to contain all the components and show in the TextView
            result = greeting + resultsRatio + resultsPercent;
            TextView resultTV = createTVwithText(result);
            resultTV.setLayoutParams(lp);
            resultsLL.addView(resultTV);
        }
    }

    private ArrayList<String> playersWithHighestScore(playersFinalScoresNNames[] playersFinalScoresNNamesArr) {
        int maxScore = 0;
        // declare an empty ArrayList of Strings to hold the names of the player(s) with the high score
        ArrayList<String> winners = new ArrayList<>();

        // go through each element of the playersFinalScoresNNamesArr checking each score to find
        // the max score, then add the associated PlayerName to the ArrayList of winners names
        for (int i=0; i< playersFinalScoresNNamesArr.length; i++){
            int score = playersFinalScoresNNamesArr[i].getEndOfGameScore();

            if (score>maxScore){
                maxScore = score;
                winners.clear();
                winners.add(playersFinalScoresNNamesArr[i].getPlayerName());
            }else if(score == maxScore){
                winners.add(playersFinalScoresNNamesArr[i].getPlayerName());
            }
        }

        return winners;

    }

    public int dpTOpx (int dp){
        DisplayMetrics displayMetrics = this.activity.getResources().getDisplayMetrics();
        return (int)((dp * displayMetrics.density) + 0.5);
    }

    public TextView createTVwithText(String text4TV){
        TextView newTV = new TextView(this.activity);
        newTV.setText(text4TV);
        return newTV;
    }
}
