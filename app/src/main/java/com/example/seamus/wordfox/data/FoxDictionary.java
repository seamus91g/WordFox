package com.example.seamus.wordfox.data;


import android.app.Activity;
import android.content.Context;
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
import android.widget.RelativeLayout;
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
import android.widget.Toast;

import com.example.seamus.wordfox.data.Diction;

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
 * Created by Seamus
 */

public class FoxDictionary implements Diction {

    private static final ArrayList<String> allValidWords = new ArrayList<String>();
    private static final HashMap<String, String> validWordsAlphabeticalKey = new HashMap<String, String>();     // TODO: shouldn't need to store the list twice .. ?
    private static final HashMap<String, Integer> letterDistributionMap = new HashMap<String, Integer>();
    public static boolean isWordListLoaded = false;

    private LetterPool letterPool;
    private Collator col;
    private static final String MONITOR_TAG_FOX = "myTag";

    public FoxDictionary(String validWordsFileName, String letterDistributionFile, AssetManager assetManager) {
        col = Collator.getInstance(new Locale("en", "EN"));
        loadWords(validWordsFileName, letterDistributionFile, assetManager);
        resetLetterPool();
    }

    public static void loadWords(String validWordsFileName, String letterDistributionFile, AssetManager assetManager) {
        if (allValidWords.isEmpty()) {
            try {
                InputStream myIpStr = assetManager.open(validWordsFileName);
                populateWords(myIpStr);
                myIpStr.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        if (letterDistributionMap.isEmpty()) {
            try {
                InputStream myIpStr = assetManager.open(letterDistributionFile);
                populateLetterDistribution(myIpStr);
                myIpStr.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        isWordListLoaded = true;
    }

    public void resetLetterPool() {
        letterPool = new LetterPool(letterDistributionMap);
    }

    // Read in text file of list of words.
    // Also store in a Hashmap where each key is a word with its letters sorted alphabetically.
    // The Hashmap value for each key is the word itself. This is used to search for longest word
    private static void populateWords(InputStream myIpStr) throws IOException {
        Reader reader = new InputStreamReader(myIpStr);
        BufferedReader buffreader = new BufferedReader(reader);
        String readString = buffreader.readLine();
        while (readString != null) {
            String thisWord = readString.toLowerCase();
            String[] wordArray = thisWord.split(" ");
            if (validWordsAlphabeticalKey.get(wordArray[1]) == null) {
                validWordsAlphabeticalKey.put(wordArray[1], wordArray[0]);
            }
            allValidWords.add(wordArray[0]);
            readString = buffreader.readLine();
        }
    }

    // Store how many of each letter to use. Example, 8 letter Ts, 3 letter Bs, 1 letter Z
    private static void populateLetterDistribution(InputStream myIpStr) throws IOException {
        Reader reader = new InputStreamReader(myIpStr);
        BufferedReader buffreader = new BufferedReader(reader);
        String readString = buffreader.readLine();
        while (readString != null) {
            String[] letterNumber = readString.split(" ");
            letterDistributionMap.put(letterNumber[0], Integer.parseInt(letterNumber[1]));
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
            return true;
        } else {
            return false;
        }
    }

    // Find longest word from the random 9 game letters.
    public ArrayList<String> longestWordFromLetters(String givenLetters) {
        return longestWordFromLetters(givenLetters, 7);     // Default is 7
    }

    public ArrayList<String> longestWordFromLetters(String givenLetters, int numberToSave) {
        givenLetters = makeStringAlphabetical(givenLetters.toLowerCase());

        // Save 'numberToSave' of the longest words that can be found. Also store 1 of each length down to length 5
        ArrayList<String> multipleLengths = new ArrayList<String>();
        if (validWordsAlphabeticalKey.containsKey(givenLetters)) {
            String niner = validWordsAlphabeticalKey.get(givenLetters);
            multipleLengths.add(niner);
            --numberToSave;
            Log.d(MONITOR_TAG_FOX, "-----------== Found a nine letter word! ==--------------: " + niner);
        }
        for (int x = 0; x < 7; ++x) {        // Only go to 7 since 2 letter words are not valid
            if (multipleLengths.size() == 0 || x < 4) {
                Log.d(MONITOR_TAG_FOX, "=== entering === " + x);
                HashMap<String, Boolean> substringsChecked = new HashMap<String, Boolean>();
                ArrayList<String> listOfLongest = new ArrayList<String>();
                listOfLongest = substringSearch(givenLetters, listOfLongest, x, substringsChecked, numberToSave);
                multipleLengths.addAll(listOfLongest);
                if (numberToSave != 1 && listOfLongest.size() > 0) {
                    numberToSave -= listOfLongest.size() - 1;
                    numberToSave = numberToSave < 1 ? 1 : numberToSave;
                }
            } else {
                Log.d(MONITOR_TAG_FOX, "=== BREAKING === at " + (9 - x) + " letters ---");
                break;
            }
        }
        return multipleLengths;
    }

    // Recursively search for valid substrings
    private ArrayList<String> substringSearch(String givenLetters, ArrayList<String> knownLongest, int Depth, HashMap substringsChecked, int howMany) {
        ArrayList<String> thisLongest = knownLongest;
        int len = givenLetters.length();
        for (int j = 0; j < len; j++) {
            String checkWindow = givenLetters.substring(0, j) + givenLetters.substring(j + 1, len);    // skip j
            if (substringsChecked.containsKey(checkWindow)) {

                //  Log.d(MONITOR_TAG_FOX, "Already checked " + checkWindow + ", continuing ...");
                continue;
            } else {
                substringsChecked.put(checkWindow, 1);
                //Log.d(MONITOR_TAG_FOX, "Checking " + checkWindow + "!!");
            }
            if (Depth == 0) {
                if (validWordsAlphabeticalKey.containsKey(checkWindow)) {
                    String candidateLongest = validWordsAlphabeticalKey.get(checkWindow);
//                    Log.d(MONITOR_TAG_FOX, "Found!!: " + candidateLongest + ", checking if higher index than " + thisLongest + ", end");
                    int numberCollected = thisLongest.size();
                    // TODO: This fails to return list of highest index words. First two will be sorted and the winner of this comparison will never be removed from the list, even if several subsequent higher indexes are found
                    if (numberCollected > 0) {
                        String leastIndexWord = thisLongest.get(numberCollected - 1);
                        String result = returnBetterIndex(leastIndexWord, candidateLongest);
                        if (!result.equals(leastIndexWord)) {
//                            Log.d(MONITOR_TAG_FOX, "LW1> Removing " + thisLongest.get(0) + ", Adding "+ res);
                            thisLongest.remove(numberCollected - 1);
                            thisLongest.add(result);
                            if (thisLongest.size() < howMany) {
                                thisLongest.add(leastIndexWord);
                            }
                        } else if (thisLongest.size() < howMany) {
                            thisLongest.add(candidateLongest);
                        }
                    } else {
                        thisLongest.add(candidateLongest);
//                        Log.d(MONITOR_TAG_FOX, "LW2> Adding " + candidateLongest);
                    }
//                    Log.d(MONITOR_TAG_FOX, "Index winnder is!!: " + thisLongest);
                }
            } else {
                thisLongest = substringSearch(checkWindow, thisLongest, Depth - 1, substringsChecked, howMany);
            }
        }
        return thisLongest;
    }

    //  Find which of two words has the highest frequency index
    private String returnBetterIndex(String defaultLongest, String suggestedLongest) {
        String bestIndex = "";
        int defaultIndex = allValidWords.indexOf(defaultLongest);
        int suggestedIndex = allValidWords.indexOf(suggestedLongest);
        if (defaultIndex == -1 && suggestedIndex == -1) {
            return bestIndex;
        }
        if (defaultIndex == -1) {
            return suggestedLongest;
        }
        if (suggestedIndex == -1) {
            return defaultLongest;
        }
        if (suggestedIndex < defaultIndex) {
            return suggestedLongest;
        }
        return defaultLongest;
    }

    // Randomly generate a sequence of 9 letters for the user
    public ArrayList<String> getGivenLetters() {
        int total = 9;
        int[] options = {2, 3, 3, 3, 3, 4};
        int random = (int) (Math.random() * options.length);
        int vowelCount = options[random];
        ArrayList<String> letters = new ArrayList<>();
        letters.addAll(getRandConsonants(total - vowelCount));
        letters.addAll(getRandVowels(vowelCount));
        shuffle(letters);
        return letters;
    }

    private ArrayList<String> getRandConsonants(int count) {
        ArrayList<String> consonants = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            consonants.add(letterPool.getConsonant());
        }
        return consonants;
    }

    private ArrayList<String> getRandVowels(int count) {
        ArrayList<String> vowels = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            vowels.add(letterPool.getVowel());
        }
        return vowels;
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
