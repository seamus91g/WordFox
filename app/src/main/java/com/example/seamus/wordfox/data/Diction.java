package com.example.seamus.wordfox.data;

import java.util.ArrayList;

/**
 * Created by Gilroy on 4/13/2018.
 */

public interface Diction {
    boolean checkWordExists(String checkWord);
    ArrayList<String> longestWordFromLetters(String givenLetters);
    ArrayList<String> getGivenLetters();
}
