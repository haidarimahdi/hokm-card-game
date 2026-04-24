//import core.HakemDetermine;
//import model.GameState;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import player.DummyAIPlayer;
//import player.Player;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class HakemHokmDetermineTest {
//    List<Player> players;
//    GameState gameState;
//
//    @BeforeEach
//    public void setUp() {
//        players = new ArrayList<>();
//        for (int i = 0; i < 4; i++) {
//            players.add(new DummyAIPlayer("Player " + (i + 1)));
//        }
//
//        gameState = new GameState();
//
//    }
//
//    @Test
//    public void testHakemHokmDetermine() {
//        HakemDetermine hakemHokmDetermine = new HakemDetermine(players);
//        System.out.println("Hakem determined: " + hakemHokmDetermine.determineHakem().getName());
//    }
//}
