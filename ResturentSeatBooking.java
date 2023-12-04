import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;
import java.util.Scanner;
import java.sql.Statement;
import java.sql.ResultSet;



public class ResturentSeatBooking {
    private static final String url = "jdbc:mysql://localhost:3306/resturent";
    private static final String username = "root";
    private static final String password = "W@2915djkq#";

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
        }catch (ClassNotFoundException e){
            System.out.println(e.getMessage());
        }

        try{
            Connection connection = DriverManager.getConnection(url, username, password);
            while(true){
                System.out.println();
                System.out.println("RESTURENT SEAT BOOKING MANAGEMENT SYSTEM");
                Scanner scanner = new Scanner(System.in);
                System.out.println("1.Booking a seat");
                System.out.println("2. View Bookings");
                System.out.println("3. Get Seat Number");
                System.out.println("4. Update Bookings");
                System.out.println("5. Delete bookings");
                System.out.println("0. Exit");
                System.out.print("Choose an option: ");
                int choice = scanner.nextInt();
                switch (choice) {
                    case 1:
                        bookingSeat(connection, scanner);
                        break;
                    case 2:
                        viewBookings(connection);
                        break;
                    case 3:
                        getSeatNumber(connection, scanner);
                        break;
                    case 4:
                        updateBooking(connection, scanner);
                        break;
                    case 5:
                        deleteBooking(connection, scanner);
                        break;
                    case 0:
                        exit();
                        scanner.close();
                        return;
                    default:
                        System.out.println("Invalid choice. Try again.");
                }
            }

        }catch (SQLException e){
            System.out.println(e.getMessage());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


    }

    private static void bookingSeat(Connection connection, Scanner scanner) {
        try {
            System.out.print("Enter guest name: ");
            String guestName = scanner.next();
            scanner.nextLine();
            System.out.print("Enter seat number: ");
            int seatNumber = scanner.nextInt();
            System.out.print("Enter contact number: ");
            String contactNumber = scanner.next();

            String sql = "INSERT INTO booking (guest_name, seat_number, contact_number) " +
                    "VALUES ('" + guestName + "', " + seatNumber + ", '" + contactNumber + "')";

            try (Statement statement = connection.createStatement()) {
                int affectedRows = statement.executeUpdate(sql);

                if (affectedRows > 0) {
                    System.out.println("Booking successful!");
                } else {
                    System.out.println("Booking failed.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void viewBookings(Connection connection) throws SQLException {
        String sql = "SELECT booking_id, guest_name, seat_number, contact_number, booking_date FROM booking";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            System.out.println("Current bookings:");
            System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");
            System.out.println("| booking ID | Guest           | seat Number   | Contact Number      | booking Date        |");
            System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");

            while (resultSet.next()) {
                int bookingId = resultSet.getInt("booking_id");
                String guestName = resultSet.getString("guest_name");
                int seatNumber = resultSet.getInt("seat_number");
                String contactNumber = resultSet.getString("contact_number");
                String bookingDate = resultSet.getTimestamp("booking_date").toString();

                // Format and display the reservation data in a table-like format
                System.out.printf("| %-14d | %-15s | %-13d | %-20s | %-19s   |\n",
                        bookingId, guestName, seatNumber, contactNumber, bookingDate);
            }

            System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");
        }
    }


    private static void getSeatNumber(Connection connection, Scanner scanner) {
        try {
            System.out.print("Enter reservation ID: ");
            int bookingId = scanner.nextInt();
            System.out.print("Enter guest name: ");
            String guestName = scanner.next();

            String sql = "SELECT seat_number FROM bookings " +
                    "WHERE bookings_id = " + bookingId +
                    " AND guest_name = '" + guestName + "'";

            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(sql)) {

                if (resultSet.next()) {
                    int seatNumber = resultSet.getInt("seat_number");
                    System.out.println("seat number for booking ID " + bookingId +
                            " and Guest " + guestName + " is: " + seatNumber);
                } else {
                    System.out.println("booking not found for the given ID and guest name.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private static void updateBooking(Connection connection, Scanner scanner) {
        try {
            System.out.print("Enter booking ID to update: ");
            int bookingId = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character

            if (!bookingExists(connection, bookingId)) {
                System.out.println("booking not found for the given ID.");
                return;
            }

            System.out.print("Enter new guest name: ");
            String newGuestName = scanner.nextLine();
            System.out.print("Enter new seat number: ");
            int newseatNumber = scanner.nextInt();
            System.out.print("Enter new contact number: ");
            String newContactNumber = scanner.next();

            String sql = "UPDATE booking SET guest_name = '" + newGuestName + "', " +
                    "room_number = " + newseatNumber + ", " +
                    "contact_number = '" + newContactNumber + "' " +
                    "WHERE booking_id = " + bookingId;

            try (Statement statement = connection.createStatement()) {
                int affectedRows = statement.executeUpdate(sql);

                if (affectedRows > 0) {
                    System.out.println("booking updated successfully!");
                } else {
                    System.out.println("booking update failed.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void deleteBooking(Connection connection, Scanner scanner) {
        try {
            System.out.print("Enter booking ID to delete: ");
            int bookingId = scanner.nextInt();

            if (!bookingExists(connection, bookingId)) {
                System.out.println("booking not found for the given ID.");
                return;
            }

            String sql = "DELETE FROM booking WHERE booking_id = " + bookingId;

            try (Statement statement = connection.createStatement()) {
                int affectedRows = statement.executeUpdate(sql);

                if (affectedRows > 0) {
                    System.out.println("booking deleted successfully!");
                } else {
                    System.out.println("booking deletion failed.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static boolean bookingExists(Connection connection, int bookingId) {
        try {
            String sql = "SELECT booking_id FROM booking WHERE booking_id = " + bookingId;

            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(sql)) {

                return resultSet.next(); // If there's a result, the reservation exists
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Handle database errors as needed
        }
    }


    public static void exit() throws InterruptedException {
        System.out.print("Exiting System");
        int i = 5;
        while(i!=0){
            System.out.print(".");
            Thread.sleep(1000);
            i--;
        }
        System.out.println();
        System.out.println("ThankYou For Using Resturent Seat booking System!!!");
    }
}