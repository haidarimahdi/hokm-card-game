package ai;

import model.Card;
import model.GameState;
import model.Player;

import java.util.List;

public class System2LlmEngine implements CognitiveEngine {
    private final AiClient aiClient;
    private final AiMemoryTracker memoryTracker;

    public System2LlmEngine(AiClient aiClient, AiMemoryTracker memoryTracker) {
        this.aiClient = aiClient;
        this.memoryTracker = memoryTracker;
    }

    @Override
    public Card decide(GameState state, List<Card> validCards, Player self) {
        try {
            String prompt = HokmPromptBuilder.buildTurnPrompt(
                    self,
                    state,
                    validCards,
                    memoryTracker
            );

            String jsonResponse = aiClient.askLlm(prompt);
            return AiResponseParser.parseDecision(jsonResponse, validCards);
        } catch (Exception e) {
            System.out.println("⚠️ System 2 LLM Engine failed to produce a valid decision: " + e.getMessage());
            return null;
        }
    }
}
