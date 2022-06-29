package com.company;

public class Bets {
    private String email;
    private int balance;
    private String typeOfBet;
    private int odds;
    private String driver;
    private int amountBet;

    public Bets(String email, int balance, String typeOfBet, int odds, String driver, int amountBet) {
        this.email = email;
        this.balance = balance;
        this.typeOfBet = typeOfBet;
        this.odds = odds;
        this.driver = driver;
        this.amountBet = amountBet;
    }

    @Override
    public String toString() {
        return email + ", " + balance + ", " + typeOfBet + ", " + odds + ", " + driver + ", " + amountBet;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public String getTypeOfBet() {
        return typeOfBet;
    }

    public void setTypeOfBet(String typeOfBet) {
        this.typeOfBet = typeOfBet;
    }

    public int getOdds() {
        return odds;
    }

    public void setOdds(int odds) {
        this.odds = odds;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public int getAmountBet() {
        return amountBet;
    }

    public void setAmountBet(int amountBet) {
        this.amountBet = amountBet;
    }
}
