package com.example.seamus.wordfox;

import android.os.Bundle;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class EndScreenSinglePlayer1 extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

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

        int numPlayers = MainActivity.allGameInstances.size();

        // get the textview that will display the GAME OVER message on the end screen
        TextView gameOverTV = (TextView) findViewById(R.id.gameOverEndScreenTV);

        // if there's more than one player set the text of the gameOverTV on the end screen to say 'GAME OVER BITCHEZ'
        if (numPlayers>1) {
            gameOverTV.setText("GAME OVER BITCHEZ");
        }else {
            //if there's only one player set the text of the gameOverTV on the end screen to say 'GAME OVER'
            //followed by their name
            // retrieve the player's name from the GameData class, store in a string to display in the heading
            GameData myGameData = new GameData(this, 0);
            String playername = myGameData.getUsername();
            gameOverTV.setText("GAME OVER " + playername.toUpperCase());
        }
        Log.d("Hello", "xxxx 1");

        //create a hashmap to be filled with the players scores and names
        HashMap<Double, String> playersScoresNNamesHM = new HashMap<>(numPlayers);

        //for the number of players add a line stating the score each achieved
        LinearLayout resultsLL = (LinearLayout) findViewById(R.id.resultEndScreenLL);
        for (int k=0; k<numPlayers; k++){

            //create an empty string to be filled with the components of the result
            String result = "";
            String playername = "";
            String greeting = "";

            if (numPlayers == 1){
                // if there's only one player, the greeting should say, 'You scored '
                greeting = "You scored ";
            }else {
                // if there's more than one player, the greeting should say, '#name scored'

                //get the player name
                GameData myGameData = new GameData(this, k);
                playername = myGameData.getUsername();
                greeting = playername + " scored ";
            }
            Log.d("Hello", "xxxx 2 " + k);

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
            //add each players percentage (score) into the array list of their scores
            playersScoresNNamesHM.put(successPercent, playername);

            //assemble the result to contain all the components and show in the TextView
            result = greeting + resultsRatio + resultsPercent;
            TextView resultTV = createTVwithText(result);
            resultsLL.addView(resultTV);
        }
        Log.d("Hello", "xxxx 3");

        //get the name of the player with the highest score and set it to be the winner or if
        // there's a draw, say who drew
        if (numPlayers>1){
            //create an array of strings to hold the name(s) of the player(s) with the highest score
            ArrayList<String> winner = valueOfMaxKeyHM(playersScoresNNamesHM);

            String victoryMessage = "";
            Log.d("Hello", "xxxx 3.1");

            if (winner.size() == 1){
                //if there's only one name in the list of winners then it wasn't a draw and that player won
                Log.d("Hello", "xxxx 3.2");
                victoryMessage = "Winner is " + winner.get(0) + "!";
            }else {
                //if there's more than one name in the list of winners then it was a draw
                victoryMessage = "It was a draw between ";

                for (int f = 0; f<winner.size(); f++){
                    Log.d("Hello", "xxxx 3.3");
                    if (f == (winner.size() - 1)){
                        //if you're entering the name of the last player to tie the score,
                        // end with an !
                        victoryMessage = victoryMessage + winner.get(f) + "!";
                        Log.d("Hello", "xxxx 3.4");
                    }else{
                        victoryMessage = victoryMessage + winner.get(f) + " and ";
                        Log.d("Hello", "xxxx 3.5");
                    }
                }

            }

            TextView winnerTV = createTVwithText(victoryMessage);
            Log.d("Hello", "xxxx 3.6");
            winnerTV.setGravity(Gravity.CENTER);

            //add the textview with the winner's name into the LinearLayout containing the Game Over message
            LinearLayout gameOverWinnerLL = (LinearLayout) findViewById(R.id.gameOverWinnerLLEndScreen);
            gameOverWinnerLL.addView(winnerTV);
        }

        LinearLayout playersLL = (LinearLayout) findViewById(R.id.playersEndScreenLL);
        LinearLayout wordsLL = (LinearLayout) findViewById(R.id.wordsEndScreenLL);

        //loop through the numbers of rounds, doing the following:
        //add margin at the top of the round summary
        // add the word 'Round' followed by the correct round number
        // add the words 'Best Word'
        //Opposite do the following
        //add the round letters
        //add the best possible word
        for (int j=0; j<3; j++){
            // create and add a TextView displaying the current round
            TextView roundTV = createTVwithText("Round " + String.valueOf(j+1) + ": ");


            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, dpTOpx(50), 0, 0);
            roundTV.setLayoutParams(params);


            playersLL.addView(roundTV);
            Log.d("Hello", "xxxx 4 " + j);

            // create and add a TextView displaying the title 'Best Word: '
            TextView bestWordTitleTV = createTVwithText("Best Word: ");
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
            wordsLL.addView(bestPossibleWordTV);
            Log.d("Hello", "xxxx 4.3" + j);


            //loop through the numbers of players, doing the following:
            // adding a new TextView for the players' names
            //Opposite add a new TextView for the players' best words
            for (int i = 0 ; i<numPlayers; i++){
                Log.d("Hello", "xxxx 5 " + i);

                // create an object instance of the GameData class to get the stored username(s)
                GameData myGameData = new GameData(this, i);
                // declare and initialise a String to hold the player's name retrieved from the GameData class
                String name = myGameData.getUsername();

                TextView nameTV = createTVwithText(name + ": ");
                playersLL.addView(nameTV);

                // create a reference to the object instance of the GameInstance class created in the main activity
                GameInstance myGameInstance = MainActivity.allGameInstances.get(i);
                TextView bestGuessTV = createTVwithText(myGameInstance.getRoundXWord(j+1) + " (" + String.valueOf(myGameInstance.getRoundXWord(j+1).length()) +")");
                wordsLL.addView(bestGuessTV);
            }

        }

    }

        public static ArrayList<String> valueOfMaxKeyHM(HashMap myHM) {
            Iterator it = myHM.entrySet().iterator();
            ArrayList<String> winner = new ArrayList<>();
            int max = 0;
            while (it.hasNext()) {

                HashMap.Entry pair = (HashMap.Entry)it.next();

                Double d = (Double) pair.getKey();
                int num = d.intValue();
                if (num>max){
                    max = num;
                    winner.clear();
                    winner.add((String) pair.getValue());
                }else if(num == max){
                    winner.add((String) pair.getValue());
                }
                it.remove(); // avoids a ConcurrentModificationException

            }
            return winner;
        }

    public int dpTOpx (int dp){
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
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
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.end_screen_single_player1, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
