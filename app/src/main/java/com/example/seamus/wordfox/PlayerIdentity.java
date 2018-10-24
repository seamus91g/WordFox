package com.example.seamus.wordfox;

import com.example.seamus.wordfox.profile.FoxRank;

import java.util.UUID;

public class PlayerIdentity {
    public final UUID ID;
    public final String username;
    public final FoxRank rank;

    public PlayerIdentity(UUID ID, String username) {
        this.ID = ID;
        this.username = username;
        rank = GameData.determineRankValue(0);
    }
    public PlayerIdentity(UUID ID, String username, FoxRank rank) {
        this.ID = ID;
        this.username = username;
        this.rank = rank;
    }
}
