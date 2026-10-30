package ui.telegram;

import model.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import model.BasePlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TelegramHumanPlayer extends BasePlayer {
    private final HokmBot bot;
    private final TelegramGameSession session;

    public TelegramHumanPlayer(String name, Team team, HokmBot bot, TelegramGameSession session) {
        super(name, team);
        this.bot = bot;
        this.session = session;
    }

    @Override
    public CardSuit chooseHokm(List<Card> firstFiveCards) {
        List<InlineKeyboardButton> row = new ArrayList<>();
        for (CardSuit suit : CardSuit.values()) {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(suit.getSymbol() + " " + suit.name());
            button.setCallbackData("HOKM_" + suit.name());
            row.add(button);
        }

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup(List.of(row));

        String cardsDisplay = firstFiveCards.stream()
                .map(Card::toVisualString)
                .collect(Collectors.joining(" "));

        String prompt = "👑 You are Hakem! Your first 5 cards:\n" + cardsDisplay + "\n\nTap a button to set the Hokm:";
        bot.sendTextWithKeyboard(session.getChatId(), prompt, markup);

        while (true) {
            String input = session.waitForUserInput(); // Automatically throws GameAbortedException on /end or /restart
            if (input.startsWith("HOKM_")) {
                try {
                    return CardSuit.valueOf(input.substring(5));
                } catch (IllegalArgumentException ignored) {}
            }
        }
    }

    @Override
    protected Card selectCard(GameState state, List<Card> validCards) {
        InlineKeyboardMarkup markup = buildHandKeyboard(validCards);
        String prompt = "Your turn! Hokm is " + state.getHokm().getSymbol() + "\nTap a valid card to play:";
        bot.sendTextWithKeyboard(session.getChatId(), prompt, markup);

        while (true) {
            String input = session.waitForUserInput();
            if (input.startsWith("PLAY_")) {
                Card chosen = parseCardCallback(input);
                if (chosen != null && validCards.contains(chosen)) {
                    bot.sendText(session.getChatId(), "You played: " + chosen.toVisualString());
                    return chosen; // BasePlayer.playCard() automatically removes this card from the hand
                }
                bot.sendText(session.getChatId(), "⚠️ Invalid move! You must follow the lead suit.");
            }
        }
    }

    private InlineKeyboardMarkup buildHandKeyboard(List<Card> validCards) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> currentRow = new ArrayList<>();

        for (Card card : getHand()) {
            boolean isValid = validCards.contains(card);
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText((isValid ? "✅ " : "🚫 ") + card.toVisualString());
            button.setCallbackData("PLAY_" + card.suit().name() + "_" + card.value().name());
            currentRow.add(button);

            if (currentRow.size() == 4) {
                rows.add(currentRow);
                currentRow = new ArrayList<>();
            }
        }
        if (!currentRow.isEmpty()) {
            rows.add(currentRow);
        }
        return new InlineKeyboardMarkup(rows);
    }

    private Card parseCardCallback(String callbackData) {
        String[] parts = callbackData.split("_");
        if (parts.length != 3) {
            return null;
        }
        try {
            CardSuit suit = CardSuit.valueOf(parts[1]);
            CardValue value = CardValue.valueOf(parts[2]);
            return new Card(suit, value);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
