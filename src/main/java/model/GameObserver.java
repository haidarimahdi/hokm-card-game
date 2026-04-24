package model;

import player.Player;

public interface GameObserver {
    void onCardPlayed(Player player, Card card);
    void onTrickWon(Player winner);
    void onHokmChosen(CardSuit hokm);

    default void onMessage(String message) {}
}