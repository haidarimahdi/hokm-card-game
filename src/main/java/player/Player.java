package player;

import model.Card;
import model.CardSuit;
import model.GameState;

import java.util.ArrayList;
import java.util.List;

public interface Player {
    String getName();
    void receiveCards(List<Card> cards);
    List<Card> getHand();
    CardSuit chooseHokm(List<Card> firstFiveCards);
    Card playCard(GameState currentState);
    default void resetMemory() {}

//    default List<Card> getValidCards(GameState state, List<Card> currentHand) {
//        if (state == null || state.getCurrentTrick() == null || state.getCurrentTrick().getLeadSuit() == null) {
//            return new ArrayList<>(currentHand); // No lead suit, all cards are valid
//        }
//
//        CardSuit leadSuit = state.getCurrentTrick().getLeadSuit();
//        List<Card> validCards = new ArrayList<>();
//
//        for (Card c : currentHand) {
//            if (c.suit() == leadSuit) {
//                validCards.add(c);
//            }
//        }
//
//        return validCards.isEmpty() ? new ArrayList<>(currentHand) : validCards;
//    }
}
