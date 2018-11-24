package capsicum.game.wordfox.recyclerview_game_results;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class PlayerDetailsItemDecoration extends RecyclerView.ItemDecoration {

    private int space;

    public PlayerDetailsItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {
        outRect.left = space;
        outRect.right = space;

        // Add top margin only for the first item to avoid double space between items
//        if (parent.getChildLayoutPosition(view) == 0) {
//            outRect.top = space;
//        } else {
//            outRect.top = 0;
//        }
    }

}
