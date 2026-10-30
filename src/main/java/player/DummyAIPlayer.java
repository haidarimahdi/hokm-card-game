package player;

import model.*;

import java.util.List;

public class DummyAIPlayer extends BasePlayer {
    public DummyAIPlayer(String name, Team team) {
        super(name, team);
    }

    public DummyAIPlayer(String name) {
        super(name, Team.TEAM_A);
    }

    @Override
    public CardSuit chooseHokm(List<Card> firstFiveCards) {
        return firstFiveCards.getFirst().suit();
    }

    @Override
    protected Card selectCard(GameState state, List<Card> validCards) {
        return validCards.getFirst();
    }
}
