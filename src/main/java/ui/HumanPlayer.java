package ui;

import model.*;
import model.BasePlayer;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class HumanPlayer extends BasePlayer {
    private final Scanner scanner = new Scanner(System.in);

    public HumanPlayer(String name, Team team) {
        super(name, team);
    }

    @Override
    public CardSuit chooseHokm(List<Card> firstFiveCards) {
        String cardsDisplay = firstFiveCards.stream()
                .map(Card::toVisualString)
                .collect(Collectors.joining(" "));

        System.out.println("\n👑 You are Hakem! Your first 5 cards: " + cardsDisplay);
        System.out.println("Choose Hokm:");

        CardSuit[] suits = CardSuit.values();
        for (int i = 0; i < suits.length; i++) {
            System.out.printf("  [%d] %s %s%n", i + 1, suits[i].getSymbol(), suits[i].name());
        }

        int choice = readChoice(suits.length);
        return suits[choice - 1];
    }

    @Override
    protected Card selectCard(GameState state, List<Card> validCards) {
        String fullHandDisplay = getHand().stream()
                .map(Card::toVisualString)
                .collect(Collectors.joining("  "));

        System.out.println("\n----- Your Turn (Hokm: " + state.getHokm().getSymbol() + " " + state.getHokm() + ") -----");
        System.out.println("Your full hand: " + fullHandDisplay);
        System.out.println("Valid cards you can play:");

        for (int i = 0; i < validCards.size(); i++) {
            System.out.printf("  [%d] %s (%s)%n", i + 1, validCards.get(i).toVisualString(), validCards.get(i));
        }

        int choice = readChoice(validCards.size());
        return validCards.get(choice - 1); // BasePlayer.playCard() removes this card from your hand
    }

    private int readChoice(int max) {
        while (true) {
            System.out.printf("Enter a number between %d and %d: ", 1, max);
            String input = scanner.nextLine().trim();
            try {
                int choice = Integer.parseInt(input);
                if (choice >= 1 && choice <= max) {
                    return choice;
                }
            } catch (NumberFormatException ignored) {}
            System.out.println("⚠️ Invalid choice. Please enter a valid number between " + 1 + " and " + max + ".");
        }
    }

}
