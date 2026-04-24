package model;

import java.util.ArrayList;
import java.util.List;

public class GameState {
    private CardSuit hokm;
    private Trick currentTrick;

    private int team1Tricks;
    private int team2Tricks;

    private int team1Score;
    private int team2Score;

    private final List<Card> playedCardsHistory = new ArrayList<>();

    public GameState() {
        this.hokm = null;
        this.currentTrick = null;
        this.team1Tricks = 0;
        this.team2Tricks = 0;
        this.team1Score = 0;
        this.team2Score = 0;
    }
    public GameState(CardSuit hokm, Trick currentTrick, int team1TricksWon, int team2TricksWon, int team1Score, int team2Score) {
        this.hokm = hokm;
        this.currentTrick = currentTrick;
        this.team1Tricks = team1TricksWon;
        this.team2Tricks = team2TricksWon;
        this.team1Score = team1Score;
        this.team2Score = team2Score;
    }



    public CardSuit getHokm() {
        return hokm;
    }

    public void setHokm(CardSuit hokm) {
        this.hokm = hokm;
    }

    public Trick getCurrentTrick() {
        return currentTrick;
    }

    public void setCurrentTrick(Trick currentTrick) {
        this.currentTrick = currentTrick;
    }

    public void addTrickToTeam1() { this.team1Tricks++; }
    public void addTrickToTeam2() { this.team2Tricks++; }

    public int getTeam1Tricks() { return team1Tricks; }
    public int getTeam2Tricks() { return team2Tricks; }

    public void addPointsToTeam1(int points) { this.team1Score += points; }
    public void addPointsToTeam2(int points) { this.team2Score += points; }

    public int getTeam1Score() { return team1Score; }
    public int getTeam2Score() { return team2Score; }

    public List<Card> getPlayedCardsHistory() {
        return playedCardsHistory;
    }

    public void addCardToHistory(List<Card> cards) {
        this.playedCardsHistory.addAll(cards);
    }

    public void resetRound() {
        this.team1Tricks = 0;
        this.team2Tricks = 0;
        this.hokm = null;
        this.playedCardsHistory.clear();
    }

}
