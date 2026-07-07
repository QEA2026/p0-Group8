package com.revature.expensemanager.cli;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class InputVal {

    private static final Scanner scanner = new Scanner(System.in);

    public static int readPositiveInt(String prompt) {

        while (true) {

            System.out.print(prompt);

            String input = scanner.nextLine().trim();

            try {

                int value = Integer.parseInt(input);

                if (value > 0) {
                    return value;
                }

                System.out.println("Please enter a number greater than 0.");

            } catch (NumberFormatException e) {

                System.out.println("Please enter a valid number.");

            }
        }
    }

    public static String readNonEmptyString(String prompt) {

        while (true) {

            System.out.print(prompt);

            String input = scanner.nextLine().trim();

            if (!input.isBlank()) {
                return input;
            }

            System.out.println("Input cannot be empty.");
        }
    }

    public static int readMenuChoice(int min, int max) {

        while (true) {

            System.out.print("Choose: ");

            String input = scanner.nextLine().trim();

            try {

                int choice = Integer.parseInt(input);

                if (choice >= min && choice <= max) {
                    return choice;
                }

            } catch (NumberFormatException ignored) {
            }

            System.out.println("Please enter a number between "
                    + min + " and " + max + ".");
        }
    }

    public static LocalDate readDate(String prompt) {

        while (true) {

            System.out.print(prompt);

            String input = scanner.nextLine().trim();

            try {

                return LocalDate.parse(input);

            } catch (DateTimeParseException e) {

                System.out.println("Please use YYYY-MM-DD.");

            }
        }
    }
}