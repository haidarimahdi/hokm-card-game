package model;

import java.util.LinkedHashMap;
import java.util.Map;

public class Trick {
    private CardSuit leadSuit = null;
    private final Map<player.Player, Card> playedCards = new LinkedHashMap<>();

    public void addCard(player.Player player, Card card) {
        if (leadSuit == null) {
            leadSuit = card.suit();
        }
        playedCards.put(player, card);
    }

    public CardSuit getLeadSuit() {
        return leadSuit;
    }

    public Map<player.Player, Card> getPlayedCards() {
        return playedCards;
    }

    public boolean isEmpty() {
        return playedCards.isEmpty();
    }
}
