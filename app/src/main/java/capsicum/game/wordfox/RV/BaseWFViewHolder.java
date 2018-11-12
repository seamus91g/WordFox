package capsicum.game.wordfox.RV;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

public abstract class BaseWFViewHolder extends RecyclerView.ViewHolder {
    private final ImageView playerArrowIV;

    public BaseWFViewHolder(View v) {
        super(v);
        playerArrowIV = v.findViewWithTag("drop_down_arrow");
    }
    protected void setSelected(boolean selected){
        if(playerArrowIV == null){
            return;
        }
        playerArrowIV.setSelected(selected);
    }
    public abstract void bindType(DataListItem item);
}
