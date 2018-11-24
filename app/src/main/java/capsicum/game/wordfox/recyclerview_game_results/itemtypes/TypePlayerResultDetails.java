package capsicum.game.wordfox.recyclerview_game_results.itemtypes;

import android.graphics.Bitmap;

import capsicum.game.wordfox.recyclerview_game_results.PlayerResultListItem;
import capsicum.game.wordfox.recyclerview_game_results.PlayerResultPackage;

public class TypePlayerResultDetails implements PlayerResultListItem {
    public final String name;
    public final Bitmap profilePic;
    public final Bitmap rankPic;
    public final String rank;
    public final int score;

    public TypePlayerResultDetails(PlayerResultPackage player) {
        this.name = player.name;
        this.profilePic = player.profilePic;
        this.rankPic = player.rankPic;
        this.rank = player.rank;
        this.score = player.score;
    }

    @Override
    public int getListItemType() {
        return PlayerResultListItem.PLAYER;
    }
}
