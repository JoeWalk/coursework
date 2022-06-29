package com.company;

import java.sql.*;

public class Admin {

    public static String[] drivers = {"AlexanderAlbon", "FernandoAlonso", "ValtteriBottas", "PierreGasly", "LewisHamilton", "NicholasLatifi", "CharlesLeclerc", "KevinMagnussen",
    "LandoNorris", "EstebanOcon", "SergioPerez", "DanielRiccardo", "GeorgeRussell", "CarlosSainz", "MickSchumacher", "LanceStroll", "YukiTsunoda", "MaxVerstappen",
        "SebastianVettel", "GuanyuZhou"};
    public static String[] circuits = {"Bahrain","Saudi Arabia", "Australia", "Emilia-Romagna", "Miami", "Spain", "Monaco", "Azerbaijan", "Canada", "Britain", "Austria", "France", "Hungary",
            "Belgium", "Netherlands", "Italy", "Singapore", "Japan", "USA", "Mexico", "Sao Paulo", "Abu Dhabi"};
    public static int currentCircuit = 0;

    public static String getEmail(String email) {
        boolean valid = false;
        while (!valid) {
            email = Main.getInput("Please enter the email you would like to use for your account");
            if (email.contains("@") && email.contains(".")) {
                valid = true;
            } else {
                System.out.println("Please enter a valid email");
            }
        }

        return (email);
    }


    public static String getPassword(String password) {
        boolean valid = false;
        while(!valid) {
            password = Main.getInput("Please enter the password you would like to use");
            if (password.length() > 6) {
                valid = true;
            }
            else{
                System.out.println("Password is too short");
            }
        }
        return(password);
    }

    public static void writeToDatabase(String email, String password) {
        String DatabaseLocation = "jdbc:ucanaccess://X://My Documents//Computer Science//Coursework//database1.accdb";

        try (Connection con = DriverManager.getConnection(DatabaseLocation)) {

            Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);

            String sql = "INSERT INTO LogIn (Email, Password, Balance) VALUES (?, ?, ?)";

            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);
            preparedStatement.setInt(3, 100);

            int row = preparedStatement.executeUpdate();
        }
        catch (Exception e) {
            System.out.println("Error in the SQL class: " + e);
        }
    }

    public static String showBalance (String email) {
        String balance = "";
        String DatabaseLocation = "jdbc:ucanaccess://X://My Documents//Computer Science//Coursework//database1.accdb";

        try (Connection con = DriverManager.getConnection(DatabaseLocation)) {

            Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);

            String sql = "SELECT * FROM LogIn";

            ResultSet rs = stmt.executeQuery(sql);

            while(rs.next()) {
                if (email.equals(rs.getString("Email"))) {
                    balance = ("£" + rs.getString("Balance"));
                }
            }
        }
        catch (Exception e) {
            System.out.println("Error in the SQL class: " + e);
        }
        return balance;
    }

    public static void displayMainMenu() {
        System.out.println("");
        System.out.println("A) Start next race in " + circuits[currentCircuit]);
        System.out.println("B) Simple explanation of Formula 1");
        System.out.println("C) Explanation of betting system");
        System.out.println("Q) Quit");
    }

    public static void preQualifyingBets() {
        displayBetMenu();
        String ans = "";
        while (!ans.equals("D")) {
            ans = Main.getInput("Enter the letter of the option you would like to chose");
            if (ans.equals("A")) {
                String typeOfBet = "Pole Position";
            }
            if (ans.equals("B")) {
                String typeOfBet = "Race Winner";
            }
            if (ans.equals("C")) {
                String typeOfBet = "Finish Race on Podium";
            }

            // Need to show drivers and their odds
        }
        String driver = Main.getInput("Which driver would you like to bet on? ");
        // Need to do validity check
        int amountBet = Main.getIntInput("How much would you like to bet? ");
        // Need to do validity check
    }

    public static void displayBetMenu() {
        System.out.println("What would you like to bet on before qualifying?");
        System.out.println("");
        System.out.println("A) Pole Position");
        System.out.println("B) Race Winner");
        System.out.println("C) Finish Race on Podium");
        System.out.println("D) Do Not Bet Simulate Qualifying");
    }
}
