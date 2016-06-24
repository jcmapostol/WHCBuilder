package com.tests.apostol.conquest.pages.armies;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.tests.apostol.conquest.R;

public class ArmiesParentViewHolder extends RecyclerView.ViewHolder {
    public ImageView icon;
    public TextView name;
    public TextView warlordName;
    public View root;
    public ImageButton edit;
    public ImageButton delete;

    public ArmiesParentViewHolder(View itemView) {
        super(itemView);
        root = itemView.findViewById(R.id.item_arm_root);
        icon = (ImageView) itemView.findViewById(R.id.item_arm_icon);
        name = (TextView) itemView.findViewById(R.id.item_arm_name);
        warlordName = (TextView) itemView.findViewById(R.id.item_arm_warlord);
        delete = (ImageButton) itemView.findViewById(R.id.item_arm_delete);
        edit = (ImageButton) itemView.findViewById(R.id.item_arm_edit);
    }
}
