package com.tests.apostol.conquest.pages.deckbuilder;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;

import com.tests.apostol.conquest.Card;
import com.tests.apostol.conquest.CardVh;
import com.tests.apostol.conquest.ExpandableAdapter;
import com.tests.apostol.conquest.ParentObject;
import com.tests.apostol.conquest.R;

import java.util.ArrayList;
import java.util.List;

public class SimulatorAdapter extends ExpandableAdapter {
    private List<Card> _cards;

    public SimulatorAdapter(List<ParentObject> dataSet) {
        super(dataSet);
        _cards = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateParentViewHolder(ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.item_sim, parent, false);
        return new SimulatorCardVh(v);
    }

    @Override
    public RecyclerView.ViewHolder onCreateChildViewHolder(ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.subitem_image, parent, false);
        return new CardImageVh(v);
    }

    public List<Card> getCards() {
        return _cards;
    }

    @Override
    public void onBindParent(RecyclerView.ViewHolder holder, int position, Object data) {
        final SimulatorCardVh vh = (SimulatorCardVh) holder;
        final CardVhData wrapper = (CardVhData) data;
        final Card card = wrapper.getCard();

        vh.name.setText(card.getName());
        vh.name.setTextColor(card.getFaction().getTextColor());
        vh.icon.setImageResource(card.getFaction().getIconId());

        if (card.getFaction() == Card.Factions.NA)
            vh.down.setImageResource(R.drawable.icon_down_dark);
        else
            vh.down.setImageResource(R.drawable.icon_down);

        vh.root.setBackgroundColor(card.getFaction().getColor());
        vh.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasChildren(wrapper)) {
                    removeChildren(wrapper);
                    RotateAnimation newAnim = new RotateAnimation(180, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    newAnim.setDuration(250);
                    newAnim.setFillAfter(true);
                    vh.down.startAnimation(newAnim);
                } else {
                    addChildren(wrapper);
                    RotateAnimation newAnim = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    newAnim.setDuration(250);
                    newAnim.setFillAfter(true);
                    vh.down.startAnimation(newAnim);
                }
            }
        });
    }

    @Override
    public void onBindChild(RecyclerView.ViewHolder holder, int position, Object data) {
        CardImageVh child = (CardImageVh) holder;
        child.image.setImageResource((int) data);
    }
}