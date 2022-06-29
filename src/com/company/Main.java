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
        Admin.showBalance(email);

        Admin.displayMainMenu();

        Character ans = ' ';
        while (ans != 'Q') {
            if (ans == 'A') {
                Admin.preQualifyingBets();
            }
        }

    }

    public static String getInput(String prompt) {
        System.out.println(prompt);
        Scanner input = new Scanner(System.in);
        return input.nextLine();
    }

    public static boolean logIn(String email, String password) {
        boolean valid = false;
        boolean emailValid = false;
        boolean passwordValid = false;

        String DatabaseLocation = "jdbc:ucanaccess://X://My Documents//Computer Science//Coursework//database1.accdb";

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
}
