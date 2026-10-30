package core;

import model.Card;
import model.CardSuit;
import model.Trick;
import model.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RuleEngine {

    public static List<Card> getValidMoves(List<Card> hand, CardSuit leadSuit) {
        if (leadSuit == null) {
            return new ArrayList<>(hand);
        }

        List<Card> validCards = new ArrayList<>();
        for (Card card : hand) {
            if (card.suit() == leadSuit) {
                validCards.add(card);
            }
        }

        return !validCards.isEmpty() ? validCards : new ArrayList<>(hand);
    }

    public static boolean beats(Card challenger, Card currentBest, CardSuit leadSuit, CardSuit hokm) {
        boolean challengerIsHokm = challenger.suit() == hokm;
        boolean bestIsHokm = currentBest.suit() == hokm;

        if (challengerIsHokm) {
            return !bestIsHokm || challenger.rank() > currentBest.rank();
        }
        if (bestIsHokm) {
            return false;
        }
        return challenger.suit() == leadSuit && challenger.rank() > currentBest.rank();
    }

    public static Player determineTrickWinner(Trick trick, CardSuit hokm) {
        CardSuit leadSuit = trick.getLeadSuit();
        Player winner = null;
        Card winningCard = null;

        for (Map.Entry<Player, Card> entry : trick.getPlayedCards().entrySet()) {
            Player player = entry.getKey();
            Card card = entry.getValue();

            if (winningCard == null || beats(card, winningCard, leadSuit, hokm)) {
                winner = player;
                winningCard = card;
            }
        }
        return winner;
    }

//    public static boolean isValidMove(Card playedCard, List<Card> hand, CardSuit leadSuit) {
//        if (leadSuit == null || playedCard.suit() == leadSuit) {
//            return true;
//        }
//
//        for (Card card : hand) {
//            if (card.suit() == leadSuit) {
//                return false;
//            }
//        }
//
//        return true;
//    }
}
