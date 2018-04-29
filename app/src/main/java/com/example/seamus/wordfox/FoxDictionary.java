package com.example.seamus.wordfox;


import android.app.Activity;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;

import static java.util.Collections.shuffle;

/**
 * Created by Seamus on 05/07/2017.
 */

public class FoxDictionary {

    private static final ArrayList<String> allValidWords = new ArrayList<String>();
    private static final HashMap<String, String> validWordsAlphabeticalKey = new HashMap<String, String>();
    public static final HashMap<String, Integer> letterDistributionMap = new HashMap<String, Integer>();

    private LetterPool letterPool;
    private Collator col;
    private final String MONITOR_TAG_FOX = "myTag";

    FoxDictionary(String validWordsFileName, String letterDistributionFile, Activity myGameActivity) {
        col = Collator.getInstance(new Locale("en", "EN"));
        AssetManager assetManager = myGameActivity.getAssets();
        if (allValidWords.isEmpty()) {
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
            if (validWordsAlphabeticalKey.get(wordArray[1]) == null){
                validWordsAlphabeticalKey.put(wordArray[1], wordArray[0]);
            } // else{
//                Log.d(MONITOR_TAG_FOX, "Found anagram: " + wordArray[0] + "," + validWordsAlphabeticalKey.get(wordArray[1]));
//            }
            allValidWords.add(wordArray[0]);
            readString = buffreader.readLine();
        }
    }
    // Store how many of each letter to use. Example, 8 letter Ts, 3 letter Bs, 1 letter Z
    public static void populateLetterDistribution(InputStream myIpStr) throws IOException {
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
        givenLetters = makeStringAlphabetical(givenLetters.toLowerCase());

        // Save 3 of the longest words that can be found. Also store 1 of each length down to length 5
        int numberToSave = 3;
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
                    numberToSave -= listOfLongest.size() -1;
                    numberToSave = numberToSave < 1 ? 1 : numberToSave;
                }
            }else{
                Log.d(MONITOR_TAG_FOX, "=== BREAKING === at " + (9 - x) + " letters ---");
                break;
            }
        }
        return multipleLengths;
    }
    // Recursively search for valid substrings
    public ArrayList<String> substringSearch(String givenLetters, ArrayList<String> knownLongest, int Depth, HashMap substringsChecked, int howMany) {
        ArrayList<String> thisLongest = knownLongest;
        int len = givenLetters.length();
        for (int j = 0; j < len; j++){
            String checkWindow = givenLetters.substring(0, j) + givenLetters.substring(j + 1, len);    // skip j
            if (substringsChecked.containsKey(checkWindow)){

              //  Log.d(MONITOR_TAG_FOX, "Already checked " + checkWindow + ", continuing ...");
                continue;
            }else{
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
                        String leastIndexWord = thisLongest.get(numberCollected-1);
                        String result = returnBetterIndex(leastIndexWord, candidateLongest);
                        if(!result.equals(leastIndexWord)) {
//                            Log.d(MONITOR_TAG_FOX, "LW1> Removing " + thisLongest.get(0) + ", Adding "+ res);
                            thisLongest.remove(numberCollected-1);
                            thisLongest.add(result);
                            if(thisLongest.size() < howMany){
                                thisLongest.add(leastIndexWord);
                            }
                        }else if(thisLongest.size() < howMany){
                            thisLongest.add(candidateLongest);
                        }
                    }else{
                        thisLongest.add(candidateLongest);
//                        Log.d(MONITOR_TAG_FOX, "LW2> Adding " + candidateLongest);
                    }
//                    Log.d(MONITOR_TAG_FOX, "Index winnder is!!: " + thisLongest);
                }
            }else {
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
        if (defaultIndex == -1 && suggestedIndex == -1){
            return bestIndex;
        }
        if (defaultIndex == -1 ){
            return suggestedLongest;
        }
        if (suggestedIndex == -1 ){
            return defaultLongest;
        }
        if (suggestedIndex < defaultIndex){
            return suggestedLongest;
        }
        return defaultLongest;
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

//    private class LettersInstance {
//        private final String givenLetters;
//        LettersInstance(String givenLetters){
//            this.givenLetters = givenLetters;
//        }


//    }

}

