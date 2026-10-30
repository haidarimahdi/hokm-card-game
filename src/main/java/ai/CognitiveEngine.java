package ai;

import model.Card;
import model.GameState;
import model.Player;

import java.util.List;

public interface CognitiveEngine {
    /**
     * @return The chosen Card, or null if this engine cannot confidently decide.
     */
    Card decide(GameState state, List<Card> validCards, Player self);
}
