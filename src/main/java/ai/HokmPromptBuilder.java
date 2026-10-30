package ai;

import core.RuleEngine;
import model.*;
import model.Player;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HokmPromptBuilder {

    public static String buildTurnPrompt(Player self, GameState currentState, List<Card> validCards, AiMemoryTracker memoryTracker) {
        Trick trick = currentState.getCurrentTrick();
        String trickStatus = formatTrickStatus(trick, currentState.getHokm(), self.getTeam());
        String fullHandBySuit = formatCardsBySuit(self.getHand());
        String validMovesList = validCards.stream().map(Card::toString).collect(Collectors.joining(","));

        return String.format("""
                        === PLAYER & MATCH CONTEXT ===
                        IDENTITY: %s | TEAM: %s
                        ROUND TRICK SCORE: Team 1 [%d/7] - Team 2 [%d/7]
                        HOKM (TRUMP SUIT): %s
                        
                        === CURRENT TRICK ON TABLE ===
                        %s
                        
                        === OPPONENT & PARTNER INTELLIGENCE ===
                        UNSEEN CARDS OUTSIDE YOUR HAND (Threat Board):
                        %s
                        KNOWN PLAYER VOIDS (Cannot follow these suits):
                        %s
                        
                        === YOUR CARDS ===
                        FULL HAND:
                        %s
                        
                        VALID MOVES (You MUST choose one of these):
                        [%s]
                        """,
                self.getName(),
                self.getTeam().getDisplayName(),
                currentState.getTeam1Tricks(),
                currentState.getTeam2Tricks(),
                currentState.getHokm(),
                trickStatus,
                memoryTracker.calculateThreatBoard(self.getHand()),
                memoryTracker.getKnownVoidsString(),
                fullHandBySuit,
                validMovesList
        );
    }

    public static String buildChooseHokmPrompt(String name, List<model.Card> firstFiveCards) {
        return String.format("""
                IDENTITY: %s (Hakem)
                TASK: Declare the Hokm (Trump suit) based on your first 5 dealt cards.
                
                YOUR FIRST 5 CARDS:
                %s
                
                RULES:
                1. Prefer suits with the most cards (quantity over a lone Ace).
                2. Keep lone Aces as off-suit winners; build trumps from multi-card suits.
                Return your chosen 'suit' in JSON.
                """,
                name,
                formatCardsBySuit(firstFiveCards)
        );
    }

    private static String formatTrickStatus(Trick trick, CardSuit hokm, Team myTeam) {
        if (trick == null || trick.isEmpty()) {
            return "Table is empty. YOU ARE LEADING THIS TRICK.";
        }

        String playedList = trick.getPlayedCards().entrySet().stream()
                .map(e -> e.getKey().getName() + " played " + e.getValue())
                .collect(Collectors.joining(" -> "));

        Player currentWinner = RuleEngine.determineTrickWinner(trick, hokm);
        String winnerRelation = (currentWinner.getTeam() == myTeam) ? "YOUR PARTNER" : "OPPONENT";

        return String.format(
                "Lead Suit: %s\nPlayed So Far: %s\nCurrently Winning: %s (%s)",
                trick.getLeadSuit(),
                playedList,
                currentWinner.getName(),
                winnerRelation
        );
    }

    private static String formatCardsBySuit(List<Card> cards) {
        Map<CardSuit, List<CardValue>> grouped = cards.stream()
                .collect(Collectors.groupingBy(Card::suit,
                        Collectors.mapping(Card::value, Collectors.toList())
                ));
        StringBuilder sb = new StringBuilder();
        for (CardSuit suit : CardSuit.values()) {
            List<CardValue> values = grouped.getOrDefault(suit, List.of());
            sb.append("- ").append(suit).append(" (").append(values.size()).append("): ")
                    .append(values.isEmpty() ? "VOID" : values).append("\n");
        }
        return sb.toString().trim();
    }
}