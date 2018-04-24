package com.example.seamus.wordfox.list;

import android.graphics.Color;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.view.View;

import com.example.seamus.wordfox.R;
import com.example.seamus.wordfox.databinding.ItemHeaderBinding;
import com.xwray.groupie.databinding.BindableItem;
/**
 * Created by Gilroy on 3/27/2018.
 */

public class HeaderItem extends BindableItem<ItemHeaderBinding> {

    private String titleStringResId;
    private String subtitleResId;
    @DrawableRes
    private int iconResId;
    private View.OnClickListener onIconClickListener;
    private int color = Color.parseColor("#FFCCFF");

    public HeaderItem( String titleStringResId) {
        this(titleStringResId, null);
    }

    public HeaderItem( String titleStringResId,  String subtitleResId) {
        this(titleStringResId, subtitleResId, 0, null);
    }

    public HeaderItem( String titleStringResId, String subtitleResId, @DrawableRes int iconResId, View.OnClickListener onIconClickListener) {
        this.titleStringResId = titleStringResId;
        this.subtitleResId = subtitleResId;
        this.iconResId = iconResId;
        this.onIconClickListener = onIconClickListener;
    }

    @Override public int getLayout() {
        return R.layout.item_header;
    }
    @Override public void bind(@NonNull ItemHeaderBinding viewBinding, int position) {
        viewBinding.title.setText(titleStringResId);
        viewBinding.getRoot().setBackgroundColor(color);
        if (subtitleResId != null) {
            viewBinding.subtitle.setText(subtitleResId);
        }
        viewBinding.subtitle.setVisibility(subtitleResId != null ? View.VISIBLE : View.GONE);

        if (iconResId > 0) {
            viewBinding.icon.setImageResource(iconResId);
            viewBinding.icon.setOnClickListener(onIconClickListener);
        }
        viewBinding.icon.setVisibility(iconResId > 0 ? View.VISIBLE : View.GONE);
    }

}
