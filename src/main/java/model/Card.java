package model;

public record Card (CardValue value, CardSuit suit, int rank){

    @Override
    public String toString() {
        return value.name() + " OF " + suit.name();
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
            default -> String.valueOf(rank + 1);
        };
        return valStr + suit.getSymbol();
    }
}