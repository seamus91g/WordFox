package com.example.seamus.wordfox.data.local;

import com.example.seamus.wordfox.data.Diction;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Gilroy
 */

public class InMemoryDictionary implements Diction {
    private static final ArrayList<String> allValidWords = new ArrayList<String>();
    public InMemoryDictionary(){
        allValidWords.add("CONUNDRUM");
        allValidWords.add("ROUND");
        allValidWords.add("CON");
        allValidWords.add("RUM");
    }
    @Override
    public boolean checkWordExists(String checkWord) {
        return (allValidWords.contains(checkWord.toUpperCase()));
    }

    @Override
    public ArrayList<String> longestWordFromLetters(String givenLetters) {
        return allValidWords;
    }

    @Override
    public ArrayList<String> getGivenLetters() {
        String[] letters = "CONUNDRUM".split("");
        return new ArrayList<String>(Arrays.asList(letters).subList(1, letters.length));
    }
}
