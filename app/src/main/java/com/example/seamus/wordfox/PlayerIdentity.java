package com.example.seamus.wordfox;

import java.util.UUID;

public class PlayerIdentity {
    public final UUID ID;
    public final String username;

    public PlayerIdentity(UUID ID, String username) {
        this.ID = ID;
        this.username = username;
    }
}
