package ai;

import model.Card;
import model.GameState;
import player.Player;

import java.util.List;

public class System2LlmEngine implements CognitiveEngine {
    private final AiClient aiClient;
    private final String memory;

    public System2LlmEngine(AiClient aiClient, String memory) {
        this.aiClient = aiClient;
        this.memory = memory;
    }

    @Override
    public Card decide(GameState state, List<Card> validCards, Player self) {
        System.out.println("🧠 [System 2]: Activating Deep LLM Reasoning...");

        String team = self.getName().substring(self.getName().indexOf("(") + 1, self.getName().indexOf(")"));

        try {
            String prompt = HokmPromptBuilder.buildTurnPrompt(self.getName(), team, state, validCards, memory);

            String jsonResponse = aiClient.askLlm(prompt);
            return AiResponseParser.parseDecision(jsonResponse, validCards);
        } catch (Exception e) {
            System.out.println("⚠️ System 2 LLM Engine failed to produce a valid decision: " + e.getMessage());
            return null;
        }
    }
}
