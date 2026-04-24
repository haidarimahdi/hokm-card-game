package player;

import ai.*;
import core.RuleEngine;
import model.Card;
import model.CardSuit;
import model.GameObserver;
import model.GameState;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AiPlayer implements Player, GameObserver {
    private final String name;
//    private final String team;
    private final List<Card> hand = new ArrayList<>();
    private final StringBuilder privateMemory = new StringBuilder();
    private final AiClient aiClient;
    private final List<CognitiveEngine> brains = new ArrayList<>();

    public AiPlayer(String name, AiClient aiClient) {
        this.name = name;
        this.aiClient = aiClient;
        this.brains.add(new SystemReflexEngine());
        this.brains.add(new System2LlmEngine(aiClient, privateMemory.toString()));
//        this.team = name.substring(name.indexOf("(") + 1, name.indexOf(")"));
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void receiveCards(List<Card> cards) {
        hand.addAll(cards);
    }

    @Override
    public CardSuit chooseHokm(List<Card> firstFiveCards) {
        System.out.println("🤖 " + name + " (Hakem) is analyzing their first 5 cards to choose the Hokm...");
        System.out.println("   [Cards]: " + firstFiveCards.toString());

        try {
            // 1. Build Prompt
            String prompt = HokmPromptBuilder.buildChooseHokmPrompt(name, firstFiveCards);

            System.out.println("DEBUG PROMPT: " + prompt);

            // 2. Fetch Decision
            String jsonResponse = aiClient.askLlm(prompt);

            // 3. Parse Decision
            CardSuit chosenHokm = AiResponseParser.parseHokmDecision(jsonResponse);

            if (chosenHokm != null) {
                return chosenHokm;
            } else {
                System.out.println("⚠️ Agent failed to pick a valid Hokm suit.");
            }

        } catch (Exception e) {
            System.err.println("🚨 API execution error while choosing Hokm: " + e.getMessage());
        }

        // 4. Smart Fallback: If AI fails, pick the suit we have the most of
        return fallbackHokm(firstFiveCards);
    }

    private CardSuit fallbackHokm(List<Card> firstFiveCards) {
        System.out.println("🛡️ " + name + " fallback triggered: Automatically picking the most frequent suit.");

        java.util.Map<CardSuit, Integer> suitCounts = new java.util.HashMap<>();
        for (Card c : firstFiveCards) {
            suitCounts.put(c.suit(), suitCounts.getOrDefault(c.suit(), 0) + 1);
        }

        // Find the suit with the highest count
        CardSuit bestSuit = firstFiveCards.getFirst().suit();
        int maxCount = 0;
        for (java.util.Map.Entry<CardSuit, Integer> entry : suitCounts.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                bestSuit = entry.getKey();
            }
        }

        return bestSuit;
    }

    @Override
    public Card playCard(GameState currentState) {
        CardSuit leadSuit = currentState.getCurrentTrick() != null ? currentState.getCurrentTrick().getLeadSuit() : null;

        List<Card> validCards = RuleEngine.getValidMoves(hand, leadSuit);

        System.out.println("🤖 " + name + " is evaluating the board...");

        for (CognitiveEngine brain : brains) {
            Card decision = brain.decide(currentState, validCards, this);

            if (decision != null) {
                hand.remove(decision);
                return decision;
            }
        }

        System.out.println("🛡️ " + name + " experienced total cognitive failure. Playing lowest valid card.");
        Card safeCard = validCards.getLast();
        hand.remove(safeCard);
        return safeCard;

//        System.out.print("Current Trick: ");
//        for (Card currentTrickCards : currentState.getCurrentTrick().getPlayedCards().values()) {
//            System.out.print("[" + currentTrickCards.toString() + "] ");
//        }
//        System.out.println();
//        System.out.println("🤖 " + name + " is thinking...");
//
//        try {
//            String prompt = HokmPromptBuilder.buildTurnPrompt(name, team, currentState, validCards, privateMemory.toString());
//
//            System.out.println("DEBUG PROMPT: " + prompt);
//            String jsonResponse = aiClient.askLlm(prompt);
//
//            Card chosenCard = AiResponseParser.parseDecision(jsonResponse, validCards);
//            if (chosenCard != null) {
//                hand.remove(chosenCard);
//                return chosenCard;
//            } else {
//                System.out.println("⚠️ Agent's response was invalid or the card was not in the valid moves.");
//            }
//        } catch (Exception e) {
//            System.err.println("🚨 API execution error: " + e.getMessage());
//        }
//
//        return fallback(validCards);
    }

    /**
     * The ultimate safety net. If the LLM completely fails, this plays a valid card automatically.
     */
    private Card fallback(List<Card> validCards) {
        System.out.println("🛡️ " + name + " fallback triggered: Playing the lowest valid card to keep the game moving.");

        // Grab the last valid card (usually the lowest card if sorted, or just a random valid one)
        Card safeCard = validCards.getLast();

        // Don't forget to actually remove it from the hand!
        hand.remove(safeCard);

        return safeCard;
    }


    @Override
    public List<Card> getHand() {
        return hand;
    }

    @Override
    public void onCardPlayed(Player player, Card card) {
        privateMemory.append("- ").append(player.getName()).append(" played ").append(card).append("\n");
    }

    @Override
    public void onTrickWon(Player winner) {
        privateMemory.append(">>> ").append(winner.getName()).append(" won that trick.\n\n");

    }

    @Override
    public void onHokmChosen(CardSuit hokm) {
        privateMemory.append(">>> The Hokm was set to ").append(hokm).append(".\n");
    }

    @Override
    public void resetMemory() {
        this.privateMemory.setLength(0);
    }
}
