package core;

import model.*;
import player.Player;

import java.util.List;

public class Dealer {
    public void dealCardsAndSetHokm(List<Player> players, Player hakem, GameState gameState) {
        Deck deck = new Deck();
        deck.shuffleDeck();

        System.out.println("Shuffling and dealing cards...");
        for (Player p : players) {
            p.getHand().clear(); // Clear previous hands
        }

        System.out.println("Dealing cards to players...");
        List<Card> hakemFirst5 = deck.dealCards(5);
        hakem.receiveCards(hakemFirst5);
        gameState.setHokm(hakem.chooseHokm(hakemFirst5));

        CardSuit hokm = gameState.getHokm();
        System.out.println("*** HOKM IS SET TO: " + hokm + " ***");

        notifyHokmChosen(players, hokm);

        for (Player p : players) {
            if (p != hakem)  {
                p.receiveCards(deck.dealCards(5));
            }
        }

        while (deck.getSize() > 0) {
            for (Player p : players) {
                p.receiveCards(deck.dealCards(4));
            }
        }
    }

    private void notifyHokmChosen(List<Player> players, CardSuit hokm) {
        for (Player player : players) {
            if (player instanceof GameObserver) {
                ((GameObserver) player).onHokmChosen(hokm);
            }
        }
    }
}
