package com.tests.apostol.conquest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class HeaderAdapterHelper {
    public static List<ParentObject> makeHeadersBySet(List<Card> cards) {
        List<ParentObject> parentObjects = new ArrayList<>();
        List<Card.CardSets> setsInOrder = new ArrayList<>();
        Map<Card.CardSets, List<Object>> cardsBySet = new HashMap<>();
        Card.CardSets currentSet = null;

        for (Card card : cards) {
            if (!cardsBySet.containsKey(card.getSet())) {
                currentSet = card.getSet();
                setsInOrder.add(currentSet);
                cardsBySet.put(currentSet, new ArrayList<>());
            }

            cardsBySet.get(currentSet).add(card);
        }

        for (int i = 0; i < setsInOrder.size(); i++) {
            HeaderVhData header = new HeaderVhData(cardsBySet.get(setsInOrder.get(i)), setsInOrder.get(i).getName());
            parentObjects.add(header);
        }

        return parentObjects;
    }
    public static List<ParentObject> makeHeadersByFaction(List<Card> cards) {
        List<ParentObject> parentObjects = new ArrayList<>();
        List<Card.Factions> factionsInOrder = new ArrayList<>();
        Map<Card.Factions, List<Object>> cardsByFaction = new HashMap<>();
        Card.Factions currentFaction = null;

        for (Card card : cards) {
            if (!cardsByFaction.containsKey(card.getFaction())) {
                currentFaction = card.getFaction();
                factionsInOrder.add(currentFaction);
                cardsByFaction.put(currentFaction, new ArrayList<>());
            }

            cardsByFaction.get(currentFaction).add(card);
        }

        for (int i = 0; i < factionsInOrder.size(); i++) {
            HeaderVhData header = new HeaderVhData(cardsByFaction.get(factionsInOrder.get(i)), factionsInOrder.get(i).getName());
            parentObjects.add(header);
        }

        return parentObjects;
    }
}
