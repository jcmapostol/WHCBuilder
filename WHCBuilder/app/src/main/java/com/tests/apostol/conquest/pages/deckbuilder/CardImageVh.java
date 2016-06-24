package com.tests.apostol.conquest.pages.deckbuilder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.tests.apostol.conquest.R;

public class CardImageVh extends RecyclerView.ViewHolder{
    public View root;
    public ImageView image;

    public CardImageVh(View v) {
        super(v);
        image = (ImageView) v.findViewById(R.id.subitem_image);
        root = v.findViewById(R.id.subitem_image_root);
    }
}
