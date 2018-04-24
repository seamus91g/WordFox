package com.example.seamus.wordfox.dataWordsRecycler;

/**
 * Created by Gilroy on 2/20/2018.
 */

public class WordData {
    private String letters;
    private String longestPossible;

    public WordData(String letters, String longestPossible){
        this.letters = letters;
        this.longestPossible = longestPossible;
    }
    public String getGivenLetters(){
        return letters;
    }
    public String getLongestPossibleWord(){
        return longestPossible;
    }
}
