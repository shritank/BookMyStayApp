import java.util.*;

public class Main {

    public static void main(String[] args) {

        System.out.println("Welcome to BookMyStayApp!\n");

        // Inventory
        RoomInventory inventory = new RoomInventory();

        // Search
        SearchService searchService = new SearchService(inventory);
        searchService.displayAvailableRooms();

        // Booking Queue
        BookingQueue bookingQueue = new BookingQueue();

        bookingQueue.addRequest(new Reservation("Arun", "Single Room"));
        bookingQueue.addRequest(new Reservation("Meena", "Double Room"));
        bookingQueue.addRequest(new Reservation("Rahul", "Suite Room"));

        // Booking Service (UC6)
        BookingService bookingService =
                new BookingService(bookingQueue, inventory);

        System.out.println("\nProcessing Booking Requests...\n");

        bookingService.processBookings();
    }
}

/* ---------------- ROOM DOMAIN ---------------- */

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

/* ---------------- INVENTORY SERVICE ---------------- */

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

    public void decrementRoom(String roomType) {

        int count = getAvailability(roomType);

        if (count > 0) {
            roomAvailability.put(roomType, count - 1);
        }
    }

    public Map<String, Integer> getAllAvailability() {
        return roomAvailability;
    }
}

/* ---------------- SEARCH SERVICE ---------------- */

class SearchService {

    private RoomInventory inventory;
    private HashMap<String, Room> roomCatalog;

    public SearchService(RoomInventory inventory) {

        this.inventory = inventory;

        roomCatalog = new HashMap<>();

        roomCatalog.put("Single Room",
                new Room("Single Room", 2500, "1 Bed, WiFi"));

        roomCatalog.put("Double Room",
                new Room("Double Room", 4000, "2 Beds, WiFi, TV"));

        roomCatalog.put("Deluxe Room",
                new Room("Deluxe Room", 6000, "King Bed, Sea View"));

        roomCatalog.put("Suite Room",
                new Room("Suite Room", 9000, "Luxury Suite"));
    }

    public void displayAvailableRooms() {

        System.out.println("Available Rooms:\n");

        for (String type : inventory.getAllAvailability().keySet()) {

            int available = inventory.getAvailability(type);

            if (available > 0) {

                Room room = roomCatalog.get(type);

                System.out.println("Room: " + room.type);
                System.out.println("Price: ₹" + room.price);
                System.out.println("Amenities: " + room.amenities);
                System.out.println("Available: " + available);
                System.out.println("---------------------");
            }
        }
    }
}

/* ---------------- RESERVATION ---------------- */

class Reservation {

    String guestName;
    String roomType;

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }
}

/* ---------------- BOOKING QUEUE ---------------- */

class BookingQueue {

    private Queue<Reservation> bookingQueue;

    public BookingQueue() {
        bookingQueue = new LinkedList<>();
    }

    public void addRequest(Reservation r) {
        bookingQueue.add(r);
    }

    public Reservation getNextRequest() {
        return bookingQueue.poll();
    }

    public boolean hasRequests() {
        return !bookingQueue.isEmpty();
    }
}

/* ---------------- BOOKING SERVICE (UC6) ---------------- */

class BookingService {

    private BookingQueue queue;
    private RoomInventory inventory;

    // Track allocated room IDs
    private Set<String> allocatedRoomIds;

    // Track allocations by room type
    private HashMap<String, Set<String>> roomAllocations;

    public BookingService(BookingQueue queue, RoomInventory inventory) {

        this.queue = queue;
        this.inventory = inventory;

        allocatedRoomIds = new HashSet<>();
        roomAllocations = new HashMap<>();
    }

    public void processBookings() {

        while (queue.hasRequests()) {

            Reservation request = queue.getNextRequest();

            String roomType = request.roomType;

            int available = inventory.getAvailability(roomType);

            if (available > 0) {

                String roomId = generateRoomId(roomType);

                allocatedRoomIds.add(roomId);

                roomAllocations
                        .computeIfAbsent(roomType, k -> new HashSet<>())
                        .add(roomId);

                inventory.decrementRoom(roomType);

                System.out.println("Reservation Confirmed!");
                System.out.println("Guest: " + request.guestName);
                System.out.println("Room Type: " + roomType);
                System.out.println("Room ID: " + roomId);
                System.out.println("---------------------");

            } else {

                System.out.println("Reservation Failed for "
                        + request.guestName +
                        " (No rooms available)");
            }
        }
    }

    private String generateRoomId(String roomType) {

        String prefix = roomType.replace(" ", "").substring(0, 2).toUpperCase();

        String id;

        do {
            id = prefix + (int)(Math.random() * 1000);
        } while (allocatedRoomIds.contains(id));

        return id;
    }
}