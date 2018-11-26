package capsicum.game.wordfox.screen_rank_detail;

import android.graphics.Bitmap;

public class RankDetailItem {
    public final String rank;
    public final Bitmap foxBmp;
    public final int scoreRequired;
    public final int timesFound;
    public boolean isLocked;

    RankDetailItem(String rank, Bitmap foxBmp, int scoreRequired, int timesFound) {
        this(rank, foxBmp, scoreRequired, timesFound, false);
    }

    RankDetailItem(String rank, Bitmap foxBmp, int scoreRequired, int timesFound, boolean isLocked) {
        this.rank = rank;
        this.foxBmp = foxBmp;
        this.scoreRequired = scoreRequired;
        this.timesFound = timesFound;
        this.isLocked = isLocked;
    }

}
