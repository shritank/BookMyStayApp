public class Main {

    public static void main(String[] args) {

        // UC1 - Welcome Message
        System.out.println("Welcome to BookMyStayApp!");
        System.out.println("Your simple hotel booking system\n");

        // UC2 - Display Available Rooms
        System.out.println("Available Rooms:");

        String[] rooms = {
                "Room 101 - Single Room",
                "Room 102 - Double Room",
                "Room 201 - Deluxe Room",
                "Room 202 - Suite Room"
        };

        for (String room : rooms) {
            System.out.println(room);
        }
    }
}