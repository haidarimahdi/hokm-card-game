package ui.telegram;

import model.Card;
import model.CardSuit;
import model.GameObserver;
import model.Trick;
import model.Player;

import java.util.stream.Collectors;

public class TelegramGameObserver implements GameObserver {
    private final HokmBot bot;
    private final Long chatId;

    public TelegramGameObserver(HokmBot bot, Long chatId) {
        this.bot = bot;
        this.chatId = chatId;
    }

    @Override
    public void onRoundStarted(Player hakem, int t1Score, int t2Score) {
        bot.sendText(chatId, String.format(
                "📊 SCORE: Team 1 [%d] - Team 2 [%d]\n👑 Hakem for this round: %s",
                t1Score, t2Score, hakem.getName()
        ));
    }

    @Override
    public void onHokmChosen(Player hakem, CardSuit hokm) {
        bot.sendText(chatId, "🎺 Hokm is set to: " + hokm.getSymbol() + " " + hokm.name());
    }

    @Override
    public void onTrickStarted(Player leadPlayer, int t1Tricks, int t2Tricks) {
        bot.sendText(chatId, String.format(
            "Trick Points -> Team 1: %d | Team 2: %d\n▶️ %s leads the trick!",
            t1Tricks, t2Tricks, leadPlayer.getName()
        ));
    }

    @Override
    public void onPlayerThinking(Player player) {
        if (!player.getName().contains("You")) {
            bot.sendText(chatId, "⏳ " + player.getName() + "...");
        }
    }

    @Override
    public void onCardPlayed(Player player, Card card, Trick currentTrick) {
        String tableCards = currentTrick.getPlayedCards().values().stream()
                .map(Card::toVisualString)
                .collect(Collectors.joining(" | ", "{ ", " }"));
        bot.sendText(chatId, tableCards);
    }

    @Override
    public void onTrickWon(Player winner, int t1Tricks, int t2Tricks) {
        bot.sendText(chatId, "🏆 " + winner.getName() + " won the trick");
    }

    @Override
    public void onRoundEnded(int winningTeam, int t1Score, int t2Score) {
        bot.sendText(chatId, String.format(
                "🎉 Team %d reaches 7 tricks and wins the round! (+1 Score)\nCurrent Score -> Team 1 [%d] - Team 2 [%d]",
                winningTeam, t1Score, t2Score
        ));
    }

    @Override
    public void onMatchEnded(int winningTeam, int t1Score, int t2Score) {
        bot.sendText(chatId, String.format(
                "🏁 MATCH OVER 🏁\nFINAL SCORE -> Team 1: %d | Team 2: %d\n🏆 Team %d Wins the Match!",
                t1Score, t2Score, winningTeam
        ));
    }
}
