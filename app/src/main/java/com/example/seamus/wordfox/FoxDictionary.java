package com.example.seamus.wordfox;


import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;


import android.content.res.AssetManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import static java.util.Collections.shuffle;

/**
 * Created by Seamus on 05/07/2017.
 */

public class FoxDictionary {

    private static final ArrayList<String> allValidWords = new ArrayList<String>();
    private static final HashMap<String, String> validWordsAlphabeticalKey = new HashMap<String, String>();
    private static final HashMap<String, Integer> letterDistributionMap = new HashMap<String, Integer>();
    private LetterPool letterPool;
    private Collator col;
    private final String MONITOR_TAG_FOX = "myTag";

    FoxDictionary(String validWordsFileName, String letterDistributionFile, Activity myGameActivity) {
        col = Collator.getInstance(new Locale("en", "EN"));
        AssetManager assetManager = myGameActivity.getAssets();
//        Log.d(MONITOR_TAG_FOX, "foxDic: 1");
        if (allValidWords.isEmpty()) {
//            Log.d(MONITOR_TAG_FOX, "foxDic: 2");
            try {
                InputStream myIpStr = assetManager.open(validWordsFileName);
                this.populateWords(myIpStr);
                myIpStr.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        if (letterDistributionMap.isEmpty()){
            try {
                InputStream myIpStr = assetManager.open(letterDistributionFile);
                this.populateLetterDistribution(myIpStr);
                myIpStr.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        letterPool = new LetterPool(letterDistributionMap);
    }
    // Read in text file of list of words.
    // Also store in a Hashmap where each key is a word with its letters sorted alphabetically.
    // The Hashmap value for each key is the word itself. This is used to search for longest word
    private void populateWords(InputStream myIpStr) throws IOException {
        Reader reader = new InputStreamReader(myIpStr);
        BufferedReader buffreader = new BufferedReader(reader);
        String readString = buffreader.readLine();
//        Log.d(MONITOR_TAG_FOX, "Reading dictionary again!!: 1");
        while (readString != null) {
            String thisWord = readString.toLowerCase();
            String[] wordArray = thisWord.split(" ");
            validWordsAlphabeticalKey.put(wordArray[1], wordArray[0]);
            allValidWords.add(wordArray[0]);
            readString = buffreader.readLine();
        }
    }
    // Store how many of each letter to use. Example, 8 letter Ts, 3 letter Bs, 1 letter Z
    private void populateLetterDistribution(InputStream myIpStr) throws IOException {
        Reader reader = new InputStreamReader(myIpStr);
        BufferedReader buffreader = new BufferedReader(reader);
        String readString = buffreader.readLine();
        while (readString != null) {
            String[] letterNumber = readString.split(" ");
            letterDistributionMap.put(letterNumber[0], Integer.parseInt(letterNumber[1]));
//            Log.d("myTag", "Letter: " + letterNumber[0] + ", " + letterNumber[1]);
            readString = buffreader.readLine();
        }
    }

    private String makeStringAlphabetical(String notAlphabetical) {
        String[] s1 = notAlphabetical.split("");
        Arrays.sort(s1, col);
        String wordAlphSorted = "";
        for (int i = 0; i < s1.length; i++) {
            wordAlphSorted += s1[i];
        }
        return wordAlphSorted;
    }
    public boolean checkWordExists(String checkWord) {
        if (allValidWords.contains(checkWord)) {
            Log.d("foxWords", "Found word: " + checkWord);
            return true;
        } else {
            Log.d("foxWords", "Did not find word: " + checkWord);
            return false;
        }
    }
    // Find longest word from the random 9 game letters.
    public String longestWordFromLetters(String givenLetters) {
        givenLetters = makeStringAlphabetical(givenLetters.toLowerCase());
        String thisLongest = "";
        Log.d(MONITOR_TAG_FOX, " Finding longest word for: " + givenLetters);

        if (validWordsAlphabeticalKey.containsKey(givenLetters)) {
            thisLongest = validWordsAlphabeticalKey.get(givenLetters);
            Log.d(MONITOR_TAG_FOX, "-----------== Found a niner!! ==--------------: " + thisLongest);
        }else {
            for (int x = 0; x < 7; x++) {        // Only go to 7 since 2 letter words are not valid
                if (thisLongest.equals("")) {
                    thisLongest = substringSearch(givenLetters, x);
                }else{
                    break;
                }
            }
        }
        return thisLongest;
    }
    // Recursively search for valid substrings
    public String substringSearch(String givenLetters, int Depth) {
        String thisLongest = "";
        int len = givenLetters.length();
        for (int j = 0; j < len; j++){
            String checkWindow = givenLetters.substring(0, j) + givenLetters.substring(j + 1, len);    // skip j
            if (Depth == 0) {
//                Log.d(MONITOR_TAG_FOX, "This window here!: " + checkWindow);
                if (validWordsAlphabeticalKey.containsKey(checkWindow)) {
                    thisLongest = validWordsAlphabeticalKey.get(checkWindow);
                    Log.d(MONITOR_TAG_FOX, "Found!!: " + thisLongest);
                    return thisLongest;
                }
            }else {
//                Log.d(MONITOR_TAG_FOX, "New depth!: " + checkWindow);
                thisLongest = substringSearch(checkWindow, Depth - 1);
            }
            if (!thisLongest.equals("")){
                break;
            }
        }
        return thisLongest;
    }

    // Randomly generate a sequence of 9 letters for the user
    public ArrayList<String> getGivenLetters() {
        ArrayList<String> givenLetters = new ArrayList<String>();
        String consonants = randLetter("consonants");
        String vowels = randLetter("vowels");
        String letters = consonants + vowels;
        int lettersLen = letters.length();
        for (int i = 0; i < lettersLen; i++) {
            givenLetters.add((letters.substring(i, i + 1)));
        }
        shuffle(givenLetters);
        return givenLetters;
    }
    // Generate 6 random consonants or 3 random vowels
    private String randLetter(String choice) {
        String letters = "";
        String set = "";
        int times = 0;

        if (choice.equals("consonants")) {
            times = 6;
            int letterLen = letters.length();
            for (int i = 0; i < times; i++) {
                set += letterPool.getConsonant();
            }
        } else if (choice.equals("vowels")) {
            times = 3;
            int letterLen = letters.length();
            for (int i = 0; i < times; i++) {
                set += letterPool.getVowel();
            }
        }
        return set;
    }

    private class LetterPool {
        private final HashMap<String, Integer> poolDistribution; // = new HashMap<String, Integer>();
        private String allVowels = "AEIOU";
        private String allConsonants = "BCDFGHJKLMNPQRSTVWXYZ";
        ArrayList<String> alreadyPicked = new ArrayList<String>();
        ArrayList<String> allVowelCards;
        ArrayList<String> allConsonantCards;

        LetterPool(HashMap<String, Integer> poolDistribution) {
            this.poolDistribution = poolDistribution;
            allVowelCards = createCards(allVowels);
            allConsonantCards = createCards(allConsonants);
        }

        private ArrayList<String> createCards(String letterSequence) {
            ArrayList<String> sequenceOfCards = new ArrayList<String>();
            String[] allLetters = letterSequence.split("(?<=.)");
            for (String letter : allLetters) {
                int count = poolDistribution.get(letter);
                for (int x = 0; x < count; x++) {
                    sequenceOfCards.add(letter);
                }
            }
            return sequenceOfCards;
        }

        public String getVowel() {
            return returnAndDeleteRandom(allVowelCards);
        }

        public String getConsonant() {
            return returnAndDeleteRandom(allConsonantCards);
        }

        private String returnAndDeleteRandom(ArrayList<String> letterList) {
            int random = (int) (Math.random() * letterList.size());
            String randomLetter = letterList.get(random);
            // If letter already chosen, try again. Keep if same found again on second attempt.
            if (alreadyPicked.contains(randomLetter)) {
                alreadyPicked.remove(alreadyPicked.indexOf(randomLetter));
                randomLetter = returnAndDeleteRandom(letterList);
            } else {
                letterList.remove(random);
                alreadyPicked.add(randomLetter);
            }
            return randomLetter;
        }
    }
}
