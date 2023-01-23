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

                betAmount = getBetAmount(email, password);
                for (int i = 0; i < drivers.length; i++) {
                    if (drivers[i].equals(chosenDriver)) {
                        oddsOfBet = finalOddsArray.get(i);
                    }
                }
                setBet(betAmount, chosenDriver, typeOfBet, email, oddsOfBet);
                displayBetMenu();
            }

            if (ans.equals("B")) {
                typeOfBet = "Race Winner";
                for (int i = 0; i < 20; i++) {
                    String driver = drivers[i];
                    oddsArray = getOdds(typeOfBet, driver);
                }
            }
            if (ans.equals("C")) {
                typeOfBet = "Finish Race on Podium";
                for (int i = 0; i < 20; i++) {
                    String driver = drivers[i];
                    oddsArray = getOdds(typeOfBet, driver);
                }
            }
        }
        simulateQualifying();
    }

    public static void displayBetMenu() {

        System.out.println("What would you like to bet on before qualifying?");
        System.out.println("");
        System.out.println("A) Pole Position");
        System.out.println("B) Race Winner");
        System.out.println("C) Finish Race on Podium");
        System.out.println("Q) Do Not Bet, Simulate Qualifying");
    }

    public static ArrayList<Integer> getOdds(String typeOfBet, String driver) {

        ArrayList<Integer> oddsArray = new ArrayList<>();
        String year = "2022";
        ArrayList<Integer> averageOfYears = new ArrayList<>();
        averageOfYears = getAverageOfYears(driver);

        String DatabaseLocation = "jdbc:ucanaccess://X://My Documents//Computer Science//Coursework//database1.accdb";

        try (Connection con = DriverManager.getConnection(DatabaseLocation)) {

            Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);

            String sql = "SELECT Year, GrandPrix, Grid FROM " + driver;

            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                if ((rs.getInt("Year") >= 2016)) {
                    if ((rs.getInt("Year") == 2022) && (rs.getString("GrandPrix").equals(circuits[currentCircuit]))) {
                        for (int i = 0; i < 10; i++) {
                            oddsArray.add(rs.getInt("Grid") - averageOfYears.get(0));
                        }
                    }
                    if ((rs.getString("GrandPrix")).equals(circuits[currentCircuit]) && (rs.getInt("Year") != 2022) && (rs.getInt("Year") > 2015)) {
                        for (int i = 0; i < 6; i++) {
                            oddsArray.add(rs.getInt("Grid") - averageOfYears.get(2022 - (rs.getInt("Year"))));
                        }
                    }
                    if ((rs.getInt("Year")) == 2022) {
                        for (int i = 0; i < 8; i++) {
                            oddsArray.add(rs.getInt("Grid") - averageOfYears.get(0));
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

    public static ArrayList<Integer> getAverageOfYears(String driver) {

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
                        averageArray.add(rs.getInt("Grid"));
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

        String DatabaseLocation = "jdbc:ucanaccess://X://My Documents//Computer Science//Coursework//database1.accdb";

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
        }

        catch (Exception e) {
            System.out.println("Error in the SQL class: " + e);
        }

        try (Connection con = DriverManager.getConnection(DatabaseLocation)) {

            Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);

            String sql = "UPDATE LogIn SET Balance = ? WHERE Email = ?";

            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setInt(1, currentBalance - amountBet);
            preparedStatement.setString(2, email);

            int row = preparedStatement.executeUpdate();

        }

        catch (Exception e) {
            System.out.println("Error in the SQL class: " + e);
        }

        return(amountBet);
    }

    public static void setBet (int betAmount, String chosenDriver, String typeOfBet, String email, double oddsOfBet) {
        Bets bet = new Bets(email, chosenDriver, typeOfBet, betAmount, oddsOfBet);
        System.out.println(bet.toString());
        betList.add(bet);
    }

    public static void simulateQualifying() {

        //startingGrid need to be a 2D array so that the bubble sort can see what averages to find
        Random rand = new Random ();
        String driver = "";
        boolean valid = false;
        int random = 0;
        int count = 0;
        int[][] startingGrid = {{0,0}, {1,0}, {2,0},{3,0},{4,0},{5,0},{6,0},{7,0},{8,0},{9,0},{10,0},{11,0},{12,0},{13,0},{14,0},{15,0},{16,0},{17,0},{18,0},{19,0}};
        ArrayList<Integer> driversToGo = new ArrayList<>();
        for (int i = 0; i < 20; i++){
            driversToGo.add(i);
        }
        for (int i = 0; i < 20; i++){
            random = rand.nextInt(20);
            for (int a = 0; a < driversToGo.size(); a++){
                if (driversToGo.get(a) == random){
                    valid = true;
                }
            }
            if (valid == true){
                startingGrid[count][1] = (getQualifyingResults(drivers[random]));
                count = count + 1;
            }
            valid = false;
            driversToGo.remove(random);
        }

        for (int i = 0; i < startingGrid.length; i++) {
            System.out.println(startingGrid[i][1]);
        }
    }

    public static int getQualifyingResults (String driver) {

        ArrayList<Integer> qualiArray = new ArrayList<>();
        ArrayList<Integer> averageOfYears = new ArrayList<>();
        boolean valid = false;
        int finalGridPlace = 0;
        int [] openPlaces = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20};
        averageOfYears = getAverageOfYears(driver);

        String DatabaseLocation = "jdbc:ucanaccess://X://My Documents//Computer Science//Coursework//database1.accdb";

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
        }

        catch (Exception e) {
            System.out.println("Error in the SQL class: " + e);
        }

        qualiArray = fillOutGrid(qualiArray, averageOfYears);
        Random random = new Random();
        int rand = random.nextInt(qualiArray.size());
        finalGridPlace = qualiArray.get(rand);
        for (int a = 0; a < openPlaces.length; a++){
            if (openPlaces[a] == finalGridPlace){
                valid = true;
            }
        }
        if (valid == true){
            //Need to check if finalGridPlace is in openPlaces and remove that place from openPlaces if it is
        }
        valid = false;

        return (finalGridPlace);
    }

    public static ArrayList<Integer> fillOutGrid (ArrayList<Integer> qualiArray, ArrayList<Integer> averageOfYear){
        int odds = 0;
        int fill = 0;
        boolean valid = false;
        ArrayList<Integer> newQualiArray = new ArrayList<>();
        for (int length = 0; length < qualiArray.size(); length++) {
            fill = qualiArray.get(length) + averageOfYear.get(0);
            newQualiArray.add(fill);
        }
        for (int i = 1; i < 21; i++) {
            valid = false;
            for (int length = 0; length < newQualiArray.size(); length++) {
                if (newQualiArray.get(length) == i) {
                    valid = true;
                }
            }
            if (valid == false) {
                newQualiArray.add(i);
            }
        }

        return newQualiArray;
    }

    public static int[][] orderGrid (int[][] startingGrid) {
        ArrayList<Integer> tempListDrivers = new ArrayList<>();
        ArrayList<Integer> tempListGrid = new ArrayList<>();
        int temp = 0;
        for (int i = 0; i < 21; i++) {
            for (int a = 1; a < 20; a++) {
                if(startingGrid[a-1][1] > startingGrid[a][1]) {
                    temp = startingGrid[a-1][1];
                    startingGrid[a-1][1] = startingGrid[a][1];
                    startingGrid[a][1] = temp;
                    temp = startingGrid[a-1][0];
                    startingGrid[a-1][0] = startingGrid[a][0];
                    startingGrid[a][0] = temp;

                }
            }
        }

        for (int i = 0; i < 21; i++) {
            for (int a = 0; a < 20; a++) {
                if (startingGrid[a][1] == i){
                    tempListDrivers.add(startingGrid[a][0]);
                    tempListGrid.add(startingGrid[a][1]);
                }
            }
            tempListGrid = orderTempLists(tempListGrid, tempListDrivers);
            for (int c = 0; c < 20; c++) {
                if (startingGrid[c][1] == i) {
                    for (int d = 0; d < tempListGrid.size() - 1; d++) {
                        startingGrid[c+d-1][1] = tempListGrid.get(d);
                    }
                }
            }
            tempListDrivers.clear();
            tempListGrid.clear();
        }
        return (startingGrid);
    }

    public static ArrayList<Integer> orderTempLists (ArrayList<Integer> tempListGrid, ArrayList<Integer> tempListDrivers) {
        int temp;
        ArrayList<Integer> temp1 = new ArrayList<>();
        ArrayList<Integer> temp2 = new ArrayList<>();
        if (tempListGrid.size() > 1){
            for (int i = 0; i < tempListGrid.size(); i++) {
                for (int a = 0; a < tempListGrid.size() - 1; i++) {
                    temp1 = getAverageOfYears(drivers[tempListDrivers.get(a)]);
                    temp2 = getAverageOfYears(drivers[tempListDrivers.get(a+1)]);
                    if (temp2.get(0) < temp1.get(0)){
                        temp = tempListGrid.get(a) + 1;
                        tempListGrid.set(a,tempListGrid.get(a+1));
                        tempListGrid.set(a+1,temp);
                    }
                }
            }
        }
        return tempListGrid;
    }
}
