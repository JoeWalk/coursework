package com.company;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
	    String email = "";
        email = Admin.getEmail(email);
        String password = "";
        password = Admin.getPassword(password);
    }

    public static String getInput(String prompt) {
        System.out.println(prompt);
        Scanner input = new Scanner(System.in);
        return input.nextLine();
    }
}
