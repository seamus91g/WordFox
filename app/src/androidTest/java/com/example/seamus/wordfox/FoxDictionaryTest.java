package com.example.seamus.wordfox;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import com.example.seamus.wordfox.data.FoxDictionary;

import static org.junit.Assert.*;

import org.junit.Test;

import java.util.ArrayList;

/**
 * Created by Gilroy on 4/11/2018.
 */
public class FoxDictionaryTest {
    Context context = InstrumentationRegistry.getTargetContext();
    FoxDictionary dictionary = new FoxDictionary("validWords_alph.txt", "letterFrequency.txt", context.getAssets());

    @Test
    public void wordExists() {
        assertEquals(true, dictionary.checkWordExists("chair"));
        assertEquals(true, dictionary.checkWordExists("conundrum"));
        assertEquals(false, dictionary.checkWordExists("a"));
        assertEquals(false, dictionary.checkWordExists("go"));
        assertEquals(false, dictionary.checkWordExists("abcdefgh"));
    }

    @Test
    public void longestLettersTest() {
        ArrayList<String> longestWords = dictionary.longestWordFromLetters("murdnunoc", 5);
        String[] expectedWords = {"conundrum", "corundum", "nondum", "round", "mourn", "dunno", "mound"};
        assertEquals(expectedWords[0], longestWords.get(0));
        assertArrayEquals(expectedWords, longestWords.toArray());
    }

    // Check 3 vowels, 6 consonants
    @Test
    public void getLettersTest() {
        // Run it a bunch of times, since randomness is involved
        for (int i = 0; i < 10; ++i) {
            ArrayList<String> letters = dictionary.getGivenLetters();
            String alphabet = "AEIOUBCDFGHJKLMNPQRSTVWXYZ";
            int vowelCount = 0, consonantCount = 0;
            for (String letter : letters) {
                int index = alphabet.indexOf(letter);
                if (index > 4) {
                    ++consonantCount;
                } else if (index >= 0) {       // If letter not found, -1 is returned
                    ++vowelCount;
                }
            }
            assertEquals(9, letters.size());
            assertTrue((vowelCount >= 2 && vowelCount <= 4));
            assertTrue((consonantCount >= 5 && consonantCount <= 7));
            assertEquals(vowelCount + consonantCount, letters.size());
        }
    }
}