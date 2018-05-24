package com.example.seamus.wordfox.RV;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.seamus.wordfox.R;
import com.example.seamus.wordfox.player_switch.PlayerSwitchContract;

//    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
public abstract class BaseWFViewHolder extends RecyclerView.ViewHolder {
//    public RelativeLayout relLayout;
//    public TextView mTextView;
    private boolean isExpanded;

    public BaseWFViewHolder(View v) {
        super(v);
        isExpanded = true;
//        relLayout = v;
//        mTextView = v.findViewById(R.id.player_word);
//            v.setOnClickListener(this);
    }

    public boolean isListExpanded(){
        return isExpanded;
    }
    public void toggleExpanded(){
        isExpanded = !isExpanded;
    }

    public abstract void bindType(DataListItem item);
//        @Override
//        public void onClick(View v) {
//            listener.onRecyclerItemSelected(getAdapterPosition(), v);
//        }
}
