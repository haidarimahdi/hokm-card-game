package core;

import model.Card;
import model.GameObserver;
import model.GameState;
import model.Trick;
import player.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Responsible for orchestrating a single Trick consisting of 4 turns.
 */
public class TrickEngine {

    private final List<Player> players;

    private final GameController gameController;

    public TrickEngine(List<Player> players, GameController gameController) {
        this.players = players;
        this.gameController = gameController;
    }

    public Player playTrick(GameState gameState, Player leadPlayer) {

        System.out.println("\n====================================");
        broadcast("Starting new Trick! " + leadPlayer.getName() + " is the lead!");

        Trick trick = new Trick();
        gameState.setCurrentTrick(trick);

        int leadIndex = players.indexOf(leadPlayer);

        try {
            List<Card> playedCards = new ArrayList<>();
            // Run the 4 turns in order
            for (int i = 0; i < players.size(); i++) {
                if (gameController != null && gameController.isAbortRequested()) {
                    throw new GameAbortedException("Game aborted by user.");
                }
                int currentPlayerIndex = (leadIndex + i) % players.size();
                Player currentPlayer = players.get(currentPlayerIndex);

                if (!currentPlayer.getHand().contains("You")) {
                    broadcast("⏳ " + currentPlayer.getName() + "...");
                }

                // Ask player for a card
                Card playedCard = currentPlayer.playCard(gameState);

                // Validation: Use RuleEngine to verify
                if (!RuleEngine.isValidMove(playedCard, currentPlayer.getHand(), trick.getLeadSuit())) {
                    System.err.println("🚨 ILLEGAL MOVE BLOCKED! " + currentPlayer.getName() + " tried to break suit with " + playedCard);

                    // Fallback: Force a valid move
                    playedCard = RuleEngine.getValidMoves(currentPlayer.getHand(), trick.getLeadSuit()).getFirst();
                    currentPlayer.getHand().remove(playedCard);
                    System.out.println(">> Referee forces " + currentPlayer.getName() + " to play " + playedCard);
                } else {
//                    broadcast(">> " + currentPlayer.getName() + " plays " + playedCard);
                    // Register card to the trick
                    trick.addCard(currentPlayer, playedCard);

                    // Notify Observers (if applicable)
                    notifyCardPlayed(currentPlayer, playedCard);
                    playedCards.add(trick.getPlayedCards().get(currentPlayer));
                    String saveCards = "";
                    for (int j = 0; j < playedCards.size(); j++) {
                        if (j != 0) {
                            saveCards += " | ";
                        }
                         saveCards += playedCards.get(j).toVisualString();
                    }
                    broadcast("{" + saveCards + "}");
//                    broadcast(trick.getPlayedCards().values().toString());
                }

            }
        } catch (Exception e) {
            System.err.println("Error during trick play in TRICK ENGINE: " + e.getMessage());
        }

        // Determine Winner using RuleEngine
        Player winner = RuleEngine.determineTrickWinner(trick, gameState.getHokm());
//        broadcast("✔️ " + winner.getName() + " won the trick!");

        // Update GameState History
        gameState.addCardToHistory(new ArrayList<>(trick.getPlayedCards().values()));

        // Notify Observers
        notifyTrickWon(winner);

        return winner;
    }

    // Helper to notify observers (keeps TrickEngine clean)
    private void notifyCardPlayed(Player p, Card playedCard) {
        for (Player player : players) {
            if (player instanceof GameObserver) {
                ((GameObserver) player).onCardPlayed(p, playedCard);
            }
        }
    }

    private void notifyTrickWon(Player winner) {
        for (Player player : players) {
            if (player instanceof GameObserver observer) {
                observer.onTrickWon(winner);
            }
        }
    }

    private void broadcast(String message) {
        System.out.println(message);
        for (Player player : players) {
            if (player instanceof GameObserver observer) {
                observer.onMessage(message);
             }
        }
    }
}