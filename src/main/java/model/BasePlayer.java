package model;

import com.sun.source.tree.Tree;
import core.RuleEngine;

import java.util.*;

public abstract class BasePlayer implements Player {
    private static final Comparator<Card> HAND_SORT_ORDER = Comparator.comparing(Card::suit)
            .thenComparing(Card::value, Comparator.reverseOrder());

    private final String name;
    private final Team team;
    private final NavigableSet<Card> hand = new TreeSet<>(HAND_SORT_ORDER);

    protected BasePlayer(String name, Team team) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Player name cannot be null or empty.");
        }
        if (team == null) {
            throw new IllegalArgumentException("Team cannot be null.");
        }

        this.name = name + " (" + team.getDisplayName() + ")";
        this.team = team;
    }

    @Override
    public final String getName() {
        return name;
    }

    @Override
    public final Team getTeam() {
        return team;
    }

    @Override
    public final List<Card> getHand() {
        return List.copyOf(hand);
    }

    @Override
    public final void receiveCards(List<Card> cards) {
        Objects.requireNonNull(cards, "Cards cannot be null.");
        hand.addAll(cards);
    }

    @Override
    public final void clearHand() {
        hand.clear();
    }

    @Override
    public final Card playCard(GameState state) {
        Objects.requireNonNull(state, "Game state cannot be null.");

        CardSuit leadSuit = state.getCurrentTrick() != null ? state.getCurrentTrick().getLeadSuit() : null;

        List<Card> currentHand = getHand();
        List<Card> validCards = RuleEngine.getValidMoves(currentHand, leadSuit);

        Card chosenCard = selectCard(state, validCards);
        if (chosenCard == null || !validCards.contains(chosenCard)) {
            System.err.println("⚠️ Illegal or null move by " + name + " (" + chosenCard + "). Forcing first valid card.");
            chosenCard = validCards.getFirst();
        }

        hand.remove(chosenCard);
        return chosenCard;
    }

    @Override
    public abstract CardSuit chooseHokm(List<Card> firstFiveCards);

    protected abstract Card selectCard(GameState state, List<Card> validCards);
}
