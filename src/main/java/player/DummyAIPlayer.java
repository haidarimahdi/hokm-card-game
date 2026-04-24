package player;

import model.Card;
import model.CardSuit;
import model.GameState;

import java.util.ArrayList;
import java.util.List;

public class DummyAIPlayer implements Player{
    private final String name;
    private final List<Card> hand;

    public DummyAIPlayer(String name) {
        this.name = name;
        this.hand = new ArrayList<>();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void receiveCards(List<Card> cards) {
        hand.addAll(cards);
    }

    @Override
    public CardSuit chooseHokm(List<Card> firstFiveCards) {
        CardSuit chosen = firstFiveCards.getFirst().suit();
        System.out.println(name + " (AI) chooses Hokm: " + chosen);
        return chosen;
    }

    @Override
    public Card playCard(GameState currentState) {
        CardSuit leadSuit = null;

        if (currentState != null && currentState.getCurrentTrick() != null) {
            leadSuit = currentState.getCurrentTrick().getLeadSuit();
        }

        List<Card> validCards = new ArrayList<>();

        if (leadSuit != null) {
            for (Card c : hand) {
                if (c.suit() == leadSuit) {
                    validCards.add(c);
                }
            }
        }

        if (validCards.isEmpty()) {
            validCards.addAll(hand);
        }

        Card chosenCard = validCards.getFirst();
        hand.remove(chosenCard);

        try {
            Thread.sleep(800);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return chosenCard;
    }

    @Override
    public List<Card> getHand() {
        return hand;
    }
}
