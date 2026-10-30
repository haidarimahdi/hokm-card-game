import ai.System1ReflexEngine;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import player.DummyAIPlayer;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class System1ReflexEngineTest {

    private System1ReflexEngine engine;
    private GameState gameState;
    private Player me, partner, opponent;

    @BeforeEach
    void setUp() {
        engine = new System1ReflexEngine();
        gameState = new GameState();
        gameState.setHokm(CardSuit.SPADES);

        me = new DummyAIPlayer("Me", Team.TEAM_A);
        partner = new DummyAIPlayer("Partner", Team.TEAM_A);
        opponent = new DummyAIPlayer("Opponent", Team.TEAM_B);
    }

    @Test
    void testDecide_PartnerIsWinning_PlaysLowestCard() {
        Trick trick = new Trick();
        // Opponent leads
        trick.addCard(opponent, new Card(CardSuit.HEARTS, CardValue.NINE));
        // Partner beats it
        trick.addCard(partner, new Card(CardSuit.HEARTS, CardValue.ACE));

        gameState.setCurrentTrick(trick);

        List<Card> myHand = List.of(
                new Card(CardSuit.HEARTS, CardValue.KING),
                new Card(CardSuit.HEARTS, CardValue.THREE)
        );

        Card decision = engine.decide(gameState, myHand, me);

        assertNotNull(decision, "System 1 should confidently decide");
        assertEquals(CardValue.THREE, decision.value(), "Should play the lowest card to let the partner win");
    }
}