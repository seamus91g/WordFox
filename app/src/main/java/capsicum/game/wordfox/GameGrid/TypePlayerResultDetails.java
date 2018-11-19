package capsicum.game.wordfox.GameGrid;

import android.graphics.Bitmap;

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
