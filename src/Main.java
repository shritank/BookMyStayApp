import java.util.*;

public class Main {

    public static void main(String[] args) {

        // UC1 - Welcome Message
        System.out.println("Welcome to BookMyStayApp!");
        System.out.println("Your simple hotel booking system\n");

        // UC3 - Inventory
        RoomInventory inventory = new RoomInventory();

        // UC4 - Search Service
        SearchService searchService = new SearchService(inventory);

        System.out.println("Available Rooms:\n");
        searchService.displayAvailableRooms();

        // UC5 - Booking Request Queue
        BookingQueue bookingQueue = new BookingQueue();

        System.out.println("\nGuests submitting booking requests...\n");

        bookingQueue.addRequest(new Reservation("Arun", "Single Room"));
        bookingQueue.addRequest(new Reservation("Meena", "Double Room"));
        bookingQueue.addRequest(new Reservation("Rahul", "Suite Room"));

        bookingQueue.displayQueue();
    }
}

/*
Room Domain Model
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
UC3 - Centralized Inventory
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
(Read-only access)
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
                new Room("Double Room", 4000, "2 Beds, WiFi, TV"));

        roomCatalog.put("Deluxe Room",
                new Room("Deluxe Room", 6000, "King Bed, Sea View"));

        roomCatalog.put("Suite Room",
                new Room("Suite Room", 9000, "Luxury Suite, Living Area"));
    }

    public void displayAvailableRooms() {

        for (String roomType : inventory.getAllAvailability().keySet()) {

            int available = inventory.getAvailability(roomType);

            if (available > 0) {

                Room room = roomCatalog.get(roomType);

                System.out.println("Room: " + room.type);
                System.out.println("Price: ₹" + room.price);
                System.out.println("Amenities: " + room.amenities);
                System.out.println("Available: " + available);
                System.out.println("----------------------");
            }
        }
    }
}

/*
UC5 - Reservation Actor
Represents guest booking intent
*/
class Reservation {

    String guestName;
    String roomType;

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }
}

/*
UC5 - Booking Request Queue
Stores booking requests in FIFO order
*/
class BookingQueue {

    private Queue<Reservation> bookingQueue;

    public BookingQueue() {
        bookingQueue = new LinkedList<>();
    }

    public void addRequest(Reservation reservation) {

        bookingQueue.add(reservation);

        System.out.println("Booking request added for "
                + reservation.guestName
                + " (" + reservation.roomType + ")");
    }

    public void displayQueue() {

        System.out.println("\nCurrent Booking Queue:");

        for (Reservation r : bookingQueue) {
            System.out.println(r.guestName + " requested " + r.roomType);
        }
    }
}