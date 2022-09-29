package com.company;

public class Bets {

    private String email;
    private String chosenDriver;
    private String typeOfBet;
    private int betAmount;
    private double oddsOfBet;

    public Bets(String email, String chosenDriver, String typeOfBet, int betAmount, double oddsOfBet) {
        this.email = email;
        this.chosenDriver = chosenDriver;
        this.typeOfBet = typeOfBet;
        this.betAmount = betAmount;
        this.oddsOfBet = oddsOfBet;
    }

    @Override
    public String toString() {
        return email + ", " + chosenDriver + ", " + typeOfBet + ", " + betAmount + ", " + oddsOfBet;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getChosenDriver() {
        return chosenDriver;
    }

    public void setChosenDriver(String chosenDriver) {
        this.chosenDriver = chosenDriver;
    }

    public String getTypeOfBet() {
        return typeOfBet;
    }

    public void setTypeOfBet(String typeOfBet) {
        this.typeOfBet = typeOfBet;
    }

    public int getBetAmount() {
        return betAmount;
    }

    public void setBetAmount(int betAmount) {
        this.betAmount = betAmount;
    }

    public double getOddsOfBet() {
        return oddsOfBet;
    }

    public void setOddsOfBet(double oddsOfBet) {
        this.oddsOfBet = oddsOfBet;
    }
}
