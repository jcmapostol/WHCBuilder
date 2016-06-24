package com.tests.apostol.conquest;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class CardVh extends RecyclerView.ViewHolder {
    public View root;
    public ImageView icon;
    public TextView name;

    public CardVh(View v) {
        super(v);
        root = v.findViewById(R.id.item_basic_root);
        icon = (ImageView) v.findViewById(R.id.item_basic_icon);
        name = (TextView) v.findViewById(R.id.item_basic_text);
    }
}