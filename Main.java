package com.wipro.restaurant;

import com.wipro.restaurant.controller.ReservationController;
import com.wipro.restaurant.model.ReservationRequest;
import com.wipro.restaurant.service.ReservationService;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/**
 * Entry point for the terminal-based Restaurant Table Reservation System.
 */
public class Main {

    public static void main(String[] args) {
        ReservationService reservationService = new ReservationService();
        ReservationController controller = new ReservationController(reservationService);
        Scanner scanner = new Scanner(System.in);

        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    handleBooking(scanner, controller);
                    break;
                case "2":
                    handleAvailability(scanner, controller);
                    break;
                case "3":
                    runSampleTestCases(controller);
                    break;
                case "4":
                    running = false;
                    System.out.println("Exiting. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Please enter 1, 2, 3, or 4.");
            }
        }
        scanner.close();
    }

    private static void printMenu() {
        System.out.println("\n===== Restaurant Table Reservation System =====");
        System.out.println("1. Book a Table        (POST /reservations)");
        System.out.println("2. Check Availability  (GET /reservations/available)");
        System.out.println("3. Run Sample Test Cases from spec");
        System.out.println("4. Exit");
        System.out.print("Enter choice: ");
    }

    private static void handleBooking(Scanner scanner, ReservationController controller) {
        try {
            System.out.print("Customer Name: ");
            String name = scanner.nextLine().trim();

            System.out.print("Party Size: ");
            int partySize = Integer.parseInt(scanner.nextLine().trim());

            System.out.print("Reservation Time (HH:mm, 24-hour, e.g. 20:00): ");
            LocalTime time = LocalTime.parse(scanner.nextLine().trim());

            System.out.print("Base Cost (Rs.): ");
            double baseCost = Double.parseDouble(scanner.nextLine().trim());

            ReservationRequest request = new ReservationRequest(name, partySize, time);
            String result = controller.postReservations(request, baseCost);
            System.out.println(result);
        } catch (NumberFormatException e) {
            System.out.println("Invalid number entered. Please try again.");
        } catch (DateTimeParseException e) {
            System.out.println("Invalid time format. Please use HH:mm (24-hour).");
        }
    }

    private static void handleAvailability(Scanner scanner, ReservationController controller) {
        System.out.print("Time (HH:mm, 24-hour): ");
        String time = scanner.nextLine().trim();

        System.out.print("Party Size: ");
        int partySize;
        try {
            partySize = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid party size.");
            return;
        }

        System.out.println(controller.getAvailable(time, partySize));
    }

    /**
     * Runs the exact test cases described in the spec's Test Case table:
     *   1. Book table for 4, time 8:00 PM (peak), base cost Rs.1000
     *        -> Reservation confirmed, Final Cost: Rs.1150 (15% surcharge)
     *   2. All tables for party size 4 occupied at requested time
     *        -> 409 Conflict: TableUnavailableException
     *   3. Same customer books 8:00 PM slot twice
     *        -> 409 Conflict: DoubleBookingException
     */
    private static void runSampleTestCases(ReservationController controller) {
        System.out.println("\n--- Test Case 1: Book table for 4, 8:00 PM (peak), base cost Rs.1000 ---");
        System.out.println("Expected: Reservation confirmed, Final Cost: Rs.1150 (15% surcharge)");
        ReservationRequest req1 = new ReservationRequest("Ravi Kumar", 4, LocalTime.of(20, 0));
        System.out.println("Actual  : " + controller.postReservations(req1, 1000));

        System.out.println("\n--- Test Case 2: Fill remaining party-size-4 tables, then try one more ---");
        System.out.println("Expected: 409 Conflict: TableUnavailableException");
        String[] fillerCustomers = {"Anita Rao", "Suresh Nair", "Kiran Shah", "Meena Das"};
        for (String customer : fillerCustomers) {
            controller.postReservations(new ReservationRequest(customer, 4, LocalTime.of(20, 0)), 1000);
        }
        System.out.println("Actual  : " + controller.postReservations(
                new ReservationRequest("Extra Customer", 4, LocalTime.of(20, 0)), 1000));

        System.out.println("\n--- Test Case 3: Same customer (Ravi Kumar) books 8:00 PM slot twice ---");
        System.out.println("Expected: 409 Conflict: DoubleBookingException");
        System.out.println("Actual  : " + controller.postReservations(
                new ReservationRequest("Ravi Kumar", 2, LocalTime.of(20, 0)), 1000));

        System.out.println("\n--- Bonus: Check availability for party size 4 at 20:00 after test cases ---");
        System.out.println(controller.getAvailable("20:00", 4));
    }
}
