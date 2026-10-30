package core;

import model.Card;
import model.CardSuit;
import model.GameObserver;
import model.Trick;
import model.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class GameEventBus implements GameObserver {
    private final List<GameObserver> observers = new ArrayList<>();

    public void register(GameObserver observer) {
        observers.add(observer);
    }

    private void notifyAll(Consumer<GameObserver> event) {
        observers.forEach(event);
    }

    @Override
    public void onRoundStarted(Player hakem, int t1Score, int t2Score) {
        notifyAll(o -> o.onRoundStarted(hakem, t1Score, t2Score));
    }

    @Override
    public void onHokmChosen(Player hakem, CardSuit hokm) {
        notifyAll(o -> o.onHokmChosen(hakem, hokm));
    }

    @Override
    public void onTrickStarted(Player leadPlayer, int t1Tricks, int t2Tricks) {
        notifyAll(o -> o.onTrickStarted(leadPlayer, t1Tricks, t2Tricks));
    }

    @Override
    public void onPlayerThinking(Player player) {
        notifyAll(o -> o.onPlayerThinking(player));
    }

    @Override
    public void onCardPlayed(Player player, Card card, Trick trick) {
        notifyAll(o -> o.onCardPlayed(player, card, trick));
    }

    @Override
    public void onTrickWon(Player winner, int t1Tricks, int t2Tricks) {
        notifyAll(o -> o.onTrickWon(winner, t1Tricks, t2Tricks));
    }

    @Override
    public void onRoundEnded(int winningTeam, int t1Score, int t2Score) {
        notifyAll(o -> o.onRoundEnded(winningTeam, t1Score, t2Score));
    }

    @Override
    public void onMatchEnded(int winningTeam, int t1Score, int t2Score) {
        notifyAll(o -> o.onMatchEnded(winningTeam, t1Score, t2Score));
    }
}
