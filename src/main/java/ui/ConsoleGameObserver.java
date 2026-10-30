package ui;

import model.Card;
import model.CardSuit;
import model.GameObserver;
import model.Trick;
import model.Player;

import java.util.stream.Collectors;

public class ConsoleGameObserver implements GameObserver {

    @Override
    public void onRoundStarted(Player hakem, int t1Score, int t2Score) {
        System.out.println("\n==================================");
        System.out.printf("SCORE: Team 1 [%d] - Team 2 [%d]%n", t1Score, t2Score);
        System.out.println("==================================");
        System.out.println("The Hakem for this round is: " + hakem.getName());
    }

    @Override
    public void onHokmChosen(Player hakem, CardSuit hokm) {
        System.out.printf(">> %s set the Hokm to %s %s%n", hakem.getName(), hokm.getSymbol(), hokm.name());
    }

    @Override
    public void onTrickStarted(Player leadPlayer, int t1Tricks, int t2Tricks) {
        System.out.println("\n------------------------------------");
        System.out.printf("Trick Points -> Team 1: %d | Team 2: %d%n", t1Tricks, t2Tricks);
        System.out.println("Starting new Trick! " + leadPlayer.getName() + " leads.");
    }

    @Override
    public void onPlayerThinking(Player player) {
        if (player.getTeam() != null && !player.getName().contains("You")) {
            System.out.println("⏳ " + player.getName() + " is thinking...");
        }
    }

    @Override
    public void onCardPlayed(Player player, Card card, Trick currentTrick) {
        String tableCards = currentTrick.getPlayedCards().values().stream()
                .map(Card::toVisualString)
                .collect(Collectors.joining(" | ", "{ ", " }"));
        System.out.println(">> " + player.getName() + " played " + card.toVisualString() + "  --> Table: " + tableCards);
    }

    @Override
    public void onTrickWon(Player winner, int t1Tricks, int t2Tricks) {
        System.out.println("🏆 " + winner.getName() + " won the trick!");
    }

    @Override
    public void onRoundEnded(int winningTeam, int t1Score, int t2Score) {
        System.out.println("\n🎉 Team " + winningTeam + " reaches 7 trick points and wins the round! (+1 Score)");
    }

    @Override
    public void onMatchEnded(int winningTeam, int t1Score, int t2Score) {
        System.out.println("\n🏁 MATCH OVER 🏁");
        System.out.printf("FINAL SCORE -> Team 1: %d | Team 2: %d%n", t1Score, t2Score);
        System.out.println("🏆 Team " + winningTeam + " Wins the Match!");
    }

}
