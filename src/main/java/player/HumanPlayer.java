package player;

import core.RuleEngine;
import model.Card;
import model.CardSuit;
import model.GameObserver;
import model.GameState;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class HumanPlayer implements Player , GameObserver {
    private final String name;
    private final List<Card> hand = new ArrayList<>();
    private final Scanner scanner = new Scanner(System.in);

    public HumanPlayer(String name) {
        this.name = name;
    }

//    public HumanPlayer(String name, List<Card> cards) {
//        this.name = name;
//        this.hand = cards;
//        this.scanner = new Scanner(System.in);
//    }

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
    public CardSuit chooseHokm(List<Card> firstFiveCards) {
        System.out.println("\nYou are Hakem! Your first five cards: " + firstFiveCards);
        System.out.println("Choose Hokm: ");
        for (CardSuit suit : CardSuit.values()) {
            System.out.println("[" + (suit.ordinal() + 1) + "] " + ": " + suit);
        }

        while (true) {
            System.out.println("Enter the number corresponding to your choice between 1 and 4: ");
            int choice = scanner.nextInt();
            if (choice < 1 || choice > 4) {
                System.out.println("Invalid choice. Please try again.");
            } else {
                return CardSuit.values()[choice - 1];
            }
        }
    }

    @Override
    public Card playCard(GameState currentState) {
        CardSuit leadSuit = currentState.getCurrentTrick() != null ? currentState.getCurrentTrick().getLeadSuit() : null;
        List<Card> validCards = RuleEngine.getValidMoves(hand, leadSuit);

        System.out.println("----- Your Turn -----");
        System.out.print("Your hand: ");
        for (int i = 1; i <= hand.size(); i++) {
            System.out.print("[" + i + "] - " + hand.get(i - 1));
        }
        System.out.println();

        System.out.println("Hokm is: " + currentState.getHokm());
        System.out.println("Valid cards: " + validCards);
        for (int i = 0; i < validCards.size(); i++) {
            System.out.println("[" + i + 1 + "] - " + validCards.get(i));
        }

        Card chosenCard = null;
        while (chosenCard == null) {
            System.out.println("Enter the number corresponding to the card you want to play: ");
            try {
                int choice = scanner.nextInt();
                if (choice < 1 || choice > hand.size()) {
                    System.out.println("Invalid choice. Please enter a number between 1 and " + hand.size() + ".");
                } else {
                    chosenCard = validCards.get(choice - 1);
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
        hand.remove(chosenCard);
        return chosenCard;
    }

    @Override
    public List<Card> getHand() {
        return hand;
    }

    @Override
    public void onCardPlayed(Player player, Card card) {
            if (!player.equals(this)) {
                System.out.println(">> " + player.getName() + " played " + card);
            }
    }

    @Override
    public void onTrickWon(Player winner) {
        System.out.println(">> " + winner.getName() + " won the trick!");

    }

    @Override
    public void onHokmChosen(CardSuit hokm) {
        System.out.println(">> The Hokm was set to " + hokm + "!");

    }
}
