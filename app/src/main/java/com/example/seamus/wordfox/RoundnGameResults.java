package com.example.seamus.wordfox;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.seamus.wordfox.database.FoxSQLData;
import com.example.seamus.wordfox.datamodels.GameItem;

import java.util.ArrayList;

public class RoundnGameResults extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private NavigationBurger navBurger = new NavigationBurger();

    public static final String MONITOR_TAG = "EndScreen: ";
    private static final String TAG = "RoundnGameResults" ;
    private boolean backButtonPressedOnce = false;
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
    private int gameIndexNumber;
    //declare a Double (object) to be used to calculate the player's percentage
    private Double successPercent;


    private LinearLayout.LayoutParams lp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_screen_single_player1);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        String roundOrGameEnd = getIntent().getExtras().getString("key");
        gameIndexNumber = getIntent().getExtras().getInt("gameIndexNumber");

        Log.v(MONITOR_TAG, "IT'S THE END OF A: " + roundOrGameEnd);
        EndOfRoundOrGameResults(roundOrGameEnd);
    }

    public void EndOfRoundOrGameResults(String roundOrGameEnd){
//        this.activity = _activity;
        numPlayers = MainActivity.allGameInstances.size();

        // create a set of parameters for a linear layout setting the width and height to be
        // wrap content to be used on each textview created later on
        lp = new LinearLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT); // width is first then height

        if (roundOrGameEnd.equals("round")){
            //do what you need for the end of a round
            Log.d(MONITOR_TAG, "IT'S THE END OF A ROUND: " + roundOrGameEnd);
            populateHeaderMsg(roundOrGameEnd);
            populateResultLL(roundOrGameEnd);
            createRoundSummary(roundOrGameEnd);

        }else if (roundOrGameEnd.equals("game")){
            //do what you need for the end of the game
            Log.d(MONITOR_TAG, "IT'S THE END OF A GAME: " + roundOrGameEnd);

            populateHeaderMsg(roundOrGameEnd);
            populateResultLL(roundOrGameEnd);

            //getWinnerOrDraw
            // create an array of playersFinalScoresNNames objects for the players' scores and names
            // at the end of the game
//            playersFinalScoresNNames[] playersFinalScoresNNamesArr = new playersFinalScoresNNames[numPlayers];

//            for (int k=0; k<numPlayers; k++) {
//                // for each player declare and initialize a new playersFinalScoresNNames object with
//                // the associated totalScore and playername and add it into playersFinalScoresNNamesArr
//
//
//                // create a reference to the object instance of the GameInstance class created in
//                // the main activity
//                GameInstance myGameInstance = MainActivity.allGameInstances.get(k);
//                //declare and initialise an integer for the player's total score at the end of the game
//                int totalScore = myGameInstance.getTotalScore();
//
//                String playername = getPlayerNameorID(k);
//
//
//
////                GameData myGameData = new GameData(this, k);
////                String playername = myGameData.getUsername();
//
//
//                playersFinalScoresNNames myPlayersFinalScoresNNames = new playersFinalScoresNNames(totalScore, playername);
//                playersFinalScoresNNamesArr[k] = myPlayersFinalScoresNNames;
//
//            }

            Log.d("Hello", "xxxx 3");

            //get the name of the player with the highest score and set it to be the winner or if
            // there's a draw, say who drew

            //create an array of strings to hold the name(s) of the player(s) with the highest score
            ArrayList<GameInstance> winners = playersWithHighestScore();

            GameItem thisGameDetails = gameitemFromInstances(winners);
            FoxSQLData foxData = new FoxSQLData(this);
            foxData.open();
            foxData.createGameItem(thisGameDetails);

            if (numPlayers>1){

                victoryMessage = "";
                Log.d("Hello", "xxxx 3.1 victoryMessage is: " + victoryMessage);

                if (winners.size() == 1){
                    //if there's only one name in the list of winners then it wasn't a draw and that player won

                    String winnerName = winners.get(0).getPlayerID();
                    if (winnerName.equals(GameData.DEFAULT_P1_NAME)){
                        GameData fox = new GameData(this, GameData.DEFAULT_P1_NAME);
                        winnerName = fox.getUsername();
                    }

                    victoryMessage = "Winner is " + winnerName + "!";
                    Log.d("Hello", "xxxx 3.2 victoryMessage is: " + victoryMessage);
                }else {
                    //if there's more than one name in the list of winners then it was a draw
                    victoryMessage = "It was a draw between ";

                    for (int f = 0; f<winners.size(); f++){
                        Log.d("Hello", "xxxx 3.3 victoryMessage is: " + victoryMessage);

                        if (f == (winners.size() - 1)){
                            //if you're entering the name of the last player to tie the score,
                            // end with an !
                            victoryMessage = (victoryMessage + getPlayerNameorID(f) + "!");
                        }else{
                            victoryMessage = (victoryMessage + getPlayerNameorID(f) + " and ");
                        }
                    }
                }

                TextView winnerTV = createTVwithText(victoryMessage);
                Log.d("Hello", "xxxx 3.6 victoryMessage is: " + victoryMessage);
                winnerTV.setGravity(Gravity.CENTER);

                //add the textview with the winner's name into the LinearLayout containing the Game Over message
                LinearLayout gameOverWinnerLL = (LinearLayout) findViewById(R.id.gameOverWinnerLLEndScreen);
                winnerTV.setLayoutParams(lp);
                gameOverWinnerLL.addView(winnerTV);
            }

            createRoundSummary(roundOrGameEnd);

        }
    }

    private GameItem gameitemFromInstances(ArrayList<GameInstance> gInstances){
        StringBuilder winWords1 = new StringBuilder();
        StringBuilder winWords2 = new StringBuilder();
        StringBuilder winWords3 = new StringBuilder();
        StringBuilder winNames = new StringBuilder();
        for(GameInstance g : gInstances){
            winWords1.append(g.getRound1Word());
            winWords1.append(", ");
            winWords2.append(g.getRound2Word());
            winWords2.append(", ");
            winWords3.append(g.getRound3Word());
            winWords3.append(", ");
            winNames.append(g.getPlayerID());
            winNames.append(", ");
        }
        String ww1 = winWords1.length() > 0 ? winWords1.substring(0, winWords1.length() - 2): "";
        String ww2 = winWords2.length() > 0 ? winWords2.substring(0, winWords2.length() - 2): "";
        String ww3 = winWords3.length() > 0 ? winWords3.substring(0, winWords3.length() - 2): "";
        String wn = winNames.length() > 0 ? winNames.substring(0, winNames.length() - 2): "";

        GameInstance myGameInstance = MainActivity.allGameInstances.get(0);
        String round1Id = myGameInstance.getRoundID(0);
        String round2Id = myGameInstance.getRoundID(1);
        String round3Id = myGameInstance.getRoundID(2);
                GameItem thisGame = new GameItem(
                round1Id, round2Id, round3Id, ww1, ww2, ww3, wn, numPlayers
        );
        return thisGame;
    }

    private String getCslFromList(ArrayList<String> strings){
        StringBuilder strBl = new StringBuilder();
        for(String string : strings) {
            strBl.append(string);
            strBl.append(",");
        }
        return strBl.length() > 0 ? strBl.substring(0, strBl.length() - 1): "";
    }

    private void createRoundSummary(String roundOrGameEnd) {
        Log.d(TAG, "createRoundSummary: roundOrGameEnd is: " + roundOrGameEnd);
        LinearLayout playersLL = (LinearLayout) findViewById(R.id.playersEndScreenLL);
        LinearLayout wordsLL = (LinearLayout) findViewById(R.id.wordsEndScreenLL);

        Button endOfRoundOrGameButton = (Button) findViewById(R.id.endOfRoundOrGameButton);

        int rounds = 0;

        // if this method is being called for the end of a game, you'll need to do the
        // below for loop for each player. If it's just the end of a round, you'll only need to do
        // the for loop once, for the player that just completed the round
        if(roundOrGameEnd.equals("game")){
            rounds = GameInstance.getMaxNumberOfRounds();
        }else if (roundOrGameEnd.equals("round")){
            rounds = 1;
        }

        int players = 0;

        // if this method is being called for the end of a game, you'll need to do the
        // below for loop for each player. If it's just the end of a round, you'll only need to do
        // the for loop once, for the player that just completed the round
        if(roundOrGameEnd.equals("game")){
            players = numPlayers;
        }else if (roundOrGameEnd.equals("round")){
            players = 1;
        }

        //loop through the numbers of rounds, doing the following:
        //add margin at the top of the round summary
        // add the word 'Round' followed by the correct round number
        // add the words 'Best Word:'
        //Opposite do the following
        //add the round letters
        //add the best possible word
        for (int j=0; j<rounds; j++){
            // create and add a TextView displaying the current round
            String roundNo ="";

            int round = MainActivity.allGameInstances.get(gameIndexNumber).getRound();
            // if it's the end of the game, each round number will need to be displayed, also on the
            // final iteration of the loop set the title of the screen to be 'Final Results' and
            // the button at the bottom to say 'Next' and  to go home when pressed
            if(roundOrGameEnd.equals("game")){

                roundNo = "Round " + String.valueOf(j+1) + ": ";

                //final iteration of loop
                if (j==rounds-1){
                    this.setTitle("Final Results");
                    endOfRoundOrGameButton.setText("Home");
                    endOfRoundOrGameButton.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            Intent MainIntent = new Intent(v.getContext(), MainActivity.class);
                            startActivity(MainIntent);
                        }
                    });
                }
            }else if (roundOrGameEnd.equals("round")){

                // this works for single player but not for multi player - if it's the end of the
                // round in a multiplayer game, j will be 0 but by the time you go to a new player,
                // the round number for player0 will have reached 3 making this int become 3


//                int round = MainActivity.allGameInstances.get(gameIndexNumber).getRound();
                this.setTitle("Round " + String.valueOf(round+1) + " Score");
                roundNo = "Round " + String.valueOf(round+1) + ": ";

                endOfRoundOrGameButton.setText("Next");
                endOfRoundOrGameButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        MainActivity.allGameInstances.get(gameIndexNumber).startGame(v.getContext());
                    }
                });
            }

            TextView roundTV = createTVwithText(roundNo);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, dpTOpx(50), 0, 0);
            roundTV.setLayoutParams(params);
            playersLL.addView(roundTV);
            Log.d("Hello", "xxxx 4.0 " + j);

            // create and add a TextView displaying the title 'Best Word: '
            String suggestionTitle;
            if(rounds == 1){
                suggestionTitle = "Possible words: ";
            }else{
                suggestionTitle = "Best word: ";
            }
            TextView bestWordTitleTV = createTVwithText(suggestionTitle);
            bestWordTitleTV.setLayoutParams(lp);
            playersLL.addView(bestWordTitleTV);
            Log.d("Hello", "xxxx 4.1 " + j);

            // create a reference to the object instance of the GameInstance class created in the main
            // activity so that you can get the letters and corresponding best words for each round
            GameInstance thisGameInstance = MainActivity.allGameInstances.get(gameIndexNumber);


            // get the letters
            // create and add a TextView displaying the letters for the round
            String letters = getRoundOrGameLetters(roundOrGameEnd, j);
            TextView lettersTV = createTVwithText(letters);
            lettersTV.setLayoutParams(params);
            wordsLL.addView(lettersTV);
            Log.d("Hello", "xxxx 4.2 " + j);


            String wordSuggestion;
            // If 'Round End' screen, Print list of suggested words for various lengths
            // If 'Game End' screen, Print just one longest word
            if (rounds == 1) {
                ArrayList<String> suggestedWords = new ArrayList<>();
                GameInstance gi = thisGameInstance;
                int curRound = gi.getRound();
                suggestedWords = gi.getSuggestedWordsOfRound(curRound);
                for (int i=0; i<suggestedWords.size(); ++i){ // String w : suggestedWords){
                    String w = suggestedWords.get(i);
                    StringBuilder suggestedToPlayer = new StringBuilder();
                    suggestedToPlayer.append(w.toUpperCase());
                    suggestedToPlayer.append(" (" + w.length() + ")");
                    TextView bestPossibleWordTV = createTVwithText(suggestedToPlayer.toString());
                    bestPossibleWordTV.setLayoutParams(lp);
                    wordsLL.addView(bestPossibleWordTV);
                    if (i>0) {
                        TextView spaceTV = createTVwithText("");
                        spaceTV.setLayoutParams(lp);
                        playersLL.addView(spaceTV);
                    }
                }
            }else{
                wordSuggestion = getRoundOrGameBestPossibleWord(roundOrGameEnd,j);
                TextView bestPossibleWordTV = createTVwithText(wordSuggestion);
                bestPossibleWordTV.setLayoutParams(lp);
                wordsLL.addView(bestPossibleWordTV);
            }

            Log.d("Hello", "xxxx 4.3 " + j);


            // for the end of a round, only do this step for the one player currently playing,
            // for the end of the game do this step for each player, doing the following:
            // adding a new TextView for the players' names
            // Opposite add a new TextView for the players' best words
            for (int i = 0 ; i<players; i++){
                Log.d("Hello", "xxxx 5.0 " + i);

                String name = "";

                if(roundOrGameEnd.equals("game")) {
                    name = getPlayerNameorID(i);
                } else if(roundOrGameEnd.equals("round")) {
                    name = getPlayerNameorID(gameIndexNumber);
                }

                TextView nameTV = createTVwithText(name + ": ");
                nameTV.setLayoutParams(lp);
                playersLL.addView(nameTV);

                //get their best guess
                // create a reference to the object instance of the GameInstance class created in the main activity
//                GameInstance myGameInstance = MainActivity.allGameInstances.get(i);
                String bestGuess = getRoundOrGameBestGuess(roundOrGameEnd,i,j);
                TextView bestGuessTV = createTVwithText(bestGuess);
                bestGuessTV.setLayoutParams(lp);
                wordsLL.addView(bestGuessTV);
            }

        }

    }

    private String getPlayerNameorID(int playerNum) {

        GameData myGameData;

        // declare and initialise a String to hold the player's ID retrieved from the
        // allGameInstance class
        String playerID = MainActivity.allGameInstances.get(playerNum).getPlayerID();

        // if the playerID is blank then the new player hasn't set a user ID and their name can
        // just be retrieved using the GameData constructor that takes an int as an input
//        if (playerID.equals("")){
//            myGameData = new GameData(this, playerNum);

//        } else {
            // if the playerID is not blank then the new player has set a user ID which
            // must be retrieved using the GameData constructor that takes the playerID
            myGameData = new GameData(this, playerID);
//        }

        // return the player's name or ID retrieved from the GameData class
        return myGameData.getUsername();
    }

    private String getRoundOrGameBestGuess(String roundOrGameEnd, int i, int j) {

        String bestGuess = "";
        if (roundOrGameEnd.equals("round")){
            Log.d("getRoundOrGameBestGuess", "getting the best guess for a round: " + roundOrGameEnd);
            int round = MainActivity.allGameInstances.get(gameIndexNumber).getRound();
            GameInstance currentPlayer = MainActivity.allGameInstances.get(gameIndexNumber);
            String Guess = currentPlayer.getRoundXWord(round+1);
            bestGuess = Guess + " (" + String.valueOf(Guess.length()) + ")";

//            bestGuess = myGameInstance.getRoundXWord(MainActivity.allGameInstances.get(j).getRound()+1) + " (" + String.valueOf(myGameInstance.getRoundXWord(MainActivity.allGameInstances.get(j).getRound()+1).length()) + ")";
        }else if(roundOrGameEnd.equals("game")){
            Log.d("getRoundOrGameBestGuess", "getting the best Guess for a game: " + roundOrGameEnd);
//            String Guess = player1GameInstance.getRoundLongestPossible(j);
//            bestPossibleWord = bestWordPoss + " (" + String.valueOf(bestWordPoss.length()) + ")";

            GameInstance myGameInstance = MainActivity.allGameInstances.get(i);
            bestGuess = myGameInstance.getRoundXWord(j+1) + " (" + String.valueOf(myGameInstance.getRoundXWord(j+1)).length() + ")";
        }
        return bestGuess;

    }

    private String getRoundOrGameBestPossibleWord(String roundOrGameEnd, int j) {
        String bestPossibleWord = "";
        GameInstance player1GameInstance = MainActivity.allGameInstances.get(0);

        if (roundOrGameEnd.equals("round")){
            Log.d("getBestPossibleWord", "getting the best word for a round: " + roundOrGameEnd);
            int round = MainActivity.allGameInstances.get(gameIndexNumber).getRound();
            GameInstance currentPlayer = MainActivity.allGameInstances.get(gameIndexNumber);
            String bestWordPoss = currentPlayer.getRoundLongestPossible(round);
            bestPossibleWord = bestWordPoss + " (" + String.valueOf(bestWordPoss.length()) + ")";
        }else if(roundOrGameEnd.equals("game")){
            Log.d("getBestPossibleWord", "getting the best word for a game: " + roundOrGameEnd);
            String bestWordPoss = player1GameInstance.getRoundLongestPossible(j);
            bestPossibleWord = bestWordPoss + " (" + String.valueOf(bestWordPoss.length()) + ")";
        }
        return bestPossibleWord;
    }

    private String getRoundOrGameLetters(String roundOrGameEnd, int j) {
        String letters = "";

        //the letters available for each player are the same so you can get the letters for just the first player
        GameInstance player1GameInstance = MainActivity.allGameInstances.get(0);

        if (roundOrGameEnd.equals("round")){

            int round = MainActivity.allGameInstances.get(gameIndexNumber).getRound();
            letters = MainActivity.allGameInstances.get(gameIndexNumber).getLetters(round);

//            letters = player1GameInstance.getLetters(MainActivity.allGameInstances.get(gameIndexNumber).getRound());
        }else if(roundOrGameEnd.equals("game")){
            letters = player1GameInstance.getLetters(j);
        }

        return letters;
    }

    private void populateHeaderMsg(String roundOrGameEnd) {
        Log.d(TAG, "populateHeaderMsg: end of a round or game - " + roundOrGameEnd);
        // get the textview that will display the header message on the end screen, either
        // GAME OVER for the end of a game or TIME UP for the end of a round
        TextView gameOverTV = (TextView) findViewById(R.id.gameOverEndScreenTV);

        if (roundOrGameEnd.equals("round")){
            //do what you need for the end of a round
            gameOverTV.setText("TIME UP!");
            Log.d(TAG, "populateHeaderMsg: roundOrGameEnd should be round: " + roundOrGameEnd);
        }else if (roundOrGameEnd.equals("game")) {
            //do what you need for the end of the game
            Log.d(TAG, "populateHeaderMsg: roundOrGameEnd should be GAME: " + roundOrGameEnd);

            // if there's more than one player set the text of the gameOverTV on the end screen to say 'GAME OVER BITCHEZ'
            if (numPlayers > 1) {
                Log.d(TAG, "numPlayers > 1" + numPlayers);
                gameOverTV.setText("GAME OVER!");
            } else {
                //if there's only one player set the text of the gameOverTV on the end screen to say 'GAME OVER'
                //followed by their name
                // retrieve the player's name from the GameData class, store in a string to display in the heading
                Log.d(TAG, "numPlayers not > 1 " + numPlayers);
                GameData myGameData = new GameData(this, MainActivity.allGameInstances.get(0).getPlayerID());
                String playername = myGameData.getUsername();
                gameOverTV.setText("GAME OVER " + playername.toUpperCase());
            }
            Log.d(TAG, "xxxx 1");
        }
    }

    private void populateResultLL(String roundOrGameEnd) {

        LinearLayout resultsLL = (LinearLayout) findViewById(R.id.resultEndScreenLL);

        int steps = 0;

        // if this method is being called for the end of a game, you'll need to do the
        // below for loop for each player. If it's just the end of a round, you'll only need to do
        // the for loop once, for the player that just completed the round
        if(roundOrGameEnd.equals("game")){
            Log.d(TAG, "roundOrGameEnd should be game, is: " + roundOrGameEnd);
            steps = numPlayers;

        }else if (roundOrGameEnd.equals("round")){
            Log.d(TAG, "roundOrGameEnd should be round, is: " + roundOrGameEnd);
            steps = 1;
        }


        //for the number of players add a line stating the score each player achieved to the
        // horizontal linear layout called resultsLL
        for (int k=0; k<steps; k++){

            //create empty strings to be filled with the individual components of the result
            String result = "";
            String playername = "";
            String greeting = "";
            int totalScore=0;
            int maxScore=0;

            // create a reference to the object instance of the GameInstance class created in the main activity
            GameInstance myGameInstance = MainActivity.allGameInstances.get(gameIndexNumber);


            // set the greeting to be "You scored" for the end of a round in any game type,
            // set the greeting to say "You scored" for end of round and game in one player,
            // set the greeting to be "#name scored" for the end of a game in multiplayer
            if (roundOrGameEnd.equals("round") || (numPlayers == 1)){
                greeting = "You scored ";
            }else if (roundOrGameEnd.equals("game") && (numPlayers > 1)){
                //get the player name
                playername = getPlayerNameorID(k);
                greeting = playername + " scored ";
            }



            if (roundOrGameEnd.equals("round")){
                // if it's the end of a round get the players score and the max possible score from that round
                    // the player's total score at the end of the round
                    totalScore = myGameInstance.getRoundXWord(myGameInstance.getRound()+1).length();
                    // the max possible score at the end of the round
                    maxScore = myGameInstance.getRoundLongestPossible(myGameInstance.getRound()).length();

            }else if (roundOrGameEnd.equals("game")){
                // if it's the end of a game get the players score and the max possible score from that game
                    GameInstance eachGameInstance = MainActivity.allGameInstances.get(k);

                    // the player's total score at the end of the game
                    totalScore = eachGameInstance.getTotalScore();
                    // the max possible score at the end of the game
                    maxScore = eachGameInstance.getHighestPossibleScore();
            }
            Log.d(TAG, "xxxx 2 iteration no. " + k);




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

//    private ArrayList<String> playersWithHighestScore(playersFinalScoresNNames[] playersFinalScoresNNamesArr) {
    private ArrayList<GameInstance> playersWithHighestScore() {
        int maxScore = 0;
        // declare an empty ArrayList of Strings to hold the names of the player(s) with the high score
        ArrayList<GameInstance> winners = new ArrayList<>();

        // go through each element of the playersFinalScoresNNamesArr checking each score to find
        // the max score, then add the associated PlayerName to the ArrayList of winners names
        for (GameInstance v1 : MainActivity.allGameInstances) {
            int score = v1.getTotalScore();
            if (score > maxScore) {
                maxScore = score;
                winners.clear();
                winners.add(v1);
            } else if (score == maxScore) {
                winners.add(v1);
            }
        }

        return winners;

    }

    public int dpTOpx (int dp){
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        return (int)((dp * displayMetrics.density) + 0.5);
    }

    public TextView createTVwithText(String text4TV){
        TextView newTV = new TextView(this);
        newTV.setText(text4TV);
        return newTV;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (this.backButtonPressedOnce) {
                Intent homeScreenIntent = new Intent(this, MainActivity.class);
                startActivity(homeScreenIntent);
                return;
            }
            Toast.makeText(this, "Double tap BACK to exit!", Toast.LENGTH_SHORT).show();
            this.backButtonPressedOnce = true;

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    backButtonPressedOnce = false;
                }
            }, 1500);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_profile:
                // User chose the "Profile" item, jump to the profile page
                Log.d(MONITOR_TAG, "Chose des's profile icon, END");
                Intent profileScreenIntent = new Intent(RoundnGameResults.this, ProfileActivity.class);
                startActivity(profileScreenIntent);
                return true;

            // Use this for other action bar items as necessary
//            case R.id.action_favorite:
//                // User chose the "Favorite" action, mark the current item
//                // as a favorite...
//                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        Log.d(MONITOR_TAG, "Before_onNavigationItemSelected__MainActivity");
        navBurger.navigateTo(item, RoundnGameResults.this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
