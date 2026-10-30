import ai.AiClient;
import core.GameEventBus;
import core.GameMaster;
import model.GameState;
import model.Team;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ui.HumanPlayer;
import model.Player;
import player.AiPlayer;
import ui.ConsoleGameObserver;
import ui.telegram.HokmBot;
import ui.telegram.TelegramGameSession;
import ui.telegram.TelegramSessionManager;

import java.util.List;

public class MainApplication {

    private static final boolean USE_TELEGRAM = true;

    private static final String PROJECT_ID = System.getenv("GCP_PROJECT_ID");
    private static final String LOCATION = System.getenv().getOrDefault("GCP_LOCATION", "europe-west2");
    private static final String MODEL_NAME = System.getenv().getOrDefault("GEMINI_MODEL_NAME", "gemini-3.5-flash");
    private static final String TELEGRAM_BOT_TOKEN = System.getenv("TELEGRAM_BOT_TOKEN");
    private static final String TELEGRAM_BOT_USERNAME = System.getenv().getOrDefault("TELEGRAM_BOT_USERNAME", "MahdisHokmBot");

    public static void main(String[] args) {

        if (!runPreFlightChecks()) {
            System.exit(1);
        }

        try {
            if (USE_TELEGRAM) {
                System.out.println("\n🚀 Booting Telegram Mode...\n");
                runTelegramMode();
            } else {
                System.out.println("\n🚀 Booting Console Mode...\n");
                runConsoleMode();
            }
        }
        catch (Exception e) {
            System.err.println("🚨 Fatal Error in Main Application:");
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static boolean runPreFlightChecks() {
        System.out.println("🔍 Running Pre-Flight Checks...");

        if (PROJECT_ID == null || PROJECT_ID.isBlank()) {
            System.err.println("⚠️ ERROR: Missing GCP_PROJECT_ID environment variable.");
            System.err.println("Please set it before running the game (e.g., export GCP_PROJECT_ID=your-project-id)");
            return false;
        }

        if (USE_TELEGRAM && (TELEGRAM_BOT_TOKEN == null || TELEGRAM_BOT_TOKEN.isBlank())) {
            System.err.println("⚠️ ERROR: Missing TELEGRAM_BOT_TOKEN environment variable.");
            System.err.println("Please set it before running the game (e.g., export TELEGRAM_BOT_TOKEN=your-bot-token)");
            return false;
        }

        try {
            AiClient.verifyCredentials();
        } catch (IllegalStateException e) {
            System.err.println("  VERTEX AI AUTH ERROR: " + e.getMessage());
            return false;
        }

        System.out.printf("  Configuration ready [Project: %s | Location: %s | Model: %s]%n",
                PROJECT_ID, LOCATION, MODEL_NAME);
        return true;
    }
    /**
     * ==========================================
     * CONSOLE MODE
     * ==========================================
     */
    private static void runConsoleMode() {
        try (AiClient aiClient = new AiClient(PROJECT_ID, LOCATION, MODEL_NAME)) {
            GameEventBus eventBus = new GameEventBus();
            eventBus.register(new ConsoleGameObserver());

            List<Player> players = List.of(
                new HumanPlayer("You", Team.TEAM_A),
                new AiPlayer("Bot 1", Team.TEAM_B, aiClient, eventBus),
                new AiPlayer("Bot 2", Team.TEAM_A, aiClient, eventBus),
                new AiPlayer("Bot 3", Team.TEAM_B, aiClient, eventBus)
            );

            // Passing 'null' for GameController because console mode is strictly synchronous
            // and we don't need the async abort features.
            GameMaster master = new GameMaster(players, new GameState(), () -> false, eventBus);
            master.playGame();

            System.out.println("Thanks for playing! Server shutting down.");
        } catch (Exception e) {
            System.err.println("🚨 Fatal Error in Console Mode:");
            e.printStackTrace();
        }
    }

    /**
     * ==========================================
     * TELEGRAM MODE
     * ==========================================
     */
    private static void runTelegramMode() throws Exception {


        try (AiClient aiClient = new AiClient(PROJECT_ID, LOCATION, MODEL_NAME)) {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            HokmBot bot = new HokmBot(TELEGRAM_BOT_USERNAME, TELEGRAM_BOT_TOKEN);

            TelegramSessionManager sessionManager = new TelegramSessionManager(bot, aiClient);
            bot.setUpdateHandler(sessionManager::handleUpdate);

            botsApi.registerBot(bot);
            System.out.println("📡 Server Online. Listening for Telegram commands from multiple users...");

            new java.util.concurrent.CountDownLatch(1).await(); // Keep the main thread alive indefinitely
        }
    }

}