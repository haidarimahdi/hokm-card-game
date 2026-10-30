import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import player.DummyAIPlayer;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DealingPhaseTest {

    private Deck deck;
    private GameState gameState;
    private List<Player> players;

    @BeforeEach
    public void setUp() {
        deck = new Deck();
        deck.shuffleDeck();
        gameState = new GameState();

        players = new ArrayList<>();
        players.add(new DummyAIPlayer("Player 1"));
        players.add(new DummyAIPlayer("Player 2"));
        players.add(new DummyAIPlayer("Player 3"));
        players.add(new DummyAIPlayer("Player 4"));
    }

    @Test
    public void testFullDealingPhase() {
        System.out.println("--- Starting Dealing Phase Test ---");

        // 1. Assert initial deck size
        assertEquals(52, deck.getSize(), "Deck should start with 52 cards");

        // 2. Deal first 5 cards to the Hakem (Player 1)
        Player hakem = players.getFirst();
        List<Card> hakemFirstFive = deck.dealCards(5);
        hakem.receiveCards(hakemFirstFive);

        assertEquals(5, hakem.getHand().size(), "Hakem should have 5 cards");
        assertEquals(47, deck.getSize(), "Deck should have 47 cards left");

        // 3. Hakem chooses Hokm
        CardSuit chosenHokm = hakem.chooseHokm(hakemFirstFive);
        gameState.setHokm(chosenHokm);
        assertNotNull(gameState.getHokm(), "Hokm must not be null");
        System.out.println("\n[GAME STATE] Hokm is set to: " + gameState.getHokm());

        // 4. Deal first 5 cards to the remaining 3 players
        for (int i = 1; i < 4; i++) {
            players.get(i).receiveCards(deck.dealCards(5));
        }
        assertEquals(32, deck.getSize(), "Deck should have 32 cards left after initial 5s are dealt");

        // 5. Deal the remaining 8 cards to all 4 players
        for (Player p : players) {
            p.receiveCards(deck.dealCards(8));
        }

        // 6. Final Assertions
        assertEquals(0, deck.getSize(), "Deck should be completely empty");

        System.out.println("\n--- Final Hands ---");
        for (Player p : players) {
            // Assert that every player mathematically has 13 cards
            assertEquals(13, p.getHand().size(), p.getName() + " does not have 13 cards!");

            // Print the hands so you can visually verify them
            System.out.println(p.getName() + " holds: " + p.getHand());
        }
        System.out.println("-----------------------------------");
    }
    @Test
    public void testHakemChoosesHokm() {
        Player hakem = players.getFirst();
        List<Card> hakemFirstFive = List.of(
                new Card(CardValue.TEN, CardSuit.CLUBS),
                new Card(CardValue.ACE, CardSuit.SPADES),
                new Card(CardValue.KING, CardSuit.CLUBS),
                new Card(CardValue.QUEEN, CardSuit.DIAMONDS),
                new Card(CardValue.NINE, CardSuit.HEARTS)
        );
        CardSuit chosenHokm = hakem.chooseHokm(hakemFirstFive);
        assertEquals(CardSuit.CLUBS, chosenHokm, "Hakem should choose HEARTS");
    }
}
