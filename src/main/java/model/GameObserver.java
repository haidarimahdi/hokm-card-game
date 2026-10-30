package model;

public interface GameObserver {
    default void onRoundStarted(Player hakem, int team1Score, int team2Score) {}
    default void onHokmChosen(Player hakem, CardSuit hokm) {}
    default void onTrickStarted(Player leadPlayer, int team1Tricks, int team2Tricks) {}
    default void onPlayerThinking(Player player) {}
    default void onCardPlayed(Player player, Card card, Trick currentTrick) {}
    default void onTrickWon(Player winner, int team1Tricks, int team2Tricks) {}
    default void onRoundEnded(int winningTeam, int team1Score, int team2Score) {}
    default void onMatchEnded(int winningTeam, int team1Score, int team2Score) {}
}