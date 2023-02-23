package com.company;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        boolean logInSuccessful = false;
        String email = "";
        String password = "";
        while (logInSuccessful == false) {
            String answer = getInput("Do want to log in (L) or register for an account (R)? ");
            if (answer.equals("L")){
                email = Admin.getEmail(email);
                password = Admin.getPassword(password);
                logInSuccessful = logIn(email, password);
                if (logInSuccessful == false) {
                    System.out.println("Not a  registered email or password");
                }
            }
            else {
                email = Admin.getEmail(email);
                password = Admin.getPassword(password);
                Admin.writeToDatabase(email,password);
            }
        }
        System.out.println("\nYour current balance is " + Admin.showBalance(email));;

        Admin.displayMainMenu();

        String ans = "";

        while (!ans.equals("Q")) {
            ans = getInput("Enter the letter of the option you would like to chose");
            if (ans.equals("A")) {
                Admin.preQualifyingBets(email, password);
                System.out.println("");
                Admin.displayMainMenu();
            }

            if (ans.equals("B")) {
                explanationOfF1();
                System.out.println("");
                Admin.displayMainMenu();
            }

            if (ans.equals("C")) {
                explanationOfBettingSystem();
                System.out.println("");
                Admin.displayMainMenu();
            }
        }

    }

    public static String getInput(String prompt) {
        System.out.println(prompt);
        Scanner input = new Scanner(System.in);
        return input.nextLine();
    }

    public static int getIntInput(String prompt) {
        System.out.println(prompt);
        Scanner input = new Scanner(System.in);
        return input.nextInt();
    }

    public static boolean logIn(String email, String password) {
        boolean valid = false;
        boolean emailValid = false;
        boolean passwordValid = false;

        String DatabaseLocation = "jdbc:ucanaccess://C://Users//kenny//Documents//ComputerScienceCoursework//database1.accdb";

        try (Connection con = DriverManager.getConnection(DatabaseLocation)) {

            Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);

            String sql = "SELECT * FROM LogIn";

            ResultSet rs = stmt.executeQuery(sql);

            while(rs.next()) {
                if (email.equals(rs.getString("Email"))) {
                    emailValid = true;
                }
            }
        }
        catch (Exception e) {
            System.out.println("Error in the SQL class: " + e);
        }

        try (Connection con = DriverManager.getConnection(DatabaseLocation)) {

            Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);

            String sql = "SELECT * FROM LogIn";

            ResultSet rs = stmt.executeQuery(sql);

            while(rs.next()) {
                if (password.equals(rs.getString("Password"))) {
                    passwordValid = true;
                }
            }
        }
        catch (Exception e) {
            System.out.println("Error in the SQL class: " + e);
        }

        if ((emailValid == true) && (passwordValid == true)) {
            valid = true;
        }
        return valid;
    }

    public static void explanationOfF1 () {
        System.out.println("\nThere are 20 drivers in Formula 1 that race in 22 different circuits. " +
                "\nEach time, before they race, they must compete in qualifying to determine the order they start the race in. " +
                "\nThe winner of qualifying starts in pole position (first place). " +
                "\nThe higher they start on the grid, the more chance they have of winning. ");
    }

    public  static void explanationOfBettingSystem () {
        System.out.println("\nThe odds of your bet will be displayed before you place it. " +
                "\nYour winnings will be how much you bet, multiplied by (the odds plus 1), then all doubled. " +
                "\nThe money you bet will be removed from your account as soon as you place the bet and any winnings will be put into your account as soon as you win them.");
    }


}
