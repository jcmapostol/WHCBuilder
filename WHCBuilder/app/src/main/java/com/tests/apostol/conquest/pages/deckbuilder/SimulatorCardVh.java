package com.tests.apostol.conquest.pages.deckbuilder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.tests.apostol.conquest.R;

public class SimulatorCardVh extends RecyclerView.ViewHolder {
    public View root;
    public ImageView icon;
    public TextView name;
    public ImageView down;

    public SimulatorCardVh(View v) {
        super(v);
        root = v.findViewById(R.id.item_sim_root);
        icon = (ImageView) v.findViewById(R.id.item_sim_icon);
        name = (TextView) v.findViewById(R.id.item_sim_text);
        down = (ImageView) v.findViewById(R.id.item_sim_down);
    }
}