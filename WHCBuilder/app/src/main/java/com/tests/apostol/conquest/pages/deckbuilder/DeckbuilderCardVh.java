package com.tests.apostol.conquest.pages.deckbuilder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.tests.apostol.conquest.R;

public class DeckbuilderCardVh extends RecyclerView.ViewHolder {
    public View root;
    public ImageView image;
    public TextView name;
    public Button quantity;

    public DeckbuilderCardVh(View v) {
        super(v);
        root = v.findViewById(R.id.item_draft_root);
        image = (ImageView) v.findViewById(R.id.item_draft_icon);
        name = (TextView) v.findViewById(R.id.item_draft_text);
        quantity = (Button) v.findViewById(R.id.item_draft_qty);
    }
}