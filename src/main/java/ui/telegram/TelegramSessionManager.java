package ui.telegram;

import ai.AiClient;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TelegramSessionManager {
    private final HokmBot bot;
    private final AiClient aiClient;
    private final Map<Long, TelegramGameSession> sessions = new ConcurrentHashMap<>();
    private final ExecutorService sessionExecutor = Executors.newVirtualThreadPerTaskExecutor();

    public TelegramSessionManager(HokmBot bot, AiClient aiClient) {
        this.bot = bot;
        this.aiClient = aiClient;
    }

    public void handleUpdate(Long chatId, String input) {
        if (chatId == null || input == null) {
            return;
        }

        TelegramGameSession session = sessions.computeIfAbsent(chatId, id -> {
            System.out.println("Creating new game session for chatId: " + id);
            TelegramGameSession newSession = new TelegramGameSession(bot, id, aiClient);
            sessionExecutor.submit(newSession::startSessionLoop);
            return newSession;
        });

        session.onTelegramInput(input);
    }
}
