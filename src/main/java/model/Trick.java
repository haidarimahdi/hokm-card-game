package model;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class Trick {
    private CardSuit leadSuit = null;
    private final Map<Player, Card> playedCards = new LinkedHashMap<>();

    public void addCard(Player player, Card card) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(card);

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
}
