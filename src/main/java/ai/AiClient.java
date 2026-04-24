package ai;

import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.*;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import com.google.cloud.vertexai.generativeai.ResponseHandler;

import java.util.Arrays;

public class AiClient implements AutoCloseable {
    private final VertexAI vertexAI;
    private final GenerativeModel model;

    public AiClient(String projectId, String location, String modelName) {
        this.vertexAI = new VertexAI(projectId, location);

        Schema cardSchema = Schema.newBuilder()
                .setType(Type.OBJECT)
//                .putProperties("reasoning", Schema.newBuilder().setType(Type.STRING).build())
                .putProperties("suit", Schema.newBuilder()
                        .setType(Type.STRING)
                        .addAllEnum(Arrays.asList("HEARTS", "DIAMONDS", "CLUBS", "SPADES"))
                        .build())
                .putProperties("value", Schema.newBuilder().setType(Type.STRING)
                        .addAllEnum(Arrays.asList("TWO", "THREE", "FOUR", "FIVE", "SIX", "SEVEN", "EIGHT", "NINE", "TEN", "JACK", "QUEEN", "KING", "ACE"))
                        .build())
                .addRequired("suit").addRequired("value")
                .build();

        GenerationConfig speedConfig = GenerationConfig.newBuilder()
                .setTemperature(0.2f)
                .setResponseMimeType("application/json")
                .setResponseSchema(cardSchema)
                .build();

        Content systemInstruction = Content.newBuilder()
                .addParts(Part.newBuilder().setText(
                        "You are an expert, Grandmaster-level player in the Persian card game Hokm.\n" +
                                "STRATEGY RULES:\n" +
                                "1. RULE: You MUST follow the lead suit if you have it in your hand.\n" +
                                "2. TEAM RECOGNITION: Pay strict attention to the Team names on the table. If you are on Team 1, your partner is the other Team 1 player. Team 2 players are your opponents. Do not cut or try to beat your own partner's winning card.\n" +
                                "3. PARTNER PLAY: If you are the 3rd or 4th player to act, evaluate if your partner is already winning the trick. If your partner is winning, play your LOWEST valid card to save your high cards.\n" +
                                "4. OPPONENT PLAY: If the opponents are winning the trick, try to beat their highest card by playing a higher card of the lead suit, or by cutting with a Hokm if you are void in the lead suit.\n" +
                                "5. LEADING: If you are leading the trick, play your highest non-Hokm card (like an Ace) if they haven't been played yet, or pull trumps if you hold the highest remaining Hokms." +
                                "Be careful to not lose King cards when you are not sure who holds the Aces unless you have the following ranks in your hand. It is really important to realized what cards your partner hold so you play that." +
                                "For example if your partner is void with a specific non-hokm suit, you can play that particular suit and allow your partner to cut it." +
                                "The key is that you remember what cards are left in the hands of the players and which are already played. To be the master, you should understand from your partner cards whether his or her hand is empty from a specific suit, in which case playing that suit is HIGHLY recommended.\n" +
                                "6. CARD RANKING: The rank of cards from highest to lowest is: ACE, KING, QUEEN, JACK, 10, 9, 8, 7, 6, 5, 4, 3, 2.\n" +
                                "7. CARD COUNTING: Look at the table and memory to know if your Kings and Queens are now the highest remaining cards in their suits.\n\n"
                ).build())
                .build();

        this.model = new GenerativeModel(modelName, vertexAI)
                .withGenerationConfig(speedConfig)
                .withSystemInstruction(systemInstruction);
//                .withTools(Collections.singletonList(buildHokmTools()));

    }

    public String askLlm(String prompt) {
        try {
            GenerateContentResponse response = model.generateContent(prompt);
            return ResponseHandler.getText(response).trim();
        } catch (Exception e) {
            System.err.println("⚠️ Vertex AI Network Error: " + e.getMessage());
            return null;
        }
    }

    @Override
    public void close() {
        if (vertexAI != null) {
            vertexAI.close();
        }
    }

    public GenerativeModel getModel() {
        return model;
    }
}
