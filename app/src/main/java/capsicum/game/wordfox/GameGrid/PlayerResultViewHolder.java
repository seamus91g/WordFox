package capsicum.game.wordfox.GameGrid;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public abstract class PlayerResultViewHolder extends RecyclerView.ViewHolder {
    public PlayerResultViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public abstract void onBind(PlayerResultListItem player);
}
