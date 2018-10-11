package com.example.seamus.wordfox;

import java.util.UUID;

public interface GameDetails {
    UUID getID();

    int getHighestPossibleScore();

    int getTotalScore();

    String getName();

    String getRoundWord(int i);

    String getLetters(int i);
}
