import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class HakemRotationTest {

    // Simulating the GameMaster's rotation logic
    private int calculateNextHakem(int currentIndex, boolean team1WonRound) {
        boolean isHakemTeam1 = (currentIndex == 0 || currentIndex == 2);
        if (team1WonRound && !isHakemTeam1) return (currentIndex + 1) % 4;
        if (!team1WonRound && isHakemTeam1) return (currentIndex + 1) % 4;
        return currentIndex;
    }

    @Test
    public void testHakemTeam1Wins_StaysWithPlayer0() {
        int currentHakem = 0; // Player 0 (Team 1)
        boolean team1Won = true;

        int nextHakem = calculateNextHakem(currentHakem, team1Won);

        assertEquals(0, nextHakem, "Player 0 should remain Hakem because Team 1 won.");
    }

    @Test
    public void testHakemTeam1Loses_PassesToPlayer1() {
        int currentHakem = 0; // Player 0 (Team 1)
        boolean team1Won = false; // Team 2 won

        int nextHakem = calculateNextHakem(currentHakem, team1Won);

        assertEquals(1, nextHakem, "Hakem should pass to Player 1 (Team 2) because Team 1 lost.");
    }

    @Test
    public void testHakemTeam2Wins_StaysWithPlayer1() {
        int currentHakem = 1; // Player 1 (Team 2)
        boolean team1Won = false; // Team 2 won

        int nextHakem = calculateNextHakem(currentHakem, team1Won);

        assertEquals(1, nextHakem, "Player 1 should remain Hakem because Team 2 won.");
    }

    @Test
    public void testHakemTeam2Loses_PassesToPlayer2() {
        int currentHakem = 1; // Player 1 (Team 2)
        boolean team1Won = true; // Team 1 won

        int nextHakem = calculateNextHakem(currentHakem, team1Won);

        assertEquals(2, nextHakem, "Hakem should pass to Player 2 (Team 1) because Team 2 lost.");
    }

    @Test
    public void testHakemWrapAround_PassesFromPlayer3ToPlayer0() {
        int currentHakem = 3; // Player 3 (Team 2)
        boolean team1Won = true; // Team 1 won

        int nextHakem = calculateNextHakem(currentHakem, team1Won);

        assertEquals(0, nextHakem, "Hakem should wrap around the table to Player 0 (Team 1) because Team 2 lost.");
    }
}