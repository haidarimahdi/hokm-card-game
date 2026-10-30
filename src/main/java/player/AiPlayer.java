package player;

import ai.*;
import core.GameEventBus;
import model.*;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AiPlayer extends BasePlayer {
    private final AiClient aiClient;
    private final List<CognitiveEngine> brains;

    /**
     * Constructor for AiPlayer that uses a shared AiMemoryTracker.
     * Accepts a shared AiMemoryTracker registered once on the GameEventBus.
     * @param name                  The name of the player.
     * @param team                  The team of the player.
     * @param aiClient              The AI client to use.
     * @param sharedMemoryTracker   The shared memory tracker.
     */
    public AiPlayer(String name, Team team, AiClient aiClient, AiMemoryTracker sharedMemoryTracker) {
        super(name, team);
        this.aiClient = Objects.requireNonNull(aiClient, "AiClient cannot be null");
        Objects.requireNonNull(sharedMemoryTracker, "AiMemoryTracker cannot be null");
        this.brains = List.of(new System1ReflexEngine(), new System2LlmEngine(aiClient, sharedMemoryTracker));
    }

    /**
     * Constructor for AiPlayer that registers a new AiMemoryTracker with the provided GameEventBus.
     * Legacy convenience constructor for backward compatibility.
     * @param name      The name of the player.
     * @param team      The team of the player.
     * @param aiClient  The AI client to use.
     * @param eventBus  The event bus to register the memory tracker with.
     */
    public AiPlayer(String name, Team team, AiClient aiClient, GameEventBus eventBus) {
        super(name, team);
        this.aiClient = aiClient;

        AiMemoryTracker memoryTracker = new AiMemoryTracker();
        eventBus.register(memoryTracker);

        this.brains = List.of(
                new System1ReflexEngine(),
                new System2LlmEngine(aiClient, memoryTracker)
        );
    }

    @Override
    public CardSuit chooseHokm(List<Card> firstFiveCards) {
        try {
            // 1. Build Prompt
            String prompt = HokmPromptBuilder.buildChooseHokmPrompt(getName(), firstFiveCards);

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

        // 4. Fallback: most frequent suit in first 5 cards
        return firstFiveCards.stream()
                .map(Card::suit)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(firstFiveCards.getFirst().suit());
    }

    @Override
    protected Card selectCard(GameState state, List<Card> validCards) {
        for (CognitiveEngine brain : brains) {
            Card decision = brain.decide(state, validCards, (Player) this);
            if (decision != null) {
                return decision;
            }
        }
        return Collections.min(validCards);
    }
}
