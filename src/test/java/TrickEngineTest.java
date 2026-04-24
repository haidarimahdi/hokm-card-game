//import core.GameMaster;
//import model.Card;
//import model.CardSuit;
//import model.CardValue;
//import model.GameState;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import player.DummyAIPlayer;
//import player.Player;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//public class TrickEngineTest {
//
//    private GameState gameState;
//    private GameMaster referee;
//    private Player p1, p2, p3, p4;
//
//    @BeforeEach
//    public void setUp() {
//        gameState = new GameState();
//        p1 = new DummyAIPlayer("Player 1 (Lead)");
//        p2 = new DummyAIPlayer("Player 2");
//        p3 = new DummyAIPlayer("Player 3");
//        p4 = new DummyAIPlayer("Player 4");
//
//        List<Player> players = new ArrayList<>(List.of(p1, p2, p3, p4));
//        referee = new GameMaster(players, gameState);
//    }
//
//    /**
//     * Helper method to clear hands and assign exactly one specific card to each player.
//     * This forces the bots to play exactly the scenario we want to test.
//     */
//    private void setRiggedHands(Card c1, Card c2, Card c3, Card c4) {
//        p1.getHand().clear();
//        p1.receiveCards(List.of(c1));
//        p2.getHand().clear();
//        p2.receiveCards(List.of(c2));
//        p3.getHand().clear();
//        p3.receiveCards(List.of(c3));
//        p4.getHand().clear();
//        p4.receiveCards(List.of(c4));
//    }
//
//    @Test
//    public void testAiHokmSelection() {
//        List<Card> initialCards = List.of(
//                new Card(CardValue.TEN, CardSuit.DIAMONDS, 9),
//                new Card(CardValue.ACE, CardSuit.SPADES, 13)
//        );
//        p1.receiveCards(initialCards);
//
//        CardSuit chosenHokm = p1.chooseHokm(initialCards);
//
//        assertEquals(CardSuit.DIAMONDS, chosenHokm, "AI should choose the suit of the first card as Hokm");
//    }
//
//    @Test
//    public void testHighestLeadSuitWins() {
//        gameState.setHokm(CardSuit.HEARTS);
//
//        setRiggedHands(
//                new Card(CardValue.TWO, CardSuit.SPADES, 1),   // P1 leads
//                new Card(CardValue.TEN, CardSuit.SPADES, 9),   // P2
//                new Card(CardValue.KING, CardSuit.SPADES, 12), // P3 (Should Win)
//                new Card(CardValue.FIVE, CardSuit.SPADES, 4)   // P4
//                );
//        Player winner = referee.playTrick(p1);
//        assertEquals(p3, winner, "Player 3 should win the trick with the King of Spades");
//    }
//
//    @Test
//    public void testHokmCutWins() {
//        gameState.setHokm(CardSuit.HEARTS);
//        // P1 leads Spades with an Ace (highest card).
//        // P2 has no Spades, so they cut with a low Hokm (Hearts).
//        // P3 and P4 play lower Spades/Clubs.
//        setRiggedHands(
//                new Card(CardValue.ACE, CardSuit.SPADES, 13),  // P1 leads highest non-trump
//                new Card(CardValue.TWO, CardSuit.HEARTS, 1),   // P2 cuts with lowest trump (Should Win)
//                new Card(CardValue.KING, CardSuit.SPADES, 12), // P3
//                new Card(CardValue.FIVE, CardSuit.CLUBS, 4)    // P4 plays random off-suit
//        );
//
//        Player winner = referee.playTrick(p1);
//        assertEquals(p2, winner, "Player 2 should win by cutting with Hokm.");
//    }
//
//    @Test
//    public void testOverCutWins() {
//        gameState.setHokm(CardSuit.HEARTS); // Hearts is Hokm
//
//        // P1 leads Spades.
//        // P2 cuts with a low Hokm.
//        // P4 over-cuts with a higher Hokm.
//        setRiggedHands(
//                new Card(CardValue.ACE, CardSuit.SPADES, 13),  // P1 leads
//                new Card(CardValue.TWO, CardSuit.HEARTS, 1),   // P2 cuts with low trump
//                new Card(CardValue.KING, CardSuit.SPADES, 12), // P3
//                new Card(CardValue.TEN, CardSuit.HEARTS, 9)    // P4 over-cuts with higher trump (Should Win)
//        );
//
//        Player winner = referee.playTrick(p1);
//        assertEquals(p4, winner, "Player 4 should win by playing a higher Hokm than Player 2.");
//    }
//
//    @Test
//    public void testOffSuitLoses() {
//        gameState.setHokm(CardSuit.HEARTS); // Hearts is Hokm
//
//        // P1 leads Clubs.
//        // P2 plays a higher card, but it's Spades (wrong suit, not Hokm).
//        setRiggedHands(
//                new Card(CardValue.FIVE, CardSuit.CLUBS, 4),   // P1 leads (Should Win)
//                new Card(CardValue.ACE, CardSuit.SPADES, 13),  // P2 plays off-suit (Loses despite being an Ace)
//                new Card(CardValue.TWO, CardSuit.CLUBS, 1),    // P3 plays lower lead suit
//                new Card(CardValue.FOUR, CardSuit.CLUBS, 3)    // P4 plays lower lead suit
//        );
//
//        Player winner = referee.playTrick(p1);
//        assertEquals(p1, winner, "Player 1 should win. Player 2's Ace is off-suit and not Hokm.");
//    }
//}
