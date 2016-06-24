package com.tests.apostol.conquest.pages.deckbuilder;

import android.support.design.widget.TabLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tests.apostol.conquest.ExpandableAdapter;
import com.tests.apostol.conquest.Card;
import com.tests.apostol.conquest.ParentObject;
import com.tests.apostol.conquest.R;

import java.util.ArrayList;
import java.util.List;

public class DeckbuilderAdapter extends ExpandableAdapter {
    private TabLayout.Tab _tab;
    private int _total;
    private boolean _mutable;
    private List<Card> _cards;

    public DeckbuilderAdapter(List<ParentObject> dataSet, boolean mutable, TabLayout.Tab tab) {
        super(dataSet);
        _mutable = mutable;
        _tab = tab;
        _cards = new ArrayList<>();

        for (ParentObject po : dataSet) {
            if (po instanceof CardVhData){
                CardVhData data = (CardVhData) po;
                Card c = data.getCard();
                _cards.add(c);
                _total += c.getQuantity();
            }
        }
        _tab.setText(String.valueOf(_total));
    }

    public int getCardTotal() {
        return _total;
    }

    @Override
    public RecyclerView.ViewHolder onCreateParentViewHolder(ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.item_draft, parent, false);
        return new DeckbuilderCardVh(v);
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
    public void addParent(ParentObject po) {
        if (_mutable) {
            if (po instanceof CardVhData) {
                CardVhData wrapper = (CardVhData) po;
                if (!_cards.contains(wrapper.getCard())) {
                    Card card = wrapper.getCard();
                    if (card.getType() != Card.Types.SY)
                        card.setQuantity(3);
                    else
                        card.setQuantity(1);

                    _cards.add(card);
                    _total += card.getQuantity();
                    _tab.setText(String.valueOf(_total));
                    super.addParent(po);
                }
            }
        }
    }

    @Override
    public void removeParent(ParentObject po) {
        if (_mutable) {
            if (po instanceof CardVhData) {
                Card card = ((CardVhData) po).getCard();
                _cards.remove(card);
                _total -= card.getQuantity();
                _tab.setText(String.valueOf(_total));
            }

            super.removeParent(po);
        }
    }

    @Override
    public void onBindParent(RecyclerView.ViewHolder holder, int position, Object data) {
        final DeckbuilderCardVh parent = (DeckbuilderCardVh) holder;
        final CardVhData wrapper = (CardVhData) data;
        final Card card = wrapper.getCard();

        parent.name.setText(card.getName());
        parent.name.setTextColor(card.getFaction().getTextColor());
        parent.root.setBackgroundColor(card.getFaction().getColor());
        parent.image.setImageResource(card.getFaction().getIconId());
        parent.quantity.setText(String.valueOf(card.getQuantity()));

        if (card.getType() != Card.Types.SY && card.getType() != Card.Types.WR && card.getLoyalty() != Card.Loyalties.SG) {
            View.OnClickListener quantityChanger = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (_mutable) {
                        if (card.getQuantity() == 1) {
                            card.setQuantity(3);
                            _total += 2;
                        } else {
                            _total--;
                            card.setQuantity(card.getQuantity() - 1);
                        }

                        _tab.setText(String.valueOf(_total));
                        notifyItemChanged(parent.getAdapterPosition());
                    }
                }
            };

            parent.quantity.setOnClickListener(quantityChanger);
        }

        parent.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasChildren(wrapper)) {
                    removeChildren(wrapper);
                } else {
                    addChildren(wrapper);
                }
            }
        });

        parent.root.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (card.getLoyalty() != Card.Loyalties.SG) {
                    if (hasChildren(wrapper)) {
                        removeChildren(wrapper);
                    }

                    removeParent(wrapper);
                    return true;
                }

                return false;
            }
        });
    }

    @Override
    public void onBindChild(RecyclerView.ViewHolder holder, int position, Object data) {
        CardImageVh child = (CardImageVh) holder;
        child.image.setImageResource((int) data);
    }
}