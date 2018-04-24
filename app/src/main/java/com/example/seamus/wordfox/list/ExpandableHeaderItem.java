package com.example.seamus.wordfox.list;

/**
 * Created by Gilroy on 3/27/2018.
 */


import android.graphics.drawable.Animatable;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;


import android.graphics.drawable.Animatable;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.util.Log;
import android.view.View;

import com.example.seamus.wordfox.R;
import com.example.seamus.wordfox.databinding.ItemHeaderBinding;
import com.xwray.groupie.ExpandableGroup;
import com.xwray.groupie.ExpandableItem;


public class ExpandableHeaderItem extends HeaderItem implements ExpandableItem {

    private ExpandableGroup expandableGroup;

    public ExpandableHeaderItem(String titleStringResId, String subtitleResId) {
        super(titleStringResId, subtitleResId);
    }
    public ExpandableHeaderItem(String titleStringResId) {
        super(titleStringResId);
    }

    @Override public void bind(@NonNull final ItemHeaderBinding viewBinding, int position) {
        super.bind(viewBinding, position);
        Log.d("tag", "ExHea: " + position);
        // Initial icon state -- not animated.
        viewBinding.icon.setVisibility(View.VISIBLE);
        viewBinding.icon.setImageResource(expandableGroup.isExpanded() ? R.drawable.collapse : R.drawable.expand);
        viewBinding.icon.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                expandableGroup.onToggleExpanded();
                bindIcon(viewBinding);
            }
        });
    }

    private void bindIcon(ItemHeaderBinding viewBinding) {
        viewBinding.icon.setVisibility(View.VISIBLE);
        viewBinding.icon.setImageResource(expandableGroup.isExpanded() ? R.drawable.collapse_animated : R.drawable.expand_animated);
        Animatable drawable = (Animatable) viewBinding.icon.getDrawable();
        drawable.start();
    }

    @Override public void setExpandableGroup(@NonNull ExpandableGroup onToggleListener) {
        this.expandableGroup = onToggleListener;
    }

}
