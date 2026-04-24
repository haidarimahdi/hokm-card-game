package ai;

import core.RuleEngine;
import model.Card;
import model.GameState;
import model.Trick;
import player.Player;

import java.util.List;

public class SystemReflexEngine implements CognitiveEngine {
    @Override
    public Card decide(GameState state, List<Card> validCards, Player self) {
        if (validCards.size() == 1) {
            return validCards.getFirst();
        }

        if (state.getCurrentTrick() != null && state.getCurrentTrick().getPlayedCards().size() == 3) {
            // If there are 3 cards already played, we can try to win the trick if possible.
            Player currentWinner = RuleEngine.determineTrickWinner(state.getCurrentTrick(), state.getHokm());

            // Partner is winning
            if (currentWinner.getName().contains(getRawTeam(self.getName()))) {
                return getLowestCard(validCards);
            }

            // Can we beat the opponent?
            boolean canBeat = false;
            for (Card c : validCards) {
                model.Trick mockTrick = new Trick();
                state.getCurrentTrick().getPlayedCards().forEach(mockTrick::addCard);
                mockTrick.addCard(self, c);

                if (RuleEngine.determineTrickWinner(mockTrick, state.getHokm()).equals(self)) {
                    canBeat = true;
                    break;
                }
            }

            if (!canBeat) {
                return getLowestCard(validCards);
            }
        }
        // Instinct failed. The situation requires deep thought.
        return null;
    }

    private String getRawTeam(String name) {
        return name.substring(name.indexOf("(") + 1, name.indexOf(")"));
    }

    private Card getLowestCard(List<Card> cards) {
        Card lowest = cards.getFirst();
        for (Card c : cards) {
            if (c.value().ordinal() < lowest.value().ordinal()) {
                lowest = c;
            }
        }
        return lowest;
    }

    private Card getHighestCard(List<Card> validCards) {
        return validCards.stream().max((c1, c2) -> c1.value().compareTo(c2.value())).orElse(null);
    }
}

