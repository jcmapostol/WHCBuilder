package com.tests.apostol.conquest;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;

import java.util.List;

public class HeaderAdapter extends ExpandableAdapter {
    private View.OnClickListener _childListener;

    public HeaderAdapter(List<ParentObject> parentObjects, View.OnClickListener childListener) {
        super(parentObjects);
        _childListener = childListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateParentViewHolder(ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.header_basic, parent, false);
        return new HeaderVh(v);
    }

    @Override
    public RecyclerView.ViewHolder onCreateChildViewHolder(ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.item_basic, parent, false);
        v.setOnClickListener(_childListener);
        return new CardVh(v);
    }

    @Override
    public void onBindParent(RecyclerView.ViewHolder holder, final int position, Object data) {
        final HeaderVh vh = (HeaderVh) holder;
        final HeaderVhData actual = (HeaderVhData) data;

        if (actual.getExpanded()) {
            RotateAnimation newAnim = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            newAnim.setDuration(250);
            newAnim.setFillAfter(true);
            vh.down.startAnimation(newAnim);
        }

        vh.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HeaderVhData header = (HeaderVhData) getDataAt(vh.getAdapterPosition());

                if (hasChildren(header)) {
                    removeChildren(header);
                    RotateAnimation newAnim = new RotateAnimation(180, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    newAnim.setDuration(250);
                    newAnim.setFillAfter(true);
                    vh.down.startAnimation(newAnim);
                } else {
                    addChildren(header);
                    RotateAnimation newAnim = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    newAnim.setDuration(250);
                    newAnim.setFillAfter(true);
                    vh.down.startAnimation(newAnim);
                }
            }
        });
        vh.text.setText(actual.getText());
    }

    @Override
    public void onBindChild(RecyclerView.ViewHolder holder, int position, Object data) {
        CardVh card = (CardVh) holder;
        Card cardData = (Card) data;
        card.icon.setImageResource(cardData.getFaction().getIconId());
        card.name.setText(cardData.getName());
        card.name.setTextColor(cardData.getFaction().getTextColor());
        card.root.setBackgroundColor(cardData.getFaction().getColor());
    }
}
