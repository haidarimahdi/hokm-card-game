package ai;

import model.Card;
import java.util.List;

public class AiResponseParser {

    public static Card parseDecision(String jsonResponse, List<Card> validCards) {
        if (jsonResponse == null || jsonResponse.isEmpty()) return null;

        // Clean markdown backticks if present
        if (jsonResponse.startsWith("```")) {
            jsonResponse = jsonResponse.replace("```json", "").replace("```", "").trim();
        }

        // We use your fast extractJsonValue approach (or you can use Jackson/Gson here later)
        String suit = extractJsonValue(jsonResponse, "suit");
        String value = extractJsonValue(jsonResponse, "value");

        if (suit.isEmpty() || value.isEmpty()) return null;

        System.out.println("   ⚡ [Decision]: Agent chose to play " + value + " of " + suit);

        // Match against valid cards
        return matchCard(suit, value, validCards);
    }

    public static model.CardSuit parseHokmDecision(String jsonResponse) {
        if (jsonResponse == null || jsonResponse.isEmpty()) return null;

        if (jsonResponse.startsWith("```")) {
            jsonResponse = jsonResponse.replace("```json", "").replace("```", "").trim();
        }

        String suitStr = extractJsonValue(jsonResponse, "suit");
        if (suitStr.isEmpty()) return null;

        System.out.println("   ⚡ [Decision]: Agent chose " + suitStr.toUpperCase() + " as the Hokm.");

        try {
            return model.CardSuit.valueOf(suitStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static Card matchCard(String suit, String value, List<Card> validCards) {
        String cleanSuit = suit.trim().toUpperCase();
        String cleanValue = value.trim().toUpperCase();

        for (Card c : validCards) {
            if (c.suit().name().equals(cleanSuit) && c.value().name().equals(cleanValue)) {
                return c;
            }
        }
        return null;
    }

    private static String extractJsonValue(String json, String key) {
        String searchKey = "\"" + key + "\":";
        int startIndex = json.indexOf(searchKey);
        if (startIndex == -1) {
            // Fallback for unquoted keys or formatting quirks
            searchKey = "\"" + key + "\" :";
            startIndex = json.indexOf(searchKey);
            if(startIndex == -1) return "";
        }

        startIndex += searchKey.length();
        int quoteStart = json.indexOf("\"", startIndex);
        int quoteEnd = json.indexOf("\"", quoteStart + 1);

        if(quoteStart == -1 || quoteEnd == -1) return "";

        return json.substring(quoteStart + 1, quoteEnd);
    }
}