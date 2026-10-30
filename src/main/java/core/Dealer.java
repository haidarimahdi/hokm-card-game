package core;

import model.*;

import java.util.List;

public class Dealer {
    private final GameObserver observer;

    public Dealer(GameObserver observer) {
        this.observer = observer;
    }

    public Player determineInitialHakem(List<Player> players) {
        Deck deck = new Deck();
        deck.shuffleDeck();

        int playerIndex = 0;
        while (deck.getSize() > 0) {
            Card drawn = deck.dealCards(1).getFirst();
            Player candidate = players.get(playerIndex);
            if (drawn.value() == CardValue.ACE) {
                return candidate;
            }
            playerIndex = (playerIndex + 1) % players.size();
        }
        return players.getFirst(); // Fallback, should never happen
    }

    public void dealCardsAndSetHokm(List<Player> players, Player hakem, GameState gameState) {
        Deck deck = new Deck();
        deck.shuffleDeck();

        for (Player p : players) {
            p.clearHand(); // Clear previous hands
        }

        List<Card> hakemFirstFive = deck.dealCards(5);
        hakem.receiveCards(hakemFirstFive);

        CardSuit hokm = hakem.chooseHokm(hakemFirstFive);
        gameState.setHokm(hokm);
        observer.onHokmChosen(hakem, hokm);

        for (Player p : players) {
            if (p != hakem)  {
                p.receiveCards(deck.dealCards(5));
            }
        }

        // 3. Deal remaining 32 cards in batches of 4 (two passes around the table)
        while (deck.getSize() > 0) {
            for (Player p : players) {
                p.receiveCards(deck.dealCards(4));
            }
        }
    }
}
