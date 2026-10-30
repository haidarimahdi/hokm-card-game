package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {
    private final List<Card> cards = new ArrayList<>();

    public Deck() {
        for (CardSuit suit : CardSuit.values()) {
            for (CardValue value : CardValue.values()) {
                cards.add(new Card(suit, value));
            }
        }
    }

    public void shuffleDeck() {
        Collections.shuffle(cards);
    }

    public int getSize() {
        return cards.size();
    }

    public boolean isEmpty() {
        return cards.isEmpty();
    }
    public List<Card> dealCards(int numberOfCards) {
        if (numberOfCards < 0 || numberOfCards > cards.size()) {
            throw new IllegalArgumentException("Not enough cards in the deck to deal " + numberOfCards + " cards.");
        }
        int fromIndex = cards.size() - numberOfCards;
        List<Card> TailSlice = cards.subList(fromIndex, cards.size());
        List<Card> dealtCards = new ArrayList<>(TailSlice);
        TailSlice.clear(); // Remove the dealt cards from the deck
        return dealtCards;
    }
}
