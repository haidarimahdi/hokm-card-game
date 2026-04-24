package core;

import model.Card;
import model.CardSuit;
import model.Trick;
import player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RuleEngine {

    public static List<Card> getValidMoves(List<Card> hand, CardSuit leadSuit) {
        if (leadSuit == null) {
            return new ArrayList<>(hand);
        }

        List<Card> validCards = new ArrayList<>();
        boolean hasLeadSuit = false;

        for (Card card : hand) {
            if (card.suit() == leadSuit) {
                validCards.add(card);
                hasLeadSuit = true;
            }
        }

        return hasLeadSuit ? validCards : new ArrayList<>(hand);
    }

    public static boolean isValidMove(Card playedCard, List<Card> hand, CardSuit leadSuit) {
        if (leadSuit == null || playedCard.suit() == leadSuit) {
            return true;
        }

        for (Card card : hand) {
            if (card.suit() == leadSuit) {
                return false;
            }
        }

        return true;
    }

public static Player determineTrickWinner(Trick trick, CardSuit hokm) {
        CardSuit leadSuit = trick.getLeadSuit();
        Player winner = null;
        Card winningCard = null;

        for (Map.Entry<Player, Card> entry : trick.getPlayedCards().entrySet()) {
            Player player = entry.getKey();
            Card card = entry.getValue();

            if (winningCard == null) {
                winner = player;
                winningCard = card;
                continue;
            }

            boolean cardIsHokm = card.suit() == hokm;
            boolean winningCardIsHokm = winningCard.suit() == hokm;
            boolean cardFollowsLead = card.suit() == leadSuit;

            if (cardIsHokm) {
                if (!winningCardIsHokm || card.rank() > winningCard.rank()) {
                    winner = player;
                    winningCard = card;
                }
            } else if (!winningCardIsHokm && cardFollowsLead && card.rank() > winningCard.rank()) {
                winner = player;
                winningCard = card;
            }
        }

        return winner;
    }


}
