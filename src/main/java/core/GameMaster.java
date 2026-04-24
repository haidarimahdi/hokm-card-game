package core;

import model.*;
import player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GameMaster {
    private final List<Player> players;
    private final GameState gameState;

    private final Dealer dealer;
    private final TrickEngine trickEngine;

    private final GameController gameController;

    public GameMaster(List<Player> players, GameState gameState, GameController gameController) {
        this.players = players;
        this.gameState = gameState;

        this.dealer = new Dealer();
        this.trickEngine = new TrickEngine(players, gameController);
        this.gameController = gameController;
    }

    public void playGame() {
        broadcast("Determining the initial Hakem...");
        HakemDetermine hakemDetermine = new HakemDetermine(players);
        Player currentHakem = hakemDetermine.determineHakem();

        int currentHakemIndex = players.indexOf(currentHakem);
        while (gameState.getTeam1Score() < 3 && gameState.getTeam2Score() < 3) {
            System.out.println("\n==================================");
            broadcast("SCORE: Team 1 [" + gameState.getTeam1Score() + "] - Team 2 [" + gameState.getTeam2Score() + "]");
            System.out.println("==================================");

            for (Player p : players) {
                p.resetMemory();
            }

            broadcast("The Hakem for this round is: " + currentHakem.getName());

            dealer.dealCardsAndSetHokm(players, currentHakem, gameState);
            // 1. Deal cards and set Hokm
//            dealAndSetHokm(hakem);

            // 2. Play the round
            boolean team1WonRound = playRound(currentHakem);

            // 3. Evaluate points and determine next Hakem
            boolean isHakemTeam1 = isTeam1(currentHakem);

            if (team1WonRound) {
                gameState.addPointsToTeam1(1);
                broadcast("🏆 Team 1 reaches 7 trick points and wins the round! (+1 Score)");

                if (!isHakemTeam1) {
                    currentHakemIndex = (currentHakemIndex + 1) % 4; // Pass Hakem to the right
                }
            } else {
                gameState.addPointsToTeam2(1);
                broadcast("🏆 Team 2 reaches 7 trick points and wins the round! (+1 Score)");
                if (isHakemTeam1) {
                    currentHakemIndex = (currentHakemIndex + 1) % 4; // Pass Hakem to the right
                }
            }

            gameState.resetRound();
        }

        broadcast("\n🎉 MATCH OVER 🎉");
        broadcast("FINAL SCORE -> Team 1: " + gameState.getTeam1Score() + " | Team 2: " + gameState.getTeam2Score());
        if (gameState.getTeam1Score() >= 3) {
            broadcast("🏅 Team 1 Wins the Match!");
        } else {
            broadcast("🏅 Team 2 Wins the Match!");
        }
    }

    private boolean playRound(Player startingLead) {
        Player currentLead = startingLead;

        while (gameState.getTeam1Tricks() < 7 && gameState.getTeam2Tricks() < 7) {
            broadcast("\nTrick Points -> Team 1: " + gameState.getTeam1Tricks() + " | Team 2: " + gameState.getTeam2Tricks());

            Player trickWinner = trickEngine.playTrick(gameState, currentLead);

            if (isTeam1(trickWinner)) {
                gameState.addTrickToTeam1();
//                broadcast("+1 Trick Point to Team 1");
            } else {
                gameState.addTrickToTeam2();
//                broadcast("+1 Trick Point to Team 2");
            }

            currentLead = trickWinner; // The winner of the trick leads the next one
        }

        return gameState.getTeam1Tricks() == 7;
    }

    // Helper to identify teams (Player 0 and 2 are Team 1)
    private boolean isTeam1(Player p) {
        int index = players.indexOf(p);
        return index == 0 || index == 2;
    }

    private void broadcast(String message) {
        System.out.println(message);
        for (Player player : players) {
            if (player instanceof GameObserver observer) {
                observer.onMessage(message);
            }
        }
    }
}
