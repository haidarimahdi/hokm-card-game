import model.Card;
import model.Deck;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class DeckTest {
    private Deck deck;

    @BeforeEach
    public void setup() {
        deck = new Deck();
    }

    @Test
    public void testDeckInitialization() {
        assertEquals(52, deck.getSize(), "A new deck must have exactly 52 cards.");

        List<Card> allCards = deck.dealCards(52);
        Set<Card> uniqueCards = new HashSet<>(allCards);

        assertEquals(52, uniqueCards.size(), "All cards must be unique.");
    }

    @Test
    public void testDealCardsReducesDeckSize() {
        List<Card> dealts = deck.dealCards(10);

        assertEquals(10, dealts.size(), "Should have dealt 10 cards.");
        assertEquals(42, deck.getSize(), "After dealing 10 cards, the deck should have 42 cards left.");
    }

    @Test
    public void testDealTooManyCardsThrowsException() {
        deck.dealCards(50);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            deck.dealCards(3);
        });

        assertTrue(exception.getMessage().contains("Not enough cards"), "Should throw an exception when over-dealing.");
    }
}
