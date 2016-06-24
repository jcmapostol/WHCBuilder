package com.tests.apostol.conquest;

import java.util.ArrayList;
import java.util.List;

public class Deck {
    private List<Card> _actual;
    private List<Card> _cards;

    public Deck(List<Card> cards) {
        _actual = cards;
        _cards = new ArrayList<>();

        for (int i = 0; i < cards.size(); i++) {
            Card c = cards.get(i);

            for (int j = 0; j < c.getQuantity(); j++) {
                _cards.add(c);
            }
        }
    }

    public List<Card> getCards() { return _actual; }
    public List<Card> shuffleAndDraw(int amount) {
        for (int i = 0; i < _cards.size(); i++) {
            int rand = (int)Math.floor(Math.random() * (_cards.size() - i));
            Card c1 = _cards.get(rand);
            Card c2 = _cards.get(i);
            _cards.set(i, c1);
            _cards.set(rand, c2);
        }

        List<Card> drawn = new ArrayList<>();

        for (int i = 0; i < amount; i++)
            drawn.add(_cards.get(i));

        return drawn;
    }
}
