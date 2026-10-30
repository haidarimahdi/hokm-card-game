package ai;

import core.RuleEngine;
import model.*;

import java.util.Comparator;
import java.util.List;

public class System1ReflexEngine implements CognitiveEngine {
    private static final Comparator<Card> BY_VALUE_ASC = Comparator.comparingInt(Card::rank);

    @Override
    public Card decide(GameState state, List<Card> validCards, Player self) {
        if (validCards.size() == 1) {
            return validCards.getFirst();
        }

        Trick currentTrick = state.getCurrentTrick();
        // 3. Instinct: 3rd Player "Let it Ride" Rule
        if (currentTrick == null || currentTrick.isEmpty()) {
            return null; // No instinctive play for the first player of a trick
        }
        int cardsOnTable = currentTrick.getPlayedCards().size();
        if (cardsOnTable < 2) {
            return null; // Not enough information to make a reflex decision
        }

        Player currentWinner = RuleEngine.determineTrickWinner(state.getCurrentTrick(), state.getHokm());
        boolean partnerIsWinning = currentWinner.getTeam() == self.getTeam();

            // If my partner is winning the trick right now...
            if (partnerIsWinning) {
                return handlePartnerWinning(state, currentTrick, validCards);
            }
            if (cardsOnTable == 3) {
                return handleFourthPlayerOpponentWinning(state, currentTrick, validCards, self);
            }
        return null;
    }

    private Card handlePartnerWinning(GameState state, Trick currentTrick, List<Card> validCards) {
        boolean followingSuit = validCards.getFirst().suit() == currentTrick.getLeadSuit();

        if (followingSuit) {
            // Play off-trump Ace if held to preserve suit control; otherwise play lowest card
            Card offTrumpAce = findOffTrumpAce(validCards, currentTrick.getLeadSuit(), state.getHokm());
            if (offTrumpAce != null) {
                return offTrumpAce;
            }
            return getLowestValueCard(validCards);
        }

        // Void in lead suit: never cut partner! Discard lowest non-Hokm card if possible
        List<Card> nonHokms = validCards.stream()
                .filter(c -> c.suit() != state.getHokm())
                .toList();

        if (!nonHokms.isEmpty()) {
            return getLowestValueCard(nonHokms);
        }

        // Forced to play Hokm (hand has only Hokms left)
        return getLowestValueCard(validCards);
    }
    private Card handleFourthPlayerOpponentWinning(
            GameState state, Trick currentTrick, List<Card> validCards, Player self) {

        List<Card> winningOptions = validCards.stream()
                .filter(candidate -> wouldWinTrick(currentTrick, state.getHokm(), self, candidate))
                .toList();

        if (!winningOptions.isEmpty()) {
            // Win as cheaply as possible (prefer non-Hokm winners before Hokm cuts if tied in rank)
            return winningOptions.stream()
                    .min(Comparator.comparing((Card c) -> c.suit() == state.getHokm())
                            .thenComparingInt(Card::rank))
                    .orElseThrow();
        }

        // Cannot beat opponent: surrender with lowest value card (preferring non-Hokm)
        List<Card> nonHokms = validCards.stream()
                .filter(c -> c.suit() != state.getHokm())
                .toList();

        return getLowestValueCard(nonHokms.isEmpty() ? validCards : nonHokms);
    }

    private boolean wouldWinTrick(Trick currentTrick, CardSuit hokm, Player self, Card candidate) {
        Trick simulated = new Trick();
        currentTrick.getPlayedCards().forEach(simulated::addCard);
        simulated.addCard(self, candidate);
        return RuleEngine.determineTrickWinner(simulated, hokm) == self;
    }

    private Card findOffTrumpAce(List<Card> validCards, CardSuit leadSuit, CardSuit hokm) {
        if (leadSuit == hokm) return null;
        return validCards.stream()
                .filter(c -> c.suit() == leadSuit && c.value() == CardValue.ACE)
                .findFirst()
                .orElse(null);
    }

    private Card getLowestValueCard(List<Card> cards) {
        return cards.stream().min(BY_VALUE_ASC).orElseThrow();
    }
}
