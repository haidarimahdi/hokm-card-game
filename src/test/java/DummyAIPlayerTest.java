//import model.Card;
//import model.CardSuit;
//import model.CardValue;
//import model.GameState;
//import model.Trick;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Disabled;
//import org.junit.jupiter.api.Test;
//import player.DummyAIPlayer;
//import player.Player;
//
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotEquals;
//
//@Disabled
//public class DummyAIPlayerTest {
//
//    private DummyAIPlayer bot;
//    private GameState gameState;
//
//    @BeforeEach
//    public void setUp() {
//        bot = new DummyAIPlayer("Test Bot");
//        gameState = new GameState();
//    }
//
//    @Test
//    public void testAiFollowsLeadSuitIfPossible() {
//        // Give the bot a mixed hand
//        bot.receiveCards(List.of(
//                new Card(CardValue.FIVE, CardSuit.CLUBS, 4),
//                new Card(CardValue.TEN, CardSuit.HEARTS, 9),
//                new Card(CardValue.ACE, CardSuit.SPADES, 13) // The required suit
//        ));
//
//        // Simulate a trick where Spades were led
//        Trick trick = new Trick();
//        Player dummyLead = new DummyAIPlayer("Lead");
//        trick.addCard(dummyLead, new Card(CardValue.TWO, CardSuit.SPADES, 1));
//
//        gameState.setCurrentTrick(trick); // Lead suit is now SPADES
//
//        // Ask the bot to play
//        Card playedCard = bot.playCard(gameState);
//
//        // The bot MUST play the Spade, even though it's the last card in its hand
//        assertEquals(CardSuit.SPADES, playedCard.suit(), "The AI must follow the lead suit (Spades).");
//    }
//
//    @Test
//    public void testAiPlaysAnyCardIfVoidInLeadSuit() {
//        // Give the bot a hand with NO Spades
//        bot.receiveCards(List.of(
//                new Card(CardValue.FIVE, CardSuit.CLUBS, 4),
//                new Card(CardValue.TEN, CardSuit.HEARTS, 9)
//        ));
//
//        // Simulate a trick where Spades were led
//        Trick trick = new Trick();
//        Player dummyLead = new DummyAIPlayer("Lead");
//        trick.addCard(dummyLead, new Card(CardValue.TWO, CardSuit.SPADES, 1));
//
//        gameState.setCurrentTrick(trick); // Lead suit is now SPADES
//
//        // Ask the bot to play
//        Card playedCard = bot.playCard(gameState);
//
//        // The bot doesn't have Spades, so it should play its first available card (Clubs)
//        assertNotEquals(CardSuit.SPADES, playedCard.suit(), "The AI cannot play a suit it doesn't have.");
//        assertEquals(CardSuit.CLUBS, playedCard.suit(), "The AI should default to its first valid card.");
//    }
//}