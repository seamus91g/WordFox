package com.example.seamus.wordfox.dataWordsRecycler;

/**
 * Created by Gilroy
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
