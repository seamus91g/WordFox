package com.example.seamus.wordfox.list;

import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;

import com.example.seamus.wordfox.R;
import com.example.seamus.wordfox.databinding.ItemCardBinding;
import com.xwray.groupie.databinding.BindableItem;

/**
 * Created by Gilroy on 3/28/2018.
 */

public class CardItem extends BindableItem<ItemCardBinding> {

    public static final String INSET_TYPE_KEY = "inset_type";
    public static final String FULL_BLEED = "full_bleed";
    public static final String INSET = "inset";
    @ColorInt
    private int colorRes;
    private CharSequence text;
    private CharSequence text2;

    public CardItem(@ColorInt int colorRes) {
        this(colorRes, "");
    }

    public CardItem(@ColorInt int colorRes, CharSequence text) {
        this.colorRes = colorRes;
        this.text = text;
        getExtras().put(INSET_TYPE_KEY, INSET);
    }
    public CardItem(@ColorInt int colorRes, CharSequence text, CharSequence text2) {
        this.colorRes = colorRes;
        this.text = text;
        this.text2 = text2;
//        getExtras().put(INSET_TYPE_KEY, INSET);
    }

    @Override
    public int getLayout() {
        return R.layout.item_card;
    }

    @Override
    public void bind(@NonNull ItemCardBinding viewBinding, int position) {
        viewBinding.getRoot().setBackgroundColor(colorRes);
        viewBinding.text.setText(text);
        viewBinding.details.setText(text2);
    }


    public void setText(CharSequence text) {
        this.text = text;
    }

    public CharSequence getText() {
        return text;
    }
}
