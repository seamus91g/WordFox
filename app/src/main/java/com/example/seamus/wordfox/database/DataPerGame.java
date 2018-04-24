package com.example.seamus.wordfox.database;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Gilroy on 4/3/2018.
 */

public class DataPerGame {
    public String winner;
    public int scoreWinner;
    public List<String> letters;
    public List<String> bestPossible;
    public Set<String> players;
    public Map<String, ArrayList<String>> wordsPerPlayer;
}
