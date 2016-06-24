package com.tests.apostol.conquest;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class HeaderVh extends RecyclerView.ViewHolder {
    public TextView text;
    public ImageView down;
    public View root;

    public HeaderVh(View itemView) {
        super(itemView);
        root = itemView.findViewById(R.id.header_basic_root);
        text = (TextView) itemView.findViewById(R.id.header_basic_text);
        down = (ImageView) itemView.findViewById(R.id.header_basic_icon);
    }
}
