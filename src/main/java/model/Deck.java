package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {
    private final List<Card> cards = new ArrayList<>();

    public Deck() {
        for (CardSuit suit : CardSuit.values()) {
            for (CardValue value : CardValue.values()) {
                cards.add(new Card(value, suit, value.ordinal() + 1));
            }
        }
    }

    public void shuffleDeck() {
        Collections.shuffle(cards);
    }

    public int getSize() {
        return cards.size();
    }

    public List<Card> dealCards(int numberOfCards) {
        if (numberOfCards > cards.size()) {
            throw new IllegalArgumentException("Not enough cards in the deck to deal " + numberOfCards + " cards.");
        }
        List<Card> dealCards = new ArrayList<>(cards.subList(0, numberOfCards));
        cards.subList(0, numberOfCards).clear();
        return dealCards;

    }
}
