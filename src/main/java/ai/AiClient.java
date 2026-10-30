package ai;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.*;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import com.google.cloud.vertexai.generativeai.ResponseHandler;
import model.CardSuit;
import model.CardValue;

import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class AiClient implements AutoCloseable {
    private static final String SYSTEM_INSTRUCTION = """
            You are an expert, Grandmaster-level player in the Persian card game Hokm.
            === CORE RULES & RANKING ===
            1. LEGAL MOVES: You MUST choose a card from the provided VALID MOVES list (following the lead suit whenever possible).
            2. CARD RANKING (High to Low): ACE, KING, QUEEN, JACK, TEN, NINE, EIGHT, SEVEN, SIX, FIVE, FOUR, THREE, TWO.
            3. ALLIANCE: Never cut or try to beat a trick when the prompt indicates YOUR PARTNER is currently winning, except as noted in Rule 2 below.

            === GRANDMASTER TURN STRATEGIES ===
            1. THE PARTNER PASS (FINESSING): When leading a trick holding a King or Queen, check the Threat Board. If higher cards (like the Ace) are still outside your hand, DO NOT lead your King/Queen. Lead a LOW card of that suit to force out the opponents' Ace or let your partner win it.
            2. PARTNER WINNING EXCEPTION: If your partner is currently winning and you must follow a non-Hokm lead suit, check if you hold the ACE of that led suit. If you do, play the ACE to secure the guaranteed trick, maintain suit control, and prevent the suit from being cut by a Hokm on a later trick. Otherwise, play your lowest valid card.
            3. EXHAUSTION AVOIDANCE: If the Threat Board shows "WARNING: Suit exhausted!" for a non-Hokm suit, avoid leading it unless you want to force opponents to spend Hokms or know your partner is void and can cut it.
            4. PARTNER FEEDING: Check "Known Player Voids". If your partner is void in a non-Hokm suit, lead a low card of that suit so your partner can cut it with a Hokm.
            5. OPPONENT STARVATION: Never lead a non-Hokm suit in which an opponent is known to be void.
            6. HOKM CONSERVATION & DEFENSE: Do not play a Hokm unless cutting an opponent's winning card, pulling trumps as the Hakem team, or leading an unbeatable Hokm. If you are the defending team, save your high Hokms to cut the Hakem's off-suit winners.
            7. OVER-CUT VALUATION: If an opponent has already cut a trick with a low Hokm, do NOT over-cut them with a premium Hokm (Ace, King, Queen) unless it is the decisive final tricks of the round (e.g., either team has 5 or 6 tricks). Discard a low off-suit card instead.

            === HOKM (TRUMP) SELECTION STRATEGIES (WHEN HAKEM) ===
            1. QUANTITY OVER LONE ACES: Prioritize the suit with the highest card count in your first 5 cards.
            2. PRESERVE OFF-SUIT ACES: Do not pick a suit solely because you hold a lone Ace; a non-Hokm Ace is already a guaranteed external trick. Build trumps from multi-card suits.
            """;

    private final VertexAI vertexAI;
    private final GenerativeModel model;

    public AiClient(String projectId, String location, String modelName) {
        this.vertexAI = new VertexAI.Builder()
                .setProjectId(projectId)
                .setLocation(location)
                .setCredentials(loadCredentials())
                .build();

        List<String> suits = Arrays.stream(CardSuit.values()).map(Enum::name).toList();
        List<String> values = Arrays.stream(CardValue.values()).map(Enum::name).toList();

        Schema responseSchema = Schema.newBuilder()
                .setType(Type.OBJECT)
                .putProperties("suit", Schema.newBuilder()
                        .setType(Type.STRING)
                        .addAllEnum(suits)
                        .build())
                .putProperties("value", Schema.newBuilder().setType(Type.STRING)
                        .addAllEnum(values)
                        .build())
                .addRequired("suit")
                .build();

        GenerationConfig generationConfig = GenerationConfig.newBuilder()
                .setTemperature(0.2f)
                .setResponseMimeType("application/json")
                .setResponseSchema(responseSchema)
                .build();

        Content systemInstruction = Content.newBuilder()
                .addParts(Part.newBuilder().setText(SYSTEM_INSTRUCTION).build())
                .build();

        this.model = new GenerativeModel(modelName, vertexAI)
                .withGenerationConfig(generationConfig)
                .withSystemInstruction(systemInstruction);
    }

    private static GoogleCredentials loadCredentials() {
        String envPath = System.getenv("GOOGLE_APPLICATION_CREDENTIALS");

        if (envPath == null || envPath.isEmpty() || !Files.exists(Path.of(envPath))) {
            Path defaultAdcPath = Paths.get(System.getProperty("user.home"),
                    ".config", "gcloud", "application_default_credentials.json"
            );

            if (Files.exists(defaultAdcPath)) {
                try (FileInputStream fis = new FileInputStream((defaultAdcPath.toFile()))) {
                    GoogleCredentials creds = GoogleCredentials.fromStream(fis);
                    creds.refreshIfExpired();
                    System.out.println("Loaded Google Cloud credentials from default path: " + defaultAdcPath);
                    return creds;
                } catch (Exception e) {
                    throw new RuntimeException("Failed to read credentials from " + defaultAdcPath, e);
                }
            }
        }
        try {
            GoogleCredentials creds = GoogleCredentials.getApplicationDefault();
            creds.refreshIfExpired();
            return creds;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load Google Cloud credentials.", e);
        }
    }

    public static void verifyCredentials() {
        loadCredentials();
    }

    public String askLlm(String prompt) {
        for (int attempt = 1; attempt <=2; attempt++) {
            try {
                GenerateContentResponse response = model.generateContent(prompt);
                return ResponseHandler.getText(response).trim();
            } catch (Exception e) {
                System.err.printf("⚠️ Vertex AI call attempt %d/2 failed: %s%n", attempt, e.getMessage());
                if (attempt == 1) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Interrupted while waiting for retry", ie);
                    }
                }
            }
        }
        return null;
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
