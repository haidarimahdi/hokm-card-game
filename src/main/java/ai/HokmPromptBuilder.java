package ai;

import model.Card;
import model.GameState;
import player.Player;

import java.util.List;
import java.util.Map;

public class HokmPromptBuilder {

    public static String buildTurnPrompt(String name, String team, GameState currentState, List<Card> validCards, String memory) {
        StringBuilder tableBuilder = new StringBuilder();

        if (currentState.getCurrentTrick() == null || currentState.getCurrentTrick().getPlayedCards().isEmpty()) {
            tableBuilder.append("Empty (You are leading).");
        } else {
            for (Map.Entry<Player, Card> entry : currentState.getCurrentTrick().getPlayedCards().entrySet()) {
                tableBuilder.append("[")
                        .append(entry.getKey().getName())
                        .append(" played ")
                        .append(entry.getValue().toString())
                        .append("], ");
            }
        }

        String history = memory != null && !memory.isEmpty() ? memory : "No history yet.";

        return String.format("""
                IDENTITY: You are %s.
                ALLIANCE: You are strictly on %s. Anyone else on %s is your partner. The other team are your opponents. NEVER cut or try to beat your own partner.
                
                HOKM: %s
                
                TABLE STATE:
                %s
                
                YOUR VALID MOVES (You MUST pick one of these):
                %s
                
                --- YOUR PRIVATE MEMORY (Game History) ---
                %s
                ------------------------------------------
                
                Analyze the game state and return your decision matching the required JSON schema.
                """,
                name, team, team,
                currentState.getHokm(),
                tableBuilder.toString(),
                validCards.toString(),
                history
        );
    }

    public static String buildChooseHokmPrompt(String name, List<model.Card> firstFiveCards) {
        return String.format("""
                IDENTITY: You are %s, an expert Grandmaster player in the Persian card game Hokm.
                SITUATION: You are the Hakem. You have just been dealt your first 5 cards, and you must declare the Hokm (Trump suit).
                
                YOUR FIRST 5 CARDS:
                %s
                
                STRATEGY RULES FOR CHOOSING HOKM (CRITICAL):
                1. QUANTITY OVER SINGLE HIGH CARDS: The most important factor is the number of cards you have in a suit. 
                    Prefer a suit where you have multiple cards over a suit where you only have 1 card.
                2. THE "OFF-SUIT ACE" RULE: Do NOT pick a suit just because you hold a lone Ace in it. An Ace in 
                    a non-Hokm suit is already a guaranteed winning trick. Save it to win external tricks!
                3. BUILDING TRUMPS: Choose a suit where you have multiple cards, especially connected mid-to-high cards 
                    (like King, Queen, Jack, 10). This gives you a strong trump base to cut opponents' cards, 
                    while your off-suit Aces win additional tricks.
                
                JSON INSTRUCTIONS:
                You must respond matching the required JSON schema.
                - Put your chosen trump suit in the 'suit' field.
                - Put 'ACE' in the 'value' field (this is just a placeholder to satisfy the schema).
                """,
                name, firstFiveCards.toString()
        );
    }
}