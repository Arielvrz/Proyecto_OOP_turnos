package com.queuemanagementsystem.ui;

import java.util.Scanner;

/**
 * Base interface for menu classes with common functionality.
 */
public interface Menu {
    /**
     * Displays the menu options
     */
    void displayMenu();

    /**
     * Processes user input and executes the corresponding action
     *
     * @param option The option selected by the user
     * @return true if the menu should continue, false if it should exit
     */
    boolean processOption(String option);

    /**
     * Starts the menu interaction loop
     *
     * @param scanner The scanner for reading user input
     */
    default void start(Scanner scanner) {
        boolean continueMenu = true;
        while (continueMenu) {
            displayMenu();
            System.out.print("Select an option: ");
            String option = scanner.nextLine().trim();
            continueMenu = processOption(option);
        }
    }
}