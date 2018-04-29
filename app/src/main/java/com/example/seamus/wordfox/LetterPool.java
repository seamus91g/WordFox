package com.example.seamus.wordfox;

import java.util.ArrayList;
import java.util.HashMap;

public class LetterPool {
    private final HashMap<String, Integer> poolDistribution; // = new HashMap<String, Integer>();
    private String allVowels = "AEIOU";
    private String allConsonants = "BCDFGHJKLMNPQRSTVWXYZ";
    private String all = "AEIOUBCDFGHJKLMNPQRSTVWXYZ";
    ArrayList<String> alreadyPicked = new ArrayList<String>();
    ArrayList<String> allVowelCards;
    ArrayList<String> allConsonantCards;
    ArrayList<String> allCards;

    LetterPool(HashMap<String, Integer> poolDistribution) {
        this.poolDistribution = poolDistribution;
        allVowelCards = createCards(allVowels);
        allConsonantCards = createCards(allConsonants);
        allCards = createCards(all);
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
    public String getLetter() {
        if (allCards.size() == 0){
            allCards = createCards(all);
        }
        return returnAndDeleteRandom(allCards);
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
