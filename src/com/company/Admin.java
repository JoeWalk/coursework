package com.company;

import java.sql.*;
import java.util.ArrayList;
import java.util.Random;

public class Admin {

    public static String[] drivers = {"AlexanderAlbon", "FernandoAlonso", "ValtteriBottas", "PierreGasly", "LewisHamilton", "NicholasLatifi", "CharlesLeclerc", "KevinMagnussen",
            "LandoNorris", "EstebanOcon", "SergioPerez", "DanielRicciardo", "GeorgeRussell", "CarlosSainz", "MickSchumacher", "LanceStroll", "YukiTsunoda", "MaxVerstappen",
            "SebastianVettel", "GuanyuZhou"};
    public static String[] circuits = {"Bahrain", "Saudi Arabia", "Australia", "Emilia-Romagna", "Miami", "Spain", "Monaco", "Azerbaijan", "Canada", "Britain", "Austria", "France", "Hungary",
            "Belgium", "Netherlands", "Italy", "Singapore", "Japan", "USA", "Mexico", "Sao Paulo", "Abu Dhabi"};

    public static int currentCircuit = 0;
    public static ArrayList<Bets> betList = new ArrayList<>();

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

        String DatabaseLocation = "jdbc:ucanaccess://C://Users//kenny//Documents//ComputerScienceCoursework//database1.accdb";

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
        String DatabaseLocation = "jdbc:ucanaccess://C://Users//kenny//Documents//ComputerScienceCoursework//database1.accdb";

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

    public static void preQualifyingBets(String email, String password) {
        double oddsOfBet = 0;
        int betAmount = 0;
        String chosenDriver = "";
        boolean valid = false;
        displayBetMenu();
        ArrayList<Integer> oddsArray = new ArrayList<>();
        ArrayList<Double> finalOddsArray = new ArrayList<>();
        double odds = 0;
        String ans = "";
        String typeOfBet = "";
        while (!ans.equals("Q")) {
            ans = Main.getInput("Enter the letter of the option you would like to chose");
            if (ans.equals("A")) {
                typeOfBet = "Pole Position";
                for (int i = 0; i < 20; i++) {
                    String driver = drivers[i];
                    oddsArray = getOdds(typeOfBet, driver);
                    odds = polePosition(oddsArray);
                    finalOddsArray.add(odds);
                    System.out.println(drivers[i] + ": " + odds);
                }
                while (!valid) {
                    chosenDriver = Main.getInput("Which driver would you like to bet on? Alternatively type Q to quit ");
                    for (int a = 0; a < drivers.length; a++) {
                        if ((drivers[a].contains(chosenDriver)) || (chosenDriver.equals("Q"))) {
                            valid = true;
                        }
                    }
                }
                valid = false;

                if (!chosenDriver.equals("Q")) {
                    betAmount = getBetAmount(email, password);
                    for (int i = 0; i < drivers.length; i++) {
                        if (drivers[i].equals(chosenDriver)) {
                            oddsOfBet = finalOddsArray.get(i);
                        }
                    }
                    setBet(betAmount, chosenDriver, typeOfBet, email, oddsOfBet);
                }

                displayBetMenu();
            }
        }
        simulateQualifying(email, password);
    }

    public static void displayBetMenu() {

        System.out.println("What would you like to bet on before qualifying?");
        System.out.println("");
        System.out.println("A) Pole Position");
        System.out.println("Q) Do Not Bet, Simulate Qualifying");
    }

    public static void displayRaceBetMenu() {

        System.out.println("What would you like to bet on before the race?");
        System.out.println("");
        System.out.println("A) Race Winner");
        System.out.println("B) Finish Race on Podium");
        System.out.println("Q) Do Not Bet, Simulate Race");
    }

    public static ArrayList<Integer> getOdds(String typeOfBet, String driver) {

        String session;
        if (typeOfBet.equals("Pole Position")) {
            session = "Grid";
        } else {
            session = "Race";
        }

        ArrayList<Integer> oddsArray = new ArrayList<>();
        String year = "2022";
        ArrayList<Integer> averageOfYears = new ArrayList<>();
        averageOfYears = getAverageOfYears(driver, session);

        String DatabaseLocation = "jdbc:ucanaccess://C://Users//kenny//Documents//ComputerScienceCoursework//database1.accdb";

        try (Connection con = DriverManager.getConnection(DatabaseLocation)) {

            Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);

            String sql = "SELECT Year, GrandPrix, Grid, Race FROM " + driver;

            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                if ((rs.getInt("Year") >= 2016)) {
                    if ((rs.getInt("Year") == 2022) && (rs.getString("GrandPrix").equals(circuits[currentCircuit]))) {
                        for (int i = 0; i < 10; i++) {
                            oddsArray.add(rs.getInt(session) - averageOfYears.get(0));
                        }
                    }
                    if ((rs.getString("GrandPrix")).equals(circuits[currentCircuit]) && (rs.getInt("Year") != 2022) && (rs.getInt("Year") > 2015)) {
                        for (int i = 0; i < 6; i++) {
                            oddsArray.add(rs.getInt(session) - averageOfYears.get(2022 - (rs.getInt("Year"))));
                        }
                    }
                    if ((rs.getInt("Year")) == 2022) {
                        for (int i = 0; i < 8; i++) {
                            oddsArray.add(rs.getInt(session) - averageOfYears.get(0));
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error in the SQL class: " + e);
        }

        ArrayList<Integer> odds = new ArrayList<>();
        odds = fillOutOdds(oddsArray, averageOfYears);

        return odds;
    }

    public static ArrayList<Integer> getAverageOfYears(String driver, String session) {

        ArrayList<Integer> averageOfYears = new ArrayList<>();
        ArrayList<Integer> averageArray = new ArrayList<>();
        ArrayList<Integer> years = new ArrayList<>();
        years.add(2022);
        boolean valid = false;
        int average = 0;

        String DatabaseLocation = "jdbc:ucanaccess://C://Users//kenny//Documents//ComputerScienceCoursework//database1.accdb";

        try (Connection con = DriverManager.getConnection(DatabaseLocation)) {

            Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);

            String sql = "SELECT * FROM " + driver;

            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                for (int i = 0; i < (years.size()); i++) {
                    valid = false;
                    if ((rs.getInt("Year") == (years.get(i)))) {
                        valid = true;
                    }
                }
                if (!valid) {
                    years.add(rs.getInt("Year"));
                    valid = false;
                }
            }
        } catch (Exception e) {
            System.out.println("Error in the SQL class: " + e);
        }

        try (Connection con = DriverManager.getConnection(DatabaseLocation)) {

            Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);

            String sql = "SELECT * FROM " + driver;

            ResultSet rs = stmt.executeQuery(sql);

            for (int i = 0; i < (years.size()); i++) {
                while (rs.next()) {
                    if ((rs.getInt("Year") == (years.get(i)))) {
                        averageArray.add(rs.getInt(session));
                    }
                }
                for (int a = 0; a < averageArray.size(); a++) {
                    average = average + averageArray.get(a);
                }
                average = average / averageArray.size();
                averageOfYears.add(average);
                averageArray.clear();
                rs.first();
            }
        } catch (Exception e) {
            System.out.println("Error in the SQL class: " + e);
        }

        return (averageOfYears);
    }


    public static ArrayList<Integer> fillOutOdds(ArrayList<Integer> oddsArray, ArrayList<Integer> averageOfYear) {

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
            for (int length = 0; length < newOddsArray.size(); length++) {
                if (newOddsArray.get(length) == i) {
                    valid = true;
                }
            }
            if (valid == false) {
                newOddsArray.add(i);
            }
        }

        return newOddsArray;
    }

    public static double polePosition(ArrayList<Integer> oddsArray) {

        double odds = 0;
        double count = 0;
        for (int i = 0; i < oddsArray.size(); i++) {
            if ((oddsArray.get(i) == 1) || (oddsArray.get(i) == 2)) {
                count = count + 1;
            }
        }
        odds = count / oddsArray.size();
        odds = odds * 100;
        odds = Math.round(odds);
        odds = odds / 100;

        return odds;
    }

    public static int getBetAmount(String email, String password) {
        int count = 0;
        boolean valid = false;
        int amountBet = 0;
        int currentBalance = 0;

        String DatabaseLocation = "jdbc:ucanaccess://C://Users//kenny//Documents//ComputerScienceCoursework//database1.accdb";

        try (Connection con = DriverManager.getConnection(DatabaseLocation)) {

            Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);

            String sql = "SELECT * FROM LogIn";

            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                count = count + 1;
                if ((rs.getString("Email").equals(email)) && (rs.getString("Password").equals(password))) {
                    while (!valid) {
                        valid = false;
                        currentBalance = rs.getInt("Balance");
                        amountBet = Main.getIntInput("How much would you like to bet? ");
                        if (rs.getInt("Balance") >= amountBet) {
                            valid = true;
                        } else {
                            System.out.println("You only have " + rs.getString("Balance"));
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error in the SQL class: " + e);
        }

        try (Connection con = DriverManager.getConnection(DatabaseLocation)) {

            Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);

            String sql = "UPDATE LogIn SET Balance = ? WHERE Email = ?";

            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setInt(1, currentBalance - amountBet);
            preparedStatement.setString(2, email);

            int row = preparedStatement.executeUpdate();

        } catch (Exception e) {
            System.out.println("Error in the SQL class: " + e);
        }

        return (amountBet);
    }

    public static void setBet(int betAmount, String chosenDriver, String typeOfBet, String email, double oddsOfBet) {
        Bets bet = new Bets(email, chosenDriver, typeOfBet, betAmount, oddsOfBet);
        System.out.println(bet.toString());
        betList.add(bet);
    }

    public static void simulateQualifying(String email, String password) {

        Random rand = new Random();
        ArrayList<Integer> openPlaces = new ArrayList<>();
        for (int i = 1; i < 21; i++) {
            openPlaces.add(i);
        }
        String driver = "";
        boolean valid = false;
        int random = 0;
        int[][] startingGrid = {{0, 0}, {1, 0}, {2, 0}, {3, 0}, {4, 0}, {5, 0}, {6, 0}, {7, 0}, {8, 0}, {9, 0}, {10, 0}, {11, 0}, {12, 0}, {13, 0}, {14, 0}, {15, 0}, {16, 0}, {17, 0}, {18, 0}, {19, 0}};
        ArrayList<Integer> driversToGo = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            driversToGo.add(i);
        }
        while (driversToGo.size() > 0) {
            random = driversToGo.get(rand.nextInt(driversToGo.size()));
            for (int a = 0; a < driversToGo.size(); a++) {
                if (driversToGo.get(a) == random) {
                    driversToGo.remove(a);
                    valid = true;
                }
            }
            if (valid == true) {
                startingGrid[random][1] = (getQualifyingResults(drivers[random], openPlaces));
            }
            valid = false;
        }
        orderGrid(startingGrid);
        for (int i = 0; i < startingGrid.length; i++) {
            System.out.println(drivers[startingGrid[i][0]] + " - " + startingGrid[i][1]);
        }

        System.out.println("");

        paybackQualiBets(startingGrid);

        raceBets(email, password);

        simulateRace(startingGrid);

        paybackRaceBets(startingGrid);

        currentCircuit = currentCircuit + 1;
        if (currentCircuit == 22) {
            currentCircuit = 0;
        }

    }

    public static int getQualifyingResults(String driver, ArrayList<Integer> openPlaces) {

        ArrayList<Integer> qualiArray = new ArrayList<>();
        ArrayList<Integer> averageOfYears = new ArrayList<>();
        boolean valid = false;
        int finalGridPlace = 0;
        int rand = 0;
        String session = "Grid";
        averageOfYears = getAverageOfYears(driver, session);

        String DatabaseLocation = "jdbc:ucanaccess://C://Users//kenny//Documents//ComputerScienceCoursework//database1.accdb";

        try (Connection con = DriverManager.getConnection(DatabaseLocation)) {

            Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);

            String sql = "SELECT * FROM " + driver;

            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                if ((rs.getInt("Year") >= 2016)) {
                    if ((rs.getInt("Year") == 2022) && (rs.getString("GrandPrix").equals(circuits[currentCircuit]))) {
                        for (int i = 0; i < 10; i++) {
                            qualiArray.add(rs.getInt("Grid") - averageOfYears.get(0));
                        }
                    }
                    if ((rs.getString("GrandPrix")).equals(circuits[currentCircuit]) && (rs.getInt("Year") != 2022) && (rs.getInt("Year") > 2015)) {
                        for (int i = 0; i < 6; i++) {
                            qualiArray.add(rs.getInt("Grid") - averageOfYears.get(2022 - (rs.getInt("Year"))));
                        }
                    }
                    if ((rs.getInt("Year")) == 2022) {
                        for (int i = 0; i < 8; i++) {
                            qualiArray.add(rs.getInt("Grid") - averageOfYears.get(0));
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error in the SQL class: " + e);
        }

        qualiArray = fillOutGrid(qualiArray, averageOfYears, openPlaces);
        Random random = new Random();
        while (valid == false) {
            rand = random.nextInt(qualiArray.size());
            finalGridPlace = qualiArray.get(rand);
            for (int a = 0; a < openPlaces.size(); a++) {
                if (openPlaces.get(a) == finalGridPlace) {
                    openPlaces.remove(a);
                    valid = true;
                    return (finalGridPlace);
                }
            }
        }
        return (finalGridPlace);
    }

    public static ArrayList<Integer> fillOutGrid(ArrayList<Integer> qualiArray, ArrayList<Integer> averageOfYear, ArrayList<Integer> openPlaces) {
        int fill = 0;
        boolean valid1 = false;
        boolean valid2 = false;
        ArrayList<Integer> newQualiArray = new ArrayList<>();
        for (int length = 0; length < qualiArray.size(); length++) {
            fill = qualiArray.get(length) + averageOfYear.get(0);
            for (int i = 0; i < openPlaces.size(); i++) {
                if (fill == openPlaces.get(i)) {
                    newQualiArray.add(fill);
                }
            }
        }
        for (int i = 1; i < 21; i++) {
            valid1 = false;
            valid2 = false;
            for (int length = 0; length < newQualiArray.size(); length++) {
                if (newQualiArray.get(length) == i) {
                    valid1 = true;
                }
            }
            for (int a = 0; a < openPlaces.size(); a++) {
                if (openPlaces.get(a) == i) {
                    valid2 = true;
                }
            }
            if ((valid1 == false) && (valid2 == true)) {
                newQualiArray.add(i);
            }
        }

        return newQualiArray;
    }

    public static int[][] orderGrid(int[][] startingGrid) {
        int temp = 0;
        for (int i = 0; i < 21; i++) {
            for (int a = 1; a < 20; a++) {
                if (startingGrid[a - 1][1] > startingGrid[a][1]) {
                    temp = startingGrid[a - 1][1];
                    startingGrid[a - 1][1] = startingGrid[a][1];
                    startingGrid[a][1] = temp;
                    temp = startingGrid[a - 1][0];
                    startingGrid[a - 1][0] = startingGrid[a][0];
                    startingGrid[a][0] = temp;

                }
            }
        }

        return (startingGrid);
    }

    public static void paybackQualiBets(int[][] startingGrid) {
        String DatabaseLocation = "jdbc:ucanaccess://C://Users//kenny//Documents//ComputerScienceCoursework//database1.accdb";
        String poleWinner = drivers[startingGrid[0][0]];
        String email = "";
        int winnings = 0;
        int currentBalance = 0;
        int amountBet = 0;
        double odds = 0;
        int oddsForEquation = 0;
        String chosenDriver = "";
        int temp = 0;
        if (betList.size() != 0) {
            for (int i = 0; i < betList.size(); i++) {
                Bets placeholder = betList.get(i);
                email = placeholder.getEmail();
                chosenDriver = placeholder.getChosenDriver();
                amountBet = placeholder.getBetAmount();
                odds = placeholder.getOddsOfBet();
                odds = odds * 100;
                oddsForEquation = (int) Math.round(odds);
                if (chosenDriver.equals(poleWinner)) {

                    // Finds users current balance to find new balance

                    try (Connection con = DriverManager.getConnection(DatabaseLocation)) {

                        Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);

                        String sql = "SELECT * FROM LogIn";

                        ResultSet rs = stmt.executeQuery(sql);

                        while (rs.next()) {
                            if (email.equals(rs.getString("email"))) {
                                currentBalance = rs.getInt("Balance");
                                temp = currentBalance + (((amountBet * 100) / oddsForEquation) * 2);
                                winnings = ((amountBet * 100) / oddsForEquation) * 2;
                                currentBalance = temp;
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("Error in the SQL class: " + e);
                    }

                    // updates database with new balance

                    try (Connection con = DriverManager.getConnection(DatabaseLocation)) {

                        Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);

                        String sql = "UPDATE LogIn SET Balance = ? WHERE Email = ?";

                        PreparedStatement preparedStatement = con.prepareStatement(sql);
                        preparedStatement.setInt(1, currentBalance);
                        preparedStatement.setString(2, email);

                        int row = preparedStatement.executeUpdate();

                    } catch (Exception e) {
                        System.out.println("Error in the SQL class: " + e);
                    }

                    System.out.println("Congratulations! You won your bet on " + placeholder.getChosenDriver() + " for Pole Position.\nYou won £" + (winnings) + "\nYour balance is now £" + currentBalance);

                } else {
                    System.out.println("You did not win your bet on " + placeholder.getChosenDriver() + " for Pole Position. Better luck next time.");
                }
            }
        }

        betList.clear();
    }

    public static void raceBets(String email, String password) {

        double oddsOfBet = 0;
        int betAmount = 0;
        String chosenDriver = "";
        boolean valid = false;
        displayRaceBetMenu();
        ArrayList<Integer> oddsArray = new ArrayList<>();
        ArrayList<Double> finalOddsArray = new ArrayList<>();
        double odds = 0;
        String ans = "";
        String typeOfBet = "";
        while (!ans.equals("Q")) {
            ans = Main.getInput("Enter the letter of the option you would like to chose");
            if (ans.equals("A")) {
                typeOfBet = "Race Winner";
                for (int i = 0; i < 20; i++) {
                    String driver = drivers[i];
                    oddsArray = getOdds(typeOfBet, driver);
                    odds = raceWinner(oddsArray);
                    finalOddsArray.add(odds);
                    System.out.println(drivers[i] + ": " + odds);
                }
                while (!valid) {
                    chosenDriver = Main.getInput("Which driver would you like to bet on? Alternatively type Q to quit ");
                    for (int a = 0; a < drivers.length; a++) {
                        if ((drivers[a].contains(chosenDriver)) || (chosenDriver.equals("Q"))) {
                            valid = true;
                        }
                    }
                }
                valid = false;

                if (!chosenDriver.equals("Q")) {
                    betAmount = getBetAmount(email, password);
                    for (int i = 0; i < drivers.length; i++) {
                        if (drivers[i].equals(chosenDriver)) {
                            oddsOfBet = finalOddsArray.get(i);
                        }
                    }
                    setBet(betAmount, chosenDriver, typeOfBet, email, oddsOfBet);
                }

                displayRaceBetMenu();
            }

            if (ans.equals("B")) {
                typeOfBet = "Finish Race on Podium";
                for (int i = 0; i < 20; i++) {
                    String driver = drivers[i];
                    oddsArray = getOdds(typeOfBet, driver);
                    odds = finishOnPodium(oddsArray);
                    finalOddsArray.add(odds);
                    System.out.println(drivers[i] + ": " + odds);
                }
                while (!valid) {
                    chosenDriver = Main.getInput("Which driver would you like to bet on? Alternatively type Q to quit ");
                    for (int b = 0; b < drivers.length; b++) {
                        if ((drivers[b].contains(chosenDriver)) || (chosenDriver.equals("Q"))) {
                            valid = true;
                        }
                    }
                }
                valid = false;

                if (!chosenDriver.equals("Q")) {
                    betAmount = getBetAmount(email, password);
                    for (int i = 0; i < drivers.length; i++) {
                        if (drivers[i].equals(chosenDriver)) {
                            oddsOfBet = finalOddsArray.get(i);
                        }
                    }
                    setBet(betAmount, chosenDriver, typeOfBet, email, oddsOfBet);
                }
                displayRaceBetMenu();
            }
        }
    }

    public static double raceWinner(ArrayList<Integer> oddsArray) {

        double odds = 0;
        double count = 0;
        for (int i = 0; i < oddsArray.size(); i++) {
            if (oddsArray.get(i) == 1) {
                count = count + 1;
            }
        }
        odds = count / oddsArray.size();
        odds = odds * 100;
        odds = Math.round(odds);
        odds = odds / 100;

        return odds;
    }

    public static double finishOnPodium(ArrayList<Integer> oddsArray) {
        double odds = 0;
        double count = 0;
        for (int i = 0; i < oddsArray.size(); i++) {
            if ((oddsArray.get(i) == 1) || (oddsArray.get(i) == 2) || (oddsArray.get(i) == 3)) {
                count = count + 1;
            }
        }
        odds = count / oddsArray.size();
        odds = odds * 100;
        odds = Math.round(odds);
        odds = odds / 100;

        return odds;
    }

    public static void simulateRace(int[][] startingGrid) {
        String driver = "";
        int driverNum = 0;
        ArrayList<Integer> openPlaces = new ArrayList<>();
        for (int i = 1; i < 21; i++) {
            openPlaces.add(i);
        }
        for (int i = 19; i > -1; i--) {
            driver = drivers[startingGrid[i][0]];
            driverNum = i;
            startingGrid[i][1] = getRaceResults(startingGrid, openPlaces, driver, driverNum);
        }

        orderGrid(startingGrid);
        System.out.println("\nRace Results:\n");
        for (int i = 0; i < 20; i++) {
            System.out.println(drivers[startingGrid[i][0]] + " - " + startingGrid[i][1]);
        }
    }

    public static int getRaceResults(int[][] startingGrid, ArrayList<Integer> openPlaces, String driver, int driverNum) {
        int finalPosition = 0;
        ArrayList<Integer> raceArray = new ArrayList<>();
        ArrayList<Integer> averageOfYears = new ArrayList<>();
        boolean valid = false;
        int rand = 0;
        String session = "Race";
        averageOfYears = getAverageOfYears(driver, session);

        String DatabaseLocation = "jdbc:ucanaccess://C://Users//kenny//Documents//ComputerScienceCoursework//database1.accdb";

        try (Connection con = DriverManager.getConnection(DatabaseLocation)) {

            Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);

            String sql = "SELECT * FROM " + driver;

            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                if ((rs.getInt("Year") >= 2016)) {
                    if ((rs.getInt("Year") == 2022) && (rs.getInt("Grid") == startingGrid[driverNum][1])) {
                        for (int i = 0; i < 60; i++) {
                            raceArray.add(rs.getInt("Race") - rs.getInt("Grid"));
                        }
                    }
                    if (rs.getInt("Grid") == startingGrid[driverNum][1]) {
                        for (int i = 0; i < 40; i++) {
                            raceArray.add(rs.getInt("Race") - rs.getInt("Grid"));
                        }
                    }
                    if ((rs.getInt("Year") == 2022) && (rs.getString("GrandPrix").equals(circuits[currentCircuit]))) {
                        for (int i = 0; i < 40; i++) {
                            raceArray.add(rs.getInt("Race") - rs.getInt("Grid"));
                        }
                    }
                    if ((rs.getString("GrandPrix")).equals(circuits[currentCircuit]) && (rs.getInt("Year") != 2022) && (rs.getInt("Year") > 2015)) {
                        for (int i = 0; i < 24; i++) {
                            raceArray.add(rs.getInt("Race") - rs.getInt("Grid"));
                        }
                    }
                    if ((rs.getInt("Year")) == 2022) {
                        for (int i = 0; i < 32; i++) {
                            raceArray.add(rs.getInt("Race") - rs.getInt("Grid"));
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error in the SQL class: " + e);
        }

        raceArray = fillOutRaceGrid(raceArray, startingGrid, driverNum, openPlaces);
        Random random = new Random();
        while (!valid) {
            rand = random.nextInt(raceArray.size());
            finalPosition = raceArray.get(rand);
            for (int a = 0; a < openPlaces.size(); a++) {
                if (openPlaces.get(a) == finalPosition) {
                    openPlaces.remove(a);
                    valid = true;
                    return (finalPosition);
                }
            }
        }
        return (finalPosition);
    }

    public static ArrayList<Integer> fillOutRaceGrid(ArrayList<Integer> raceArray, int[][] startingGrid, int driverNum, ArrayList<Integer> openPlaces) {
        ArrayList<Integer> newRaceArray = new ArrayList<>();
        boolean valid1 = false;
        boolean valid2 = false;
        int temp  = 0;
        for (int i = 0; i < raceArray.size(); i++) {
            temp = startingGrid[driverNum][1] - raceArray.get(i);
            if ((temp >= 1) && (temp <= 20)) {
                for (int a = 0; a < openPlaces.size(); a++) {
                    if (temp == openPlaces.get(a)) {
                        newRaceArray.add(temp);
                    }
                }
            }
        }
        for (int i = 1; i < 21; i++) {
            valid1 = false;
            valid2 = false;
            for (int length = 0; length < newRaceArray.size(); length++) {
                if (newRaceArray.get(length) == i) {
                    valid1 = true;
                }
            }
            for (int a = 0; a < openPlaces.size(); a++) {
                if (openPlaces.get(a) == i) {
                    valid2 = true;
                }
            }
            if ((valid1 == false) && (valid2 == true)) {
                newRaceArray.add(i);
            }
        }
        return(newRaceArray);
    }

    public static void paybackRaceBets(int[][] startingGrid) {
        String DatabaseLocation = "jdbc:ucanaccess://C://Users//kenny//Documents//ComputerScienceCoursework//database1.accdb";
        String raceWinner = drivers[startingGrid[0][0]];
        String podiums = drivers[startingGrid[0][0]] + drivers[startingGrid[1][0]] + drivers[startingGrid[2][0]];
        String email;
        String typeOfBet;
        int currentBalance = 0;
        int winnings = 0;
        int amountBet;
        double odds;
        int oddsForEquation;
        String chosenDriver;
        int temp = 0;
        if (betList.size() != 0) {
            for (int i = 0; i < betList.size(); i++) {
                Bets placeholder = betList.get(i);
                email = placeholder.getEmail();
                chosenDriver = placeholder.getChosenDriver();
                amountBet = placeholder.getBetAmount();
                odds = placeholder.getOddsOfBet();
                odds = odds * 100;
                oddsForEquation = (int) Math.round(odds);
                typeOfBet = placeholder.getTypeOfBet();
                if (typeOfBet.equals("Race Winner")) {
                    if (chosenDriver.equals(raceWinner)) {

                        // Finds users current balance to find new balance

                        try (Connection con = DriverManager.getConnection(DatabaseLocation)) {

                            Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);

                            String sql = "SELECT * FROM LogIn";

                            ResultSet rs = stmt.executeQuery(sql);

                            while (rs.next()) {
                                if (email.equals(rs.getString("email"))) {
                                    currentBalance = rs.getInt("Balance");
                                    temp = currentBalance + (((amountBet * 100) / oddsForEquation) * 2);
                                    winnings = temp - currentBalance;
                                    currentBalance = temp;
                                }
                            }
                        } catch (Exception e) {
                            System.out.println("Error in the SQL class: " + e);
                        }

                        // updates database with new balance

                        try (Connection con = DriverManager.getConnection(DatabaseLocation)) {

                            Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);

                            String sql = "UPDATE LogIn SET Balance = ? WHERE Email = ?";

                            PreparedStatement preparedStatement = con.prepareStatement(sql);
                            preparedStatement.setInt(1, currentBalance);
                            preparedStatement.setString(2, email);

                            int row = preparedStatement.executeUpdate();

                        } catch (Exception e) {
                            System.out.println("Error in the SQL class: " + e);
                        }

                        System.out.println("Congratulations! You won your bet on " + placeholder.getChosenDriver() + " for Race Winner.\nYou won £" + winnings + "\nYour balance is now £" + currentBalance);

                    } else {
                        System.out.println("You did not win your bet on " + placeholder.getChosenDriver() + " for Race Winner. Better luck next time.");
                    }
                }
                if (typeOfBet.equals("Finish Race on Podium")) {
                    if (betList.size() != 0) {
                        if (podiums.contains(chosenDriver)) {

                            // Finds users current balance to find new balance

                            try (Connection con = DriverManager.getConnection(DatabaseLocation)) {

                                Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);

                                String sql = "SELECT * FROM LogIn";

                                ResultSet rs = stmt.executeQuery(sql);

                                while (rs.next()) {
                                    if (email.equals(rs.getString("email"))) {
                                        currentBalance = rs.getInt("Balance");
                                        temp = currentBalance + (((amountBet * 100) / oddsForEquation) * 2);
                                        winnings = temp - currentBalance;
                                        currentBalance = temp;
                                    }
                                }
                            } catch (Exception e) {
                                    System.out.println("Error in the SQL class: " + e);
                            }

                            // updates database with new balance

                            try (Connection con = DriverManager.getConnection(DatabaseLocation)) {

                                Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);

                                String sql = "UPDATE LogIn SET Balance = ? WHERE Email = ?";

                                PreparedStatement preparedStatement = con.prepareStatement(sql);
                                preparedStatement.setInt(1, currentBalance);
                                preparedStatement.setString(2, email);

                                int row = preparedStatement.executeUpdate();

                            } catch (Exception e) {
                                System.out.println("Error in the SQL class: " + e);
                            }

                            System.out.println("Congratulations! You won your bet on " + placeholder.getChosenDriver() + " for Finish On Podium.\nYou won £" + winnings + "\nYour balance is now £" + currentBalance);

                        } else {
                            System.out.println("You did not win your bet on " + placeholder.getChosenDriver() + " for Finish On Podium. Better luck next time.");
                        }
                    }
                }
            }
        }

        betList.clear();

    }
}
