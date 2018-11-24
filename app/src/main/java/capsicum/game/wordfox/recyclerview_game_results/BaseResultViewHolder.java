package capsicum.game.wordfox.recyclerview_game_results;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public abstract class BaseResultViewHolder extends RecyclerView.ViewHolder {
    public BaseResultViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public abstract void onBind(PlayerResultListItem player);
}
