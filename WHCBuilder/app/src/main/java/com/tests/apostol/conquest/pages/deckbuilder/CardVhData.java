package com.tests.apostol.conquest.pages.deckbuilder;

import com.tests.apostol.conquest.Card;
import com.tests.apostol.conquest.ParentObject;

import java.util.ArrayList;
import java.util.List;

public class CardVhData implements ParentObject {
    private List<Object> _children;
    private Card _card;
    private boolean _isExpanded;

    public CardVhData(Card card) {
        _card = card;
        _children = new ArrayList<>();
        _children.add(card.getImageId());
    }

    public Card getCard() { return _card; }

    @Override
    public List<Object> getChildren() {
        return _children;
    }

    @Override
    public void setChildren(List<Object> children) {
        _children = children;
    }

    @Override
    public boolean getExpanded() {
        return _isExpanded;
    }

    @Override
    public void setExpanded(boolean isExpanded) {
        _isExpanded = isExpanded;
    }
}
