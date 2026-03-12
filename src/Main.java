import java.util.HashMap;
import java.util.Map;

public class Main
{

    public static void main(String[] args) {

        // UC1 - Welcome Message
        System.out.println("Welcome to BookMyStayApp!");
        System.out.println("Your simple hotel booking system\n");

        // UC3 - Centralized Room Inventory
        RoomInventory inventory = new RoomInventory();

        System.out.println("Current Room Availability:");
        inventory.displayInventory();
    }
}

/*
 UC3 - RoomInventory Actor
 Responsible for centralized management of room availability
*/
class RoomInventory {

    private HashMap<String, Integer> roomAvailability;

    // Constructor initializes room inventory
    public RoomInventory() {
        roomAvailability = new HashMap<>();

        roomAvailability.put("Single Room", 5);
        roomAvailability.put("Double Room", 4);
        roomAvailability.put("Deluxe Room", 3);
        roomAvailability.put("Suite Room", 2);
    }

    // Retrieve availability
    public int getAvailability(String roomType) {
        return roomAvailability.getOrDefault(roomType, 0);
    }

    // Update availability
    public void updateAvailability(String roomType, int newCount) {
        roomAvailability.put(roomType, newCount);
    }

    // Display inventory
    public void displayInventory() {
        for (Map.Entry<String, Integer> entry : roomAvailability.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue() + " rooms available");
        }
    }
}