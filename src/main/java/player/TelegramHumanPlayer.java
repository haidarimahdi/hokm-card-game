package player;

import core.GameAbortedException;
import core.RuleEngine;
import model.Card;
import model.CardSuit;
import model.GameObserver;
import model.GameState;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import telegram.HokmBot;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TelegramHumanPlayer implements Player, GameObserver {

    private final String name;
    private final List<Card> hand = new ArrayList<>();
    private final HokmBot bot;

    public TelegramHumanPlayer(String name, HokmBot bot) {
        this.name = name;
        this.bot = bot;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void receiveCards(List<Card> cards) {
        hand.addAll(cards);
        hand.sort(Comparator.comparing(Card::suit).thenComparing(Card::rank, Comparator.reverseOrder()));
    }

    @Override
    public List<Card> getHand() {
        return hand;
    }

    @Override
    public CardSuit chooseHokm(List<Card> firstFiveCards) {
//        StringBuilder sb = new StringBuilder("You are Hakem! Your first 5 cards:\n");
//        sb.append(firstFiveCards.toString()).append("\n\n");
//        sb.append("Reply with a suit: HEARTS, DIAMONDS, CLUBS, or SPADES.");
//
//        bot.sendText(bot.getCurrentChatId(), sb.toString());
//
//        CardSuit chosenSuit = null;
//        while (chosenSuit == null) {
//            String input = bot.waitForUserInput().trim().toUpperCase();
//            try {
//                chosenSuit = CardSuit.valueOf(input);
//            } catch (IllegalArgumentException e) {
//                bot.sendText(bot.getCurrentChatId(), "Invalid suit. Type HEARTS, DIAMONDS, CLUBS, or SPADES.");
//            }
//        }
//        return chosenSuit;
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();

        for (CardSuit suit : CardSuit.values()) {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(suit.getSymbol() + " " + suit.name());
            button.setCallbackData("HOKM_" + suit.name());
            row.add(button);
        }

        rowsInline.add(row);
        markupInline.setKeyboard(rowsInline);

        StringBuilder sb = new StringBuilder("\uD83D\uDC51 You are Hakem! Your first 5 cards:\n");

        for (Card c : firstFiveCards) {
            sb.append(c.toVisualString()).append(" ");
        }
        sb.append("\n\nTap a button to set the Hokm.");

        bot.sendTextWithKeyboard(bot.getCurrentChatId(), sb.toString(), markupInline);
        while (true) {
            String input = bot.waitForUserInput();
            if (bot.isAbortRequested() || "CMD_ABORT".equals(input)) throw new GameAbortedException("User aborted.");

            if (input.startsWith("HOKM_")) {
                try {
                    return CardSuit.valueOf(input.replace("HOKM_", ""));
                } catch (Exception ignored) {}
            }
        }
    }

    @Override
    public Card playCard(GameState currentState) {
        CardSuit leadSuit = currentState.getCurrentTrick() != null ? currentState.getCurrentTrick().getLeadSuit() : null;
        List<Card> validCards = RuleEngine.getValidMoves(hand, leadSuit);

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> currentRow = new ArrayList<>();

        for (Card card : hand) {
            boolean isValid = validCards.contains(card);
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText((isValid ? "✅ " : "❌ ") + card.toVisualString());

            button.setCallbackData("PLAY_" + card.suit().name() + "_" + card.value().name());

            currentRow.add(button);

            if (currentRow.size() == 4) {
                rowsInline.add(currentRow);
                currentRow = new ArrayList<>();
            }
        }

        if (!currentRow.isEmpty()) {
            rowsInline.add(currentRow);
        }

        markupInline.setKeyboard(rowsInline);

        String prompt = "Your turn! Hokm is " + currentState.getHokm().getSymbol() + "\nTap a valid card to play:";
        bot.sendTextWithKeyboard(bot.getCurrentChatId(), prompt, markupInline);

        while (true) {
            String input = bot.waitForUserInput();

            if (bot.isAbortRequested() || "CMD_ABORT".equals(input)) {
                throw new GameAbortedException("User aborted.");
            }

            if (input.startsWith("PLAY_")) {
                String[] parts = input.split("_");
                if (parts.length == 3) {
                    String suitStr = parts[1];
                    String valueStr = parts[2];

                    // Find the tapped card in valid moves
                    Card chosen = null;
                    for (Card c : validCards) {
                        if (c.suit().name().equals(suitStr) && c.value().name().equals(valueStr)) {
                            chosen = c;
                            break;
                        }
                    }

                    if (chosen != null) {
                        hand.remove(chosen);
                        bot.sendText(bot.getCurrentChatId(), "You played: " + chosen.toVisualString());
                        return chosen;
                    } else {
                        bot.sendText(bot.getCurrentChatId(), "❌ Invalid move! You must follow the lead suit.");
                    }
                }
            }
        }
//        hand.remove(chosenCard);
//        bot.sendText(bot.getCurrentChatId(), "You chose: " + chosenCard);
//        return chosenCard;
    }

    // --- Visual Helper Methods ---

    private String getCardVisual(Card card) {
        String valueStr = switch (card.value()) {
            case TWO -> "2"; case THREE -> "3"; case FOUR -> "4"; case FIVE -> "5";
            case SIX -> "6"; case SEVEN -> "7"; case EIGHT -> "8"; case NINE -> "9";
            case TEN -> "10"; case JACK -> "J"; case QUEEN -> "Q"; case KING -> "K"; case ACE -> "A";
        };
        return valueStr + card.suit().getSymbol();
    }

//    private String getSuitEmoji(CardSuit suit) {
//        if (suit == null) return "❓";
//        return switch (suit) {
//            case HEARTS -> "♥️";
//            case DIAMONDS -> "♦️";
//            case CLUBS -> "♣️";
//            case SPADES -> "♠️";
//        };
//    }

    @Override
    public void onCardPlayed(Player player, Card card) {
//        if (!player.getName().equals(this.name)) {
//            bot.sendText(bot.getCurrentChatId(), "🃏 " + player.getName() + " played " + getCardVisual(card));
//        }
    }

    @Override
    public void onTrickWon(Player winner) {
//        bot.sendText(bot.getCurrentChatId(), "🏆 " + winner.getName() + " won the trick!\n");
    }

    @Override
    public void onHokmChosen(CardSuit hokm) {
//        bot.sendText(bot.getCurrentChatId(), "📢 Hokm was set to " + getSuitEmoji(hokm) + " " + hokm.name());
    }

    @Override
    public void onMessage(String message) {
        bot.sendText(bot.getCurrentChatId(), "📢 " + message);
    }

}
