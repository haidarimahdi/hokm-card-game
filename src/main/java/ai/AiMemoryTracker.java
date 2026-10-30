package ai;

import model.*;

import java.util.*;

public class AiMemoryTracker implements GameObserver {
    private final Set<Card> unseenCards = new HashSet<>();
    private final Map<String, Set<CardSuit>> knownVoids = new HashMap<>();

    public AiMemoryTracker() {
        resetMemory();
    }

    @Override
    public void onRoundStarted(Player hakem, int t1Score, int t2Score) {
        resetMemory();
    }

    @Override
    public void onCardPlayed(Player player, Card card, Trick currentTrick) {
        unseenCards.remove(card);
        CardSuit leadSuit = currentTrick.getLeadSuit();
        if (leadSuit != null && card.suit() != leadSuit) {
            knownVoids.computeIfAbsent(player.getName(), k -> EnumSet.noneOf(CardSuit.class))
                    .add(leadSuit);
        }
    }

    public void resetMemory() {
        unseenCards.clear();
        knownVoids.clear();
        for (CardSuit suit : CardSuit.values()) {
            for (CardValue value : CardValue.values()) {
                unseenCards.add(new Card(suit, value));
            }
        }
    }

    public String calculateThreatBoard(List<Card> myHand) {
        StringBuilder sb = new StringBuilder();
        for (CardSuit suit : CardSuit.values()) {
            List<Card> remainingInSuit = unseenCards.stream()
                    .filter(c -> c.suit() == suit && !myHand.contains(c))
                    .toList();

            if (!remainingInSuit.isEmpty()) {
                Card highestUnseen = Collections.max(remainingInSuit);
                sb.append("- ").append(suit).append(": ")
                        .append(highestUnseen.value()).append(" is the highest remaining (")
                        .append(remainingInSuit.size()).append(" cards left)");
                if (remainingInSuit.size() <= 4) {
                    sb.append(" -> WARNING: Suit exhausted! High probability someone is VOID and will cut.");
                }
                sb.append("\n");
            } else {
                sb.append("- ").append(suit).append(": ALL CARDS PLAYED.\n");
            }
        }
        return sb.toString();
    }

    public String getKnownVoidsString() {
        return knownVoids.isEmpty() ? "No known voids yet." : knownVoids.toString();
    }
}
