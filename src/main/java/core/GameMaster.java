package core;

import model.*;

import java.util.List;
import java.util.Objects;

public class GameMaster {
    private static final int TRICKS_TO_WIN_ROUND = 7;
    private static final int POINTS_TO_WIN_MATCH = 3;

    private final List<Player> players;
    private final GameState gameState;
    private final Dealer dealer;
    private final TrickEngine trickEngine;
    private final GameObserver observer;

    public GameMaster(List<Player> players, GameState gameState, GameController gameController, GameObserver observer) {
        if (players == null || players.size() != 4) {
            throw new IllegalArgumentException("Exactly 4 players are required.");
        }
        this.players = List.copyOf(players);
        this.gameState = Objects.requireNonNull(gameState, "GameState cannot be null.");
        this.observer = Objects.requireNonNull(observer, "GameObserver cannot be null.");
        this.dealer = new Dealer(observer);
        this.trickEngine = new TrickEngine(players, gameController, observer);
    }

    public void playGame() {
        Player currentHakem = dealer.determineInitialHakem(players);
        int currentHakemIndex = players.indexOf(currentHakem);

        while (gameState.getTeam1Score() < POINTS_TO_WIN_MATCH && gameState.getTeam2Score() < POINTS_TO_WIN_MATCH) {
            observer.onRoundStarted(currentHakem, gameState.getTeam1Score(), gameState.getTeam2Score());

            dealer.dealCardsAndSetHokm(players, currentHakem, gameState);

            Team roundWinner = playRound(currentHakem);
            updateScoreAndNotify(roundWinner, currentHakem.getTeam());

            if (roundWinner != currentHakem.getTeam()) {
                currentHakemIndex = (currentHakemIndex + 1) % players.size();
                currentHakem = players.get(currentHakemIndex);
            }

            gameState.resetRound();
        }

        int winningTeamNum = gameState.getTeam1Score() >= POINTS_TO_WIN_MATCH ? 1 : 2;
        observer.onMatchEnded(winningTeamNum, gameState.getTeam1Score(), gameState.getTeam2Score());
    }

    /**
     * Plays a round of the game until one team wins 7 tricks.
     *
     * @param startingLead The player who leads the first trick of the round.
     * @return The team that won the round.
     */
    private Team playRound(Player startingLead) {
        Player currentLead = startingLead;

        while (gameState.getTeam1Tricks() < TRICKS_TO_WIN_ROUND && gameState.getTeam2Tricks() < TRICKS_TO_WIN_ROUND) {
            currentLead = trickEngine.playTrick(gameState, currentLead); // The winner of the trick leads the next one
        }

        return gameState.getTeam1Tricks() == TRICKS_TO_WIN_ROUND ? Team.TEAM_A : Team.TEAM_B;
    }

    private void updateScoreAndNotify(Team roundWinner, Team hakemTeam) {
        int losingTeamTricks = (roundWinner == Team.TEAM_A)
                ? gameState.getTeam2Tricks()
                : gameState.getTeam1Tricks();
        int pointsAwarded = 1;
        if (losingTeamTricks == 0) {
            pointsAwarded = (roundWinner == hakemTeam) ? 2 : 3;
        }

        int winningTeamNum;
        if (roundWinner == Team.TEAM_A) {
            gameState.addPointsToTeam1(pointsAwarded);
            winningTeamNum = 1;
        } else {
            gameState.addPointsToTeam2(pointsAwarded);
            winningTeamNum = 2;
        }

        observer.onRoundEnded(winningTeamNum, gameState.getTeam1Score(), gameState.getTeam2Score());
    }
}
