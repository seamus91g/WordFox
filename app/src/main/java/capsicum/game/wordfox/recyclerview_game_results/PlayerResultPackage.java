package capsicum.game.wordfox.recyclerview_game_results;

import android.graphics.Bitmap;

import java.util.ArrayList;

public class PlayerResultPackage {
    public final ArrayList<String> wordsFound;
    public final String name;
    public final Bitmap profilePic;
    public final Bitmap rankPic;
    public final String rank;
    public final int score;

    public PlayerResultPackage(ArrayList<String> wordsFound, String name, int score, Bitmap profilePic, Bitmap rankPic, String rank) {
        this.wordsFound = wordsFound;
        this.name = name;
        this.score = score;
        this.profilePic = profilePic;
        this.rankPic = rankPic;
        this.rank = rank;
    }
}
