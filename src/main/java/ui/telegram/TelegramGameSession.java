package ui.telegram;

import ai.AiClient;
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
    private final AiClient aiClient;
    private final BlockingQueue<String> inputQueue = new LinkedBlockingQueue<>();

    private volatile Long activeChatId;
    private volatile boolean gameRunning = false;
    private volatile boolean abortRequested = false;
    private volatile boolean restartPending = false;

    public TelegramGameSession(HokmBot bot, AiClient aiClient) {
        this.bot = bot;
        this.aiClient = aiClient;
        this.bot.setUpdateHandler(this::onTelegramInput);
    }

    private void onTelegramInput(Long chatId, String input) {
        this.activeChatId = chatId;

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

            bot.sendText(activeChatId, "🃏 Dealing cards. Get ready!");
            try {
                runSingleMatch();
                bot.sendText(activeChatId, "🎉 Match finished! Type /start to play again.");
            } catch (GameAbortedException e) {
                bot.sendText(activeChatId, "🛑 Game aborted.");
            } catch (Exception e) {
                e.printStackTrace();
                bot.sendText(activeChatId, "⚠️ A critical error stopped the match.");
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
        eventBus.register(new TelegramGameObserver(bot, activeChatId));

        // Also mirror all events to the server console for debugging purposes
        eventBus.register(new ConsoleGameObserver());

        Player human = new TelegramHumanPlayer("You", Team.TEAM_A, bot, this);
        AiPlayer bot1 = new AiPlayer("Bot 1", Team.TEAM_B, aiClient, eventBus);
        AiPlayer bot2 = new AiPlayer("Bot 2", Team.TEAM_A, aiClient, eventBus);
        AiPlayer bot3 = new AiPlayer("Bot 3", Team.TEAM_B, aiClient, eventBus);

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

    public Long getActiveChatId() {
        return activeChatId;
    }

    @Override
    public boolean isAbortRequested() {
        return abortRequested;
    }
}