package ai;

import model.*;

import java.util.*;

public class AiMemoryTracker implements GameObserver {
    // Backed by 64-bit bit-vectors for O(1) operations
    private final Map<CardSuit, EnumSet<CardValue>> unseenBySuit = new EnumMap<>(CardSuit.class);
    private final Map<String, EnumSet<CardSuit>> knownVoids = new HashMap<>();

    public AiMemoryTracker() {
        resetMemory();
    }

    @Override
    public void onRoundStarted(Player hakem, int t1Score, int t2Score) {
        resetMemory();
    }

    @Override
    public void onCardPlayed(Player player, Card card, Trick currentTrick) {
//        unseenCards.remove(card);
//        CardSuit leadSuit = currentTrick.getLeadSuit();
        EnumSet<CardValue> suitSet = unseenBySuit.get(card.suit());
        if (suitSet != null) {
            suitSet.remove(card.value());
        }

        CardSuit leadSuit = currentTrick.getLeadSuit();
        if (leadSuit != null && card.suit() != leadSuit) {
            knownVoids.computeIfAbsent(player.getName(), k -> EnumSet.noneOf(CardSuit.class))
                    .add(leadSuit);
        }
    }

    public void resetMemory() {
        knownVoids.clear();
        for (CardSuit suit : CardSuit.values()) {
            unseenBySuit.put(suit, EnumSet.allOf(CardValue.class));
        }
    }

    public String calculateThreatBoard(List<Card> myHand) {
        Map<CardSuit, EnumSet<CardValue>> handBySuit = new EnumMap<>(CardSuit.class);
        for (Card card : myHand) {
            handBySuit.computeIfAbsent(card.suit(), k -> EnumSet.noneOf(CardValue.class)).add(card.value());
        }

        StringBuilder sb = new StringBuilder();
        for (CardSuit suit : CardSuit.values()) {
            EnumSet<CardValue> remainingInSuit = EnumSet.copyOf(unseenBySuit.get(suit));
            EnumSet<CardValue> mySuitCards = handBySuit.get(suit);
            if (mySuitCards != null) {
                remainingInSuit.removeAll(mySuitCards);
            }

            if (!remainingInSuit.isEmpty()) {
                CardValue highestUnseen = Collections.max(remainingInSuit);
                sb.append("- ").append(suit).append(": ")
                        .append(highestUnseen).append(" is the highest remaining (")
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
