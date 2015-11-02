package com.spellcalc.agriotdesigntest;

import java.io.IOException;
import java.util.Scanner;

public class MainMenu {

    String options[];

    MainMenu(String[] options) {
        this.options = options;
    }

    /**
     * Function to display all of the options to the user, then asks for input
     */
    void displayOptions() throws IOException {

        for (int i = 0; i < options.length; i++) {
            System.out.println(i + ": " + options[i]);
        }
        getInput();
    }

    /**
     * Function to get input from the user, then does an action based off the
     * user's input
     *
     */
    int getInput() throws IOException {
        Scanner sc = new Scanner(System.in);
        System.out.println("Please enter an option: ");
        int option = sc.nextInt();
        return option;
    }
}
