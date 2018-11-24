package capsicum.game.wordfox;

import capsicum.game.wordfox.screen_profile.FoxRank;

import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class PlayerIdentity {
    protected static final String PLAYER_ID = "player_id";
    protected static final String USERNAME = "username_key";
    protected static final String FOX_RANK_KEY = "fox_rank_key";
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

    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj == null){
            return false;
        }
        if (!PlayerIdentity.class.isAssignableFrom(obj.getClass())) {
            return false;
        }
        PlayerIdentity playerIdentity = (PlayerIdentity) obj;
        if (playerIdentity.ID.equals(this.ID)) {
            return true;
        }
        return false;
    }
}
