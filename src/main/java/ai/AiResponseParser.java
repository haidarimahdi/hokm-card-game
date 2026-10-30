package ai;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import model.Card;
import model.CardSuit;
import model.CardValue;

import java.util.List;

public class AiResponseParser {
    private static final Gson GSON = new Gson();

    private record LlmCardPayload(CardSuit suit, CardValue value) {}

    public static Card parseDecision(String jsonResponse, List<Card> validCards) {
        LlmCardPayload payload = parseJson(jsonResponse);
        if (payload == null || payload.suit() == null || payload.value() == null) {
            return null;
        }

        Card requested = new Card(payload.suit(), payload.value());
        if (validCards.contains(requested)) {
            System.out.println("     [System 2 Decision]: " + requested);
            return requested;
        }

        System.err.println("⚠️ LLM chose a card not in validMoves: " + requested);
        return null;
    }

    public static CardSuit parseHokmDecision(String jsonResponse) {
        LlmCardPayload payload = parseJson(jsonResponse);
        if (payload == null || payload.suit() == null) {
            return null;
        }
        System.out.println("     [System 2 Hokm Decision]: " + payload.suit());
        return payload.suit();
    }

    private static LlmCardPayload parseJson(String jsonResponse) {
        if (jsonResponse == null || jsonResponse.isBlank()) {
            return null;
        }
        try {
            return GSON.fromJson(jsonResponse.trim(), LlmCardPayload.class);
        } catch (JsonSyntaxException e) {
            System.err.println("Failed to parse LLM JSON (" + jsonResponse + "): " + e.getMessage());
            return null;
        }
    }
}