import java.util.HashMap;
import java.util.Map;

public class Main {

    public static void main(String[] args) {

        // UC1 - Welcome Message
        System.out.println("Welcome to BookMyStayApp!");
        System.out.println("Your simple hotel booking system\n");

        // UC3 - Centralized Inventory
        RoomInventory inventory = new RoomInventory();

        // UC4 - Guest searches available rooms
        SearchService searchService = new SearchService(inventory);

        System.out.println("Available Rooms for Booking:\n");
        searchService.displayAvailableRooms();
    }
}

/*
Room Domain Model
Represents details of each room type
*/
class Room {

    String type;
    double price;
    String amenities;

    public Room(String type, double price, String amenities) {
        this.type = type;
        this.price = price;
        this.amenities = amenities;
    }
}

/*
UC3 - Inventory Actor
Centralized availability management
*/
class RoomInventory {

    private HashMap<String, Integer> roomAvailability;

    public RoomInventory() {
        roomAvailability = new HashMap<>();

        roomAvailability.put("Single Room", 5);
        roomAvailability.put("Double Room", 4);
        roomAvailability.put("Deluxe Room", 0);
        roomAvailability.put("Suite Room", 2);
    }

    public int getAvailability(String roomType) {
        return roomAvailability.getOrDefault(roomType, 0);
    }

    public Map<String, Integer> getAllAvailability() {
        return roomAvailability;
    }
}

/*
UC4 - Search Service
Handles read-only search logic
*/
class SearchService {

    private RoomInventory inventory;

    private HashMap<String, Room> roomCatalog;

    public SearchService(RoomInventory inventory) {
        this.inventory = inventory;

        roomCatalog = new HashMap<>();

        roomCatalog.put("Single Room",
                new Room("Single Room", 2500, "1 Bed, Free WiFi"));

        roomCatalog.put("Double Room",
                new Room("Double Room", 4000, "2 Beds, Free WiFi, TV"));

        roomCatalog.put("Deluxe Room",
                new Room("Deluxe Room", 6000, "King Bed, Sea View, WiFi"));

        roomCatalog.put("Suite Room",
                new Room("Suite Room", 9000, "Luxury Suite, Living Area, WiFi"));
    }

    public void displayAvailableRooms() {

        for (String roomType : inventory.getAllAvailability().keySet()) {

            int available = inventory.getAvailability(roomType);

            // Filter unavailable rooms
            if (available > 0) {

                Room room = roomCatalog.get(roomType);

                System.out.println("Room Type: " + room.type);
                System.out.println("Price: ₹" + room.price);
                System.out.println("Amenities: " + room.amenities);
                System.out.println("Available: " + available);
                System.out.println("---------------------------");
            }
        }
    }
}