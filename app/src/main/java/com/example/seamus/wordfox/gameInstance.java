package com.example.seamus.wordfox;


import android.content.Context;
import android.content.Intent;

/**
 * Created by Desmond on 05/07/2017.
 */

public class gameInstance {

    private static int totalScore;
    private static int score;


    public void gameInstance(){
        totalScore = 0;
        score = 78;
    }

    public int getTotalScore(){
        return totalScore;
    }

    public void setTotalScore(int point){
        totalScore += point;
    }

    public int getScore(){
        return score;
    }

    public void setScore(int point){
        score = point;
    }

    public static void clearScores(){
        totalScore = 0;
        score = 0;
    }

    public void clearScore(){
        score = 0;
    }


    public void startGame(Context context){
        Intent gameIntent = new Intent(context, GameActivity.class);
        context.startActivity(gameIntent);
    }



}
