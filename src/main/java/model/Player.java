package model;

import java.util.List;

public interface Player {
    String getName();
    Team getTeam();
    List<Card> getHand();
    void receiveCards(List<Card> cards);
    void clearHand();
    CardSuit chooseHokm(List<Card> firstFiveCards);
    Card playCard(GameState currentState);
}
