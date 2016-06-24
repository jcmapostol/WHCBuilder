package com.tests.apostol.conquest.pages.deckbuilder;

import com.tests.apostol.conquest.Card;
import com.tests.apostol.conquest.ParentObject;

import java.util.ArrayList;
import java.util.List;

public abstract class DeckbuilderAdapterHelper {
    public static List<ParentObject> makeCardWrappers(List<Card> cards) {
        List<ParentObject> parentObjects = new ArrayList<>();

        for (Card card : cards) {
            CardVhData wrapper = new CardVhData(card);
            parentObjects.add(wrapper);
        }

        return parentObjects;
    }
}
