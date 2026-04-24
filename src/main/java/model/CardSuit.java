package model;
public enum CardSuit {
    SPADES, HEARTS, CLUBS, DIAMONDS;

    public String getSymbol() {
        return switch (this) {
            case SPADES -> "♠️";
            case HEARTS -> "♥️";
            case CLUBS -> "♣️";
            case DIAMONDS -> "♦️";
        };
    }
}