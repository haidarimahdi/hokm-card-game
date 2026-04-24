//import ai.AiClient;
//import model.Card;
//import model.CardSuit;
//import model.CardValue;
//import model.GameState;
//import org.junit.jupiter.api.Test;
//import player.AiPlayer;
//
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//public class VertexAiIntegrationTest {
//
//    // IMPORTANT: Replace with your actual project ID
//    private static final String PROJECT_ID = "gemini-tdd-agent-prod";
//    private static final String LOCATION = "us-central1";
//    private static final String MODEL = "gemini-2.5-flash";
//
//    AiClient aiClient = new AiClient(PROJECT_ID, LOCATION, MODEL);
//    // Note: If you don't want this running automatically with your normal builds,
//    // you can keep the @Disabled annotation. Remove it when you want to explicitly test the API.
//    @Test
//    // @Disabled("Remove this annotation to run the live API test")
//    public void testLiveVertexAiCall() {
//        AiPlayer aiPlayer = new AiPlayer("Gemini Integration Test", aiClient);
//
//        // Give it a single, undeniable choice to see if it makes it
//        Card singleCard = new Card(CardValue.KING, CardSuit.DIAMONDS, 12);
//        aiPlayer.receiveCards(List.of(singleCard));
//
//        GameState gameState = new GameState();
//        gameState.setHokm(CardSuit.HEARTS);
//
//        System.out.println("Sending live request to Vertex AI...");
//        long startTime = System.currentTimeMillis();
//
//        // This triggers the actual network call using your ADC credentials
//        Card playedCard = aiPlayer.playCard(gameState);
//
//        long endTime = System.currentTimeMillis();
//        System.out.println("Response received in " + (endTime - startTime) + "ms");
//
//        assertNotNull(playedCard, "The AI should return a valid card.");
//        assertEquals(CardSuit.DIAMONDS, playedCard.suit(), "It should have played the only card it had.");
//    }
//}