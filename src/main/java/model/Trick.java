package model;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class Trick {
    private CardSuit leadSuit = null;
    private final Map<Player, Card> playedCards = new LinkedHashMap<>();

    public void addCard(Player player, Card card) {
        Objects.requireNonNull(player, "Player cannot be null");
        Objects.requireNonNull(card, "Card cannot be null");

        if (leadSuit == null) {
            leadSuit = card.suit();
        }
        playedCards.put(player, card);
    }

    public CardSuit getLeadSuit() {
        return leadSuit;
    }

    public Map<Player, Card> getPlayedCards() {
        return Collections.unmodifiableMap(playedCards);
    }

    public boolean isEmpty() {
        return playedCards.isEmpty();
    }

    public Trick asReadOnly() {
        Trick parent = this;
        return new Trick() {
            @Override
            public void addCard(Player player, Card card) {
                throw new UnsupportedOperationException("Cannot modify a read-only Trick");
            }

            @Override
            public CardSuit getLeadSuit() {
                return parent.getLeadSuit();
            }

            @Override
            public Map<Player, Card> getPlayedCards() {
                return parent.getPlayedCards();
            }

            @Override
            public boolean isEmpty() {
                return parent.isEmpty();
            }

            @Override
            public Trick asReadOnly() {
                return this; // Already read-only
            }
        };
    }
}
