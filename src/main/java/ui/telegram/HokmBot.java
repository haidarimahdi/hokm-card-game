package ui.telegram;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.function.BiConsumer;

public class HokmBot extends TelegramLongPollingBot {
    private final String botUsername;
    private final String botToken;
    private BiConsumer<Long, String> updateHandler;

    public HokmBot(String botUsername, String botToken) {
        this.botUsername = botUsername;
        this.botToken = botToken;
    }

    public void setUpdateHandler(BiConsumer<Long, String> updateHandler) {
        this.updateHandler = updateHandler;
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (updateHandler == null) {
            return;
        }

        if (update.hasMessage() && update.getMessage().hasText()) {
            updateHandler.accept(update.getMessage().getChatId(), update.getMessage().getText().trim());
        } else if (update.hasCallbackQuery()) {
            acknowledgeCallback(update.getCallbackQuery().getId());
            Long chatId = update.getCallbackQuery().getMessage().getChatId();
            updateHandler.accept(chatId, update.getCallbackQuery().getData());
        }
    }

    public void sendText(Long chatId, String text) {
        sendTextWithKeyboard(chatId, text, null);
    }

    public void sendTextWithKeyboard(Long chatId, String text, InlineKeyboardMarkup keyboard) {
        if (chatId == null) {
            return;
        }

        SendMessage message = new SendMessage(chatId.toString(), text);
        if (keyboard != null) {
            message.setReplyMarkup(keyboard);
        }
        try {
            execute(message);
        } catch (TelegramApiException e) {
            System.err.println("Telegram send error: " + e.getMessage());
        }
    }

    private void acknowledgeCallback(String queryId) {
        try {
            execute(new AnswerCallbackQuery(queryId));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
