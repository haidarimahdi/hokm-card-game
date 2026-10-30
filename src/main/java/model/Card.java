package model;

import java.util.Comparator;
import java.util.Objects;

public record Card (CardSuit suit, CardValue value) implements Comparable<Card> {
    public Card {
        Objects.requireNonNull(suit, "Card suit cannot be null");
        Objects.requireNonNull(value, "Card value cannot be null");
    }

    private static final Comparator<Card> CARD_COMPARATOR =
            Comparator.comparing(Card::suit)
                      .thenComparing(Card::value);

    public Card(CardValue value, CardSuit suit) {
        this(suit, value);
    }

    public int rank() {
        return value.ordinal();
    }

    @Override
    public String toString() {
        return value.name() + " OF " + suit.name();
    }

    @Override
    public int compareTo(Card other) {
        Objects.requireNonNull(other, "Cannot compare to null");
        return CARD_COMPARATOR.compare(this, other);
    }

    /**
     * @return a string representation of the card suitable for visual display.
     */
    public String toVisualString() {
        String valStr = switch (value) {
            case JACK -> "J";
            case QUEEN -> "Q";
            case KING -> "K";
            case ACE -> "A";
            default -> String.valueOf(value.ordinal() + 2); // TWO (ordinal 0) -> "2"
        };

        return valStr + suit.getSymbol();
    }
}