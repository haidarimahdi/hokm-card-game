import ai.AiClient;
import core.GameAbortedException;
import core.GameMaster;
import model.GameState;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import player.HumanPlayer;
import player.Player;
import player.AiPlayer;
import player.TelegramHumanPlayer;
import telegram.HokmBot;

import java.util.ArrayList;
import java.util.List;

public class MainApplication {

    private static final boolean USE_TELEGRAM = true;
    private static final String PROJECT_ID = "gemini-tdd-agent-prod";
    private static final String LOCATION = "europe-west1";
    private static final String MODEL_NAME = "gemini-2.5-flash";

    public static void main(String[] args) {
        // Read configurations from Environment Variables

        if (PROJECT_ID == null || PROJECT_ID.isBlank()) {
            System.err.println("⚠️ ERROR: Missing GCP_PROJECT_ID environment variable.");
            System.err.println("Please set it before running the game (e.g., export GCP_PROJECT_ID=your-project-id)");
            System.exit(1);
        }

//        if (LOCATION == null || LOCATION.isBlank()) LOCATION = "europe-west1";
//        if (MODEL_NAME == null || MODEL_NAME.isBlank()) MODEL_NAME = "gemini-2.5-flash";

        try {
            List<Player> players = new ArrayList<>();
            AiClient aiClient = new AiClient(PROJECT_ID, LOCATION, MODEL_NAME);
            GameState gameState = new GameState();
            List<Player> gamePlayers = new ArrayList<>();

            if (USE_TELEGRAM) {
                System.out.println("\n🚀 Booting Telegram Mode...\n");
                runTelegramMode();
            } else {
                System.out.println("\n🚀 Booting Console Mode...\n");
                runConsoleMode();
            }
//                TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
//                HokmBot myBot = new HokmBot();
//                botsApi.registerBot(myBot);
//                System.out.println("Server Online. Listening for Telegram Commands...");
//                System.out.println("Starting in Telegram mode...");
//                while (true) {
//                    String input = myBot.waitForUserInput();
//
//                    if ("CMD_START".equals(input)) {
//                        myBot.clearAbort();
//                        myBot.setGameRunning(true);
//
//                        if (myBot.getCurrentChatId() != null) {
//                            myBot.sendText(myBot.getCurrentChatId(), "🃏 Dealing cards. Get ready!");
//                        }
//
//                        try {
//                            // Launch the actual game
//                            TelegramHumanPlayer telegramPlayer = new TelegramHumanPlayer("You (Team 1)", myBot);
//                            players.add(telegramPlayer);
//                            GameMaster master = new GameMaster(players, gameState, myBot);
//
//                            if (myBot.getCurrentChatId() != null) {
//                                myBot.sendText(myBot.getCurrentChatId(), "🎉 Match finished! Type /start to play again.");
//                            }
//                        } catch (GameAbortedException e) {
//                            if (myBot.getCurrentChatId() != null) {
//                                myBot.sendText(myBot.getCurrentChatId(), "🛑 Game aborted successfully.");
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                            if (myBot.getCurrentChatId() != null) {
//                                myBot.sendText(myBot.getCurrentChatId(), "🚨 A critical error crashed the match.");
//                            }
//                        } finally {
//                            myBot.setGameRunning(false);
//
//                            // If they typed /restart, we immediately push a start command to auto-relaunch
//                            if (myBot.isRestartPending()) {
//                                myBot.clearRestartPending();
//                                myBot.queueInput("CMD_START");
//                            }
//                        }
//
//
//                        // aiClient.close(); // Only reaches here if you forcefully kill the Java server
//                    }
//                    players.add(new TelegramHumanPlayer("You (Team 1)", myBot));
//                }
//            } else {
//                System.out.println("Starting in Local Console Mode...");
//                players.add(new HumanPlayer("You (Team 1)"));
//            }
//
//            players.add(new AiPlayer("Player 2 (Team 2)", aiClient));
//            players.add(new AiPlayer("Player 3 (Team 1)", aiClient));
//            players.add(new AiPlayer("Player 4 (Team 2)", aiClient));
//
//            GameMaster master = new GameMaster(players, gameState, myBot);
//            master.playGame();

        }


//            GameState gameState = new GameState();
//            GameMaster master = new GameMaster(players, gameState);
//            master.playGame();
//
//            aiClient.close();
         catch (Exception e) {
            e.printStackTrace();
        }
//
    }

    /**
     * ==========================================
     * CONSOLE MODE
     * ==========================================
     */
    private static void runConsoleMode() {
        try (AiClient aiClient = new AiClient(PROJECT_ID, LOCATION, MODEL_NAME)) {
            GameState gameState = new GameState();
            List<Player> players = new ArrayList<>();

            // Use your original Console-based HumanPlayer
            players.add(new HumanPlayer("You (Team 1)"));
            players.add(new AiPlayer("Bot 1 (Team 2)", aiClient));
            players.add(new AiPlayer("Bot 2 (Team 1)", aiClient));
            players.add(new AiPlayer("Bot 3 (Team 2)", aiClient));

            // Passing 'null' for GameController because console mode is strictly synchronous
            // and we don't need the async abort features.
            GameMaster master = new GameMaster(players, gameState, null);
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
    private static void runTelegramMode() {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            HokmBot myBot = new HokmBot();
            botsApi.registerBot(myBot);
            System.out.println("📡 Server Online. Listening for Telegram commands...");

            AiClient aiClient = new AiClient(PROJECT_ID, LOCATION, MODEL_NAME);

            // THE INFINITE SERVER LOOP
            while (true) {
                String input = myBot.waitForUserInput();

                if ("CMD_START".equals(input)) {
                    myBot.clearAbort();
                    myBot.setGameRunning(true);

                    if (myBot.getCurrentChatId() != null) {
                        myBot.sendText(myBot.getCurrentChatId(), "🃏 Dealing cards. Get ready!");
                    }

                    try {
                        playTelegramMatch(myBot, aiClient);

                        if (myBot.getCurrentChatId() != null) {
                            myBot.sendText(myBot.getCurrentChatId(), "🎉 Match finished! Type /start to play again.");
                        }
                    } catch (GameAbortedException e) {
                        if (myBot.getCurrentChatId() != null) {
                            myBot.sendText(myBot.getCurrentChatId(), "🛑 Game aborted successfully.");
                        }
                        System.out.println("Game aborted by user.");
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (myBot.getCurrentChatId() != null) {
                            myBot.sendText(myBot.getCurrentChatId(), "🚨 A critical error crashed the match.");
                        }
                    } finally {
                        myBot.setGameRunning(false);

                        // Auto-relaunch if they typed /restart
                        if (myBot.isRestartPending()) {
                            myBot.clearRestartPending();
                            myBot.queueInput("CMD_START");
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("🚨 Fatal Error in Telegram Mode:");
            e.printStackTrace();
        }
    }

    // Helper for Telegram match setup
    private static void playTelegramMatch(HokmBot myBot, AiClient aiClient) {
        GameState gameState = new GameState();
        List<Player> players = new ArrayList<>();

        // Use the Telegram Human Player
        players.add(new TelegramHumanPlayer("You (Team 1)", myBot));
        players.add(new AiPlayer("Bot 1 (Team 2)", aiClient));
        players.add(new AiPlayer("Bot 2 (Team 1)", aiClient));
        players.add(new AiPlayer("Bot 3 (Team 2)", aiClient));

        // Pass 'myBot' as the GameController so GameMaster can listen for /end or /restart
        GameMaster master = new GameMaster(players, gameState, myBot);
        master.playGame();
    }
}