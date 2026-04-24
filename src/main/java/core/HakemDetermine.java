package core;

import model.Card;
import model.CardValue;
import model.Deck;
import player.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HakemDetermine {

    List<Player> players;

    public HakemDetermine(List<Player> players) {
        this.players = players;
    }

    public Player determineHakem() {
        Deck deck = new Deck();
        deck.shuffleDeck();

        while (true) {
            for (Player player : players) {
                Card dealtCard = deck.dealCards(1).getFirst();
                player.receiveCards(Collections.singletonList(dealtCard));

                System.out.println("Dealt " + dealtCard + " to " + player.getName());

                if (dealtCard.value() == CardValue.ACE) {
                    System.out.println(player.getName() + " is the Hakem with the card: " + dealtCard);
                    return player;
                }
            }
        }


    }
}
