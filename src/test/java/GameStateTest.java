//import model.CardSuit;
//import model.GameState;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNull;
//
//public class GameStateTest {
//    private GameState gameState;
//
//    @BeforeEach
//    public void setup() {
//        gameState = new GameState();
//    }
//
//    @Test
//    public void testTrickAndScoreTracking() {
//        gameState.addTrickToTeam1();
//        gameState.addTrickToTeam1();
//        gameState.addTrickToTeam2();
//
//        assertEquals(2, gameState.getTeam1Tricks(), "Team 1 should have 2 tricks.");
//        assertEquals(1, gameState.getTeam2Tricks(), "Team 2 should have 1 trick.");
//
//        gameState.addPointsToTeam1(1);
//        assertEquals(1, gameState.getTeam1Score(), "Team 1 should have 1 Game Score.");
//    }
//
//    @Test
//    public void testRoundTResetLeavesGameSdcoresIntact() {
//        gameState.setHokm(CardSuit.HEARTS);
//        gameState.addTrickToTeam1();
//        gameState.addTrickToTeam2();
//        gameState.addPointsToTeam1(1); // Team 1 has 1 overall point
//
//        // Trigger the reset (happens when a team hits 7 tricks)
//        gameState.resetRound();
//
//        // Assert tricks and Hokm are wiped
//        assertEquals(0, gameState.getTeam1Tricks(), "Tricks should reset to 0.");
//        assertEquals(0, gameState.getTeam2Tricks(), "Tricks should reset to 0.");
//        assertNull(gameState.getHokm(), "Hokm should be null after a round reset.");
//
//        // Assert game score remains untouched
//        assertEquals(1, gameState.getTeam1Score(), "Game Score must NOT be reset between rounds.");
//    }
//}
