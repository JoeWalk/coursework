package com.company;

import java.sql.*;
import java.util.ArrayList;

public class Admin {

    public static String[] drivers = {"AlexanderAlbon", "FernandoAlonso", "ValtteriBottas", "PierreGasly", "LewisHamilton", "NicholasLatifi", "CharlesLeclerc", "KevinMagnussen",
            "LandoNorris", "EstebanOcon", "SergioPerez", "DanielRicciardo", "GeorgeRussell", "CarlosSainz", "MickSchumacher", "LanceStroll", "YukiTsunoda", "MaxVerstappen",
            "SebastianVettel", "GuanyuZhou"};
    public static String[] circuits = {"Bahrain", "Saudi Arabia", "Australia", "Emilia-Romagna", "Miami", "Spain", "Monaco", "Azerbaijan", "Canada", "Britain", "Austria", "France", "Hungary",
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
        while (!valid) {
            password = Main.getInput("Please enter the password you would like to use");
            if (password.length() > 6) {
                valid = true;
            } else {
                System.out.println("Password is too short");
            }
        }
        return (password);
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
        } catch (Exception e) {
            System.out.println("Error in the SQL class: " + e);
        }
    }

    public static String showBalance(String email) {
        String balance = "";
        String DatabaseLocation = "jdbc:ucanaccess://X://My Documents//Computer Science//Coursework//database1.accdb";

        try (Connection con = DriverManager.getConnection(DatabaseLocation)) {

            Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);

            String sql = "SELECT * FROM LogIn";

            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                if (email.equals(rs.getString("Email"))) {
                    balance = ("£" + rs.getString("Balance"));
                }
            }
        } catch (Exception e) {
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
        String typeOfBet = "";
        while (!ans.equals("D")) {
            ans = Main.getInput("Enter the letter of the option you would like to chose");
            if (ans.equals("A")) {
                typeOfBet = "Pole Position";
                for (int i = 0; i < 20; i++) {
                    String driver = drivers[i];
                    int odds = getOdds(typeOfBet, driver);
                }
            }

            if (ans.equals("B")) {
                typeOfBet = "Race Winner";
                for (int i = 0; i < 20; i++) {
                    String driver = drivers[i];
                    int odds = getOdds(typeOfBet, driver);
                }
            }
            if (ans.equals("C")) {
                typeOfBet = "Finish Race on Podium";
                for (int i = 0; i < 20; i++) {
                    String driver = drivers[i];
                    int odds = getOdds(typeOfBet, driver);
                }
            }

                        // Need to show drivers and their odds
        }
    }
                String chosenDriver = Main.getInput("Which driver would you like to bet on? ");
                // Need to do validity check
                int amountBet = Main.getIntInput("How much would you like to bet? ");
                // Need to do validity check

    public static void displayBetMenu() {
        System.out.println("What would you like to bet on before qualifying?");
        System.out.println("");
        System.out.println("A) Pole Position");
        System.out.println("B) Race Winner");
        System.out.println("C) Finish Race on Podium");
        System.out.println("D) Do Not Bet Simulate Qualifying");
    }

    public static int getOdds(String typeOfBet, String driver) {
        int odds = 0;
        ArrayList<Integer> oddsArray = new ArrayList<>();
        String year = "2022";
        ArrayList<Integer> averageOfYears = new ArrayList<>();
        averageOfYears = getAverageOfYear(driver);

        String DatabaseLocation = "jdbc:ucanaccess://X://My Documents//Computer Science//Coursework//database1.accdb";

        try (Connection con = DriverManager.getConnection(DatabaseLocation)) {

            Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);

            String sql = "SELECT Year, GrandPrix, Grid FROM " + driver;

            ResultSet rs = stmt.executeQuery(sql);

            while(rs.next()) {
                if ((rs.getInt("Year") == 2022) && (rs.getString("GrandPrix").equals(circuits[currentCircuit]))) {
                    for (int i = 0; i < 5; i++) {
                        oddsArray.add(rs.getInt("Grid") - averageOfYears.get(0));
                    }
                }
                if ((rs.getString("GrandPrix")).equals(circuits[currentCircuit])) {
                    for (int i = 0; i < 4; i++) {
                        oddsArray.add(rs.getInt("Grid") - averageOfYears.get(2022 - (rs.getInt("Year"))));
                    }
                }
                if ((rs.getInt("Year")) == 2022) {
                    for (int i = 0; i < 3; i++) {
                        oddsArray.add(rs.getInt("Grid") - averageOfYears.get(0));
                    }
                }
            }
        }
        catch (Exception e) {
            System.out.println("Error in the SQL class: " + e);
        }

        odds = fillOutOdds(oddsArray, averageOfYears);

        return odds;
    }

    public static ArrayList<Integer> getAverageOfYear (String driver) {

        ArrayList<Integer> averageOfYears = new ArrayList<>();
        ArrayList<Integer> averageArray = new ArrayList<>();
        ArrayList<Integer> years = new ArrayList<>();
        years.add(2022);
        boolean valid = false;
        int average = 0;

        String DatabaseLocation = "jdbc:ucanaccess://X://My Documents//Computer Science//Coursework//database1.accdb";

        try (Connection con = DriverManager.getConnection(DatabaseLocation)) {

            Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);

            String sql = "SELECT * FROM " + driver;

            ResultSet rs = stmt.executeQuery(sql);

            while(rs.next()) {
                for (int i = 0; i < (years.size()); i++){
                    if ((rs.getInt("Year") == (years.get(i)))){
                        valid = true;
                    }
                }
                if (!valid) {
                    years.add(rs.getInt("Year"));
                    valid = false;
                }
            }

            // Here down need to be in separate try catch

            for (int i = 0; i < (years.size()); i++) {
                while (rs.next()) {
                    if ((rs.getInt("Year") == (years.get(i)))) {
                        averageArray.add(rs.getInt("Grid"));
                    }
                }
            }

            for(int i = 0; i < (years.size()); i++){
                for (int a = 0; a < averageArray.size(); a++) {
                    average = average + averageArray.get(i);
                }
                average = average / averageArray.size();
                averageOfYears.add(average);
             }
        }
    catch (Exception e) {
        System.out.println("Error in the SQL class: " + e);
    }

        return (averageOfYears);
    }


    public static int fillOutOdds (ArrayList <Integer> oddsArray, ArrayList<Integer> averageOfYear) {
        int odds = 0;
        int fill = 0;
        boolean valid = false;
        ArrayList<Integer> newOddsArray = new ArrayList<>();
        for (int length = 0; length < oddsArray.size(); length++) {
            fill = oddsArray.get(length) + averageOfYear.get(0);
            newOddsArray.add(fill);
        }
        for (int i = 1; i < 21; i++) {
            valid = false;
            for (int length = 0; length < newOddsArray.size(); length++){
                if (newOddsArray.get(length) == i){
                    valid = true;
                }
            }
            if (valid == false) {
                newOddsArray.add(i);
            }
        }
        // Some error here

        return odds;
    }
}
