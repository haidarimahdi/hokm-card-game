import ai.AiMemoryTracker;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import player.DummyAIPlayer;

import static org.junit.jupiter.api.Assertions.*;

public class AiMemoryTrackerTest {

    private AiMemoryTracker memoryTracker;
    private Player p1, p2;

    @BeforeEach
    void setUp() {
        memoryTracker = new AiMemoryTracker();
        p1 = new DummyAIPlayer("P1", Team.TEAM_A);
        p2 = new DummyAIPlayer("P2", Team.TEAM_B);
    }

    @Test
    void testOnCardPlayed_DeducesVoidCorrectly() {
        Trick trick = new Trick();
        // P1 leads with Clubs
        Card leadCard = new Card(CardSuit.CLUBS, CardValue.KING);
        trick.addCard(p1, leadCard);
        memoryTracker.onCardPlayed(p1, leadCard, trick);

        // P2 plays Diamonds instead of Clubs
        Card offSuitCard = new Card(CardSuit.DIAMONDS, CardValue.TWO);
        trick.addCard(p2, offSuitCard);
        memoryTracker.onCardPlayed(p2, offSuitCard, trick);

        String voids = memoryTracker.getKnownVoidsString();

        assertTrue(voids.contains("P2"), "Memory should register P2");
        assertTrue(voids.contains("CLUBS"), "Memory should deduce P2 is void in Clubs");
    }
}