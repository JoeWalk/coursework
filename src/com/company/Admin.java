package com.company;

public class Admin {

    public static String getEmail(String email) {
        boolean valid = false;
        while(!valid){
            email = Main.getInput("Please enter the email you would like to use for your account");
            if (email.contains("@") && email.contains(".")) {
                valid = true;
            }
            else{
                System.out.println("Please enter a valid email");
            }
        }
        return(email);
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
}
