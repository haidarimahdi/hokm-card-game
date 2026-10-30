package ui.telegram;

import ai.AiClient;
import ai.AiMemoryTracker;
import core.GameAbortedException;
import core.GameController;
import core.GameEventBus;
import core.GameMaster;
import model.GameState;
import model.Team;
import model.Player;
import player.AiPlayer;
import ui.ConsoleGameObserver;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TelegramGameSession implements GameController {
    private final HokmBot bot;
    private final Long chatId;
    private final AiClient aiClient;
    private final BlockingQueue<String> inputQueue = new LinkedBlockingQueue<>();

//    private volatile Long activeChatId;
    private volatile boolean gameRunning = false;
    private volatile boolean abortRequested = false;
    private volatile boolean restartPending = false;

    public TelegramGameSession(HokmBot bot, Long chatId, AiClient aiClient) {
        this.bot = bot;
        this.chatId = chatId;
        this.aiClient = aiClient;
    }

    public void onTelegramInput(String input) {
        switch (input.toLowerCase()) {
            case "/start" -> {
                if (gameRunning) {
                    bot.sendText(chatId, "Game is already running. Type /restart or /end.");
                } else {
                    inputQueue.offer("CMD_START");
                }
            }
            case "/restart" -> {
                if (gameRunning) {
                    abortRequested = true;
                    restartPending = true;
                    inputQueue.offer("CMD_ABORT");
                } else {
                    inputQueue.offer("CMD_START");
                }
            }
            case "/end" -> {
                if (gameRunning) {
                    abortRequested = true;
                    inputQueue.offer("CMD_ABORT");
                } else {
                    bot.sendText(chatId, "No game is running. Type /start to begin.");
                }
            }
            default -> {
                if (gameRunning) {
                    inputQueue.offer(input);
                } else {
                    bot.sendText(chatId, "Type /start to begin a new match.");
                }
            }
        }
    }

    public void startSessionLoop() {
        while (!Thread.currentThread().isInterrupted()) {
            String cmd = waitForUserInput();
            if (!"CMD_START".equals(cmd)) {
                continue;
            }

            abortRequested = false;
            gameRunning = true;
            inputQueue.clear();
            bot.sendText(chatId, "🃏 Dealing cards. Get ready!");

            try {
                runSingleMatch();
                bot.sendText(chatId, "🏁 Match finished! Type /start to play again.");
            } catch (GameAbortedException e) {
                bot.sendText(chatId, "🛑 Game aborted.");
            } catch (Exception e) {
                e.printStackTrace();
                bot.sendText(chatId, "❌ A critical error stopped the match.");
            } finally {
                gameRunning = false;
                if (restartPending) {
                    restartPending = false;
                    inputQueue.offer("CMD_START");
                }
            }
        }
    }

    private void runSingleMatch() {
        GameEventBus eventBus = new GameEventBus();
        eventBus.register(new TelegramGameObserver(bot, chatId));
        eventBus.register(new ConsoleGameObserver());

        AiMemoryTracker sharedTracker = new AiMemoryTracker();
        eventBus.register(sharedTracker);

        Player human = new TelegramHumanPlayer("You", Team.TEAM_A, bot, this);
        AiPlayer bot1 = new AiPlayer("Bot 1", Team.TEAM_B, aiClient, sharedTracker);
        AiPlayer bot2 = new AiPlayer("Bot 2", Team.TEAM_A, aiClient, sharedTracker);
        AiPlayer bot3 = new AiPlayer("Bot 3", Team.TEAM_B, aiClient, sharedTracker);

        List<Player> players = List.of(human, bot1, bot2, bot3);
        GameMaster master = new GameMaster(players, new GameState(), this, eventBus);
        master.playGame();
    }

    public String waitForUserInput() {
        try {
            String input = inputQueue.take();
            if (abortRequested || "CMD_ABORT".equals(input)) {
                throw new GameAbortedException("User requested abort.");
            }
            return input;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Session interrupted.");
        }
    }

    public Long getChatId() {
        return chatId;
    }

    @Override
    public boolean isAbortRequested() {
        return abortRequested;
    }
}