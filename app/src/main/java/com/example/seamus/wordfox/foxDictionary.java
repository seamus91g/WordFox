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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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

public class foxDictionary {

    private static final ArrayList<String> allValidWords = new ArrayList<String>();
    private final HashMap<String, Integer> letterDistributionMap = new HashMap<String, Integer>();
    LetterPool letterPool;
    private final String MONITOR_TAG_FOX = "myTag";

    foxDictionary(String validWordsFileName, String letterDistributionFile, Activity myGameActivity) {
        AssetManager assetManager = myGameActivity.getAssets();
        Log.d(MONITOR_TAG_FOX, "foxDic: 1");
        if (allValidWords.isEmpty()) {
            try {
                InputStream myIpStr = assetManager.open(validWordsFileName);
                this.populateWords(myIpStr);
                myIpStr.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        try {
            InputStream myIpStr = assetManager.open(letterDistributionFile);
            this.populateLetterDistribution(myIpStr);
            myIpStr.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        letterPool = new LetterPool(letterDistributionMap);
    }

    private void populateWords(InputStream myIpStr) throws IOException {
        Reader reader = new InputStreamReader(myIpStr);
        BufferedReader buffreader = new BufferedReader(reader);
        String readString = buffreader.readLine();
        while (readString != null) {
            String thisWord = readString.toLowerCase();
            allValidWords.add(thisWord);
            readString = buffreader.readLine();
        }
    }

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

    public boolean checkWordExists(String checkWord) {
        if (allValidWords.contains(checkWord)) {
            Log.d("foxWords", "Found word: " + checkWord);
            return true;
        } else {
            Log.d("foxWords", "Did not find word: " + checkWord);
            return false;
        }
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
