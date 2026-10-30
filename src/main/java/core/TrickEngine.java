package core;

import model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Responsible for orchestrating a single Trick consisting of 4 turns.
 */
public class TrickEngine {
    private final List<Player> players;
    private final GameController gameController;
    private final GameObserver observer;

    public TrickEngine(List<Player> players, GameController gameController, GameObserver observer) {
        if (players == null || players.size() != 4) {
            throw new IllegalArgumentException("TrickEngine requires exactly 4 players.");
        }
        this.players = List.copyOf(players);
        this.gameController = gameController;
        this.observer = Objects.requireNonNull(observer, "GameObserver cannot be null");
    }

    public Player playTrick(GameState gameState, Player leadPlayer) {
        Objects.requireNonNull(gameState, "GameState cannot be null");
        Objects.requireNonNull(leadPlayer, "Lead player cannot be null");

        int leadPlayerIndex = players.indexOf(leadPlayer);
        if (leadPlayerIndex == -1) {
            throw new IllegalArgumentException("Lead player is not part of this match: " + leadPlayer.getName());
        }
        observer.onTrickStarted(leadPlayer, gameState.getTeam1Tricks(), gameState.getTeam2Tricks());

        Trick trick = new Trick();
        gameState.setCurrentTrick(trick);

        // Run the 4 turns in order
        for (int i = 0; i < players.size(); i++) {
            if (gameController != null && gameController.isAbortRequested()) {
                throw new GameAbortedException("Game aborted by user.");
            }

            Player currentPlayer = players.get((leadPlayerIndex + i) % players.size());
            observer.onPlayerThinking(currentPlayer);

            // Ask player for a card
            Card playedCard = currentPlayer.playCard(gameState);

            // Register card to the trick
            trick.addCard(currentPlayer, playedCard);
            observer.onCardPlayed(currentPlayer, playedCard, trick);
        }

        // Determine Winner using RuleEngine
        Player winner = RuleEngine.determineTrickWinner(trick, gameState.getHokm());

        if (winner.getTeam() == Team.TEAM_A) {
            gameState.addTrickToTeam1();
        } else {
            gameState.addTrickToTeam2();
        }

        // Update GameState History
        gameState.addCardToHistory(new ArrayList<>(trick.getPlayedCards().values()));
        gameState.setCurrentTrick(null);

        // Notify Observers
        observer.onTrickWon(winner, gameState.getTeam1Tricks(), gameState.getTeam2Tricks());

        return winner;
    }
}