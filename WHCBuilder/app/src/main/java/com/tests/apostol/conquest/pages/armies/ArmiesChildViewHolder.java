package com.tests.apostol.conquest.pages.armies;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.tests.apostol.conquest.R;

public class ArmiesChildViewHolder extends RecyclerView.ViewHolder {
    public TextView text;

    public ArmiesChildViewHolder(View itemView) {
        super(itemView);
        text = (TextView) itemView.findViewById(R.id.subitem_desc);
    }
}
