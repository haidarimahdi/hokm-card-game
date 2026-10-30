import core.RuleEngine;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import player.DummyAIPlayer;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RuleEngineExperimentalTest {

    private Player p1, p2, p3, p4;

    @BeforeEach
    void setUp() {
        p1 = new DummyAIPlayer("P1", Team.TEAM_A);
        p2 = new DummyAIPlayer("P2", Team.TEAM_B);
        p3 = new DummyAIPlayer("P3", Team.TEAM_A);
        p4 = new DummyAIPlayer("P4", Team.TEAM_B);
    }

    @Test
    void testValidMoves_MustFollowLeadSuit() {
        List<Card> hand = List.of(
                new Card(CardSuit.HEARTS, CardValue.TEN),
                new Card(CardSuit.SPADES, CardValue.ACE),
                new Card(CardSuit.SPADES, CardValue.TWO)
        );

        List<Card> validMoves = RuleEngine.getValidMoves(hand, CardSuit.SPADES);

        assertEquals(2, validMoves.size(), "Should only allow playing Spades");
        assertTrue(validMoves.stream().allMatch(c -> c.suit() == CardSuit.SPADES));
    }

    @Test
    void testValidMoves_VoidInLeadSuit_AllowsAll() {
        List<Card> hand = List.of(
                new Card(CardSuit.HEARTS, CardValue.TEN),
                new Card(CardSuit.DIAMONDS, CardValue.KING)
        );

        List<Card> validMoves = RuleEngine.getValidMoves(hand, CardSuit.SPADES);

        assertEquals(2, validMoves.size(), "Should allow any card when void in lead suit");
    }

    @Test
    void testDetermineTrickWinner_HokmOvercut() {
        Trick trick = new Trick();
        trick.addCard(p1, new Card(CardSuit.CLUBS, CardValue.ACE));   // Lead
        trick.addCard(p2, new Card(CardSuit.SPADES, CardValue.TWO));  // Cut with Hokm
        trick.addCard(p3, new Card(CardSuit.HEARTS, CardValue.KING)); // Off-suit
        trick.addCard(p4, new Card(CardSuit.SPADES, CardValue.TEN));  // Overcut with higher Hokm

        Player winner = RuleEngine.determineTrickWinner(trick, CardSuit.SPADES);

        assertEquals(p4, winner, "Highest Hokm should win the trick");
    }
}