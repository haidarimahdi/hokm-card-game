package telegram;

import core.GameController;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class HokmBot extends TelegramLongPollingBot implements GameController {
    private final BlockingQueue<String> userInputQueue = new LinkedBlockingQueue<>();
    private Long currentChatId = null;

    // State trackers
    private volatile boolean isGameRunning = false;
    private volatile boolean abortRequested = false;
    private volatile boolean restartPending = false;

    @Override
    public String getBotUsername() {
        return "MahdisHokmBot";
    }

    @Override
    public String getBotToken() {
        return "8790267903:AAHmdsEFUKxrydN-jKLbbXTbjZ_Bnk3ZpiU";
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String text = update.getMessage().getText();
            currentChatId = update.getMessage().getChatId();
            handleTextCommand(text);
        }
        else if (update.hasCallbackQuery()) {
            String callData = update.getCallbackQuery().getData();
            currentChatId = update.getCallbackQuery().getMessage().getChatId();
            String queryId = update.getCallbackQuery().getId();

            AnswerCallbackQuery answer = new AnswerCallbackQuery();
            answer.setCallbackQueryId(queryId);
            try {execute(answer);} catch (TelegramApiException e) {e.printStackTrace();}

            if (isGameRunning) {
                userInputQueue.offer(callData);
            }
        }
    }

    private void handleTextCommand(String text) {
        if (text.equalsIgnoreCase("/start")) {
            if (isGameRunning) {
                sendText(currentChatId, "Game is already running. Type /restart to restart, or /end to stop the game.");
            } else {
                userInputQueue.offer("CMD_START");
            }
        } else if (text.equalsIgnoreCase("/restart")) {
            if (isGameRunning) {
                abortRequested = true;
                restartPending = true;
                userInputQueue.offer("CMD_ABORT");
            } else {
                userInputQueue.offer("CMD_START");
            }
        } else if (text.equalsIgnoreCase("/end")) {
            if (isGameRunning) {
                abortRequested = true;
                userInputQueue.offer("CMD_ABORT");
            } else {
                sendText(currentChatId, "No game is currently running. Type /start to start a new game.");
            }
        } else if (isGameRunning) {
            userInputQueue.offer(text);
        }
    }
    public void sendText(Long chatId, String text) {
        if (chatId == null) {
            return;
        }
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendTextWithKeyboard(Long chatId, String text, InlineKeyboardMarkup keyboard) {
        if (chatId == null) {
            return;
        }
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);
        message.setReplyMarkup(keyboard);
        try {execute(message);} catch (TelegramApiException e) {e.printStackTrace();}
    }

    public String waitForUserInput() {
        try {
            return userInputQueue.take();
        } catch (InterruptedException e) {
            return "";
        }
    }

    public Long getCurrentChatId() {
        return currentChatId;
    }

    @Override
    public boolean isAbortRequested() {
        return abortRequested;
    }

    public void clearAbort() {
        this.abortRequested = false;
    }

    public boolean isRestartPending() {
        return restartPending;
    }

    public void clearRestartPending() {
        this.restartPending = false;
    }

    public void setGameRunning(boolean isRunning) {
        this.isGameRunning = isRunning;
    }

    public void queueInput(String input) {
        userInputQueue.offer(input);
    }
}
