import java.util.*;

public class Main {

    public static void main(String[] args) {

        System.out.println("Welcome to BookMyStayApp!\n");

        RoomInventory inventory = new RoomInventory();

        SearchService searchService = new SearchService(inventory);
        searchService.displayAvailableRooms();

        BookingQueue queue = new BookingQueue();

        queue.addRequest(new Reservation("Arun", "Single Room"));
        queue.addRequest(new Reservation("Meena", "Double Room"));

        BookingService bookingService = new BookingService(queue, inventory);

        System.out.println("\nProcessing Booking Requests...\n");

        bookingService.processBookings();

        // UC7 - Add-on services
        AddOnServiceManager addOnManager = new AddOnServiceManager();

        System.out.println("\nAdding optional services to reservations...\n");

        addOnManager.addService("RES1", new AddOnService("Breakfast", 500));
        addOnManager.addService("RES1", new AddOnService("Airport Pickup", 1200));

        addOnManager.addService("RES2", new AddOnService("Spa Access", 1500));

        addOnManager.displayServices("RES1");
        addOnManager.displayServices("RES2");
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

/* ---------------- INVENTORY ---------------- */

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

/* ---------------- BOOKING SERVICE ---------------- */

class BookingService {

    private BookingQueue queue;
    private RoomInventory inventory;

    private Set<String> allocatedRoomIds;
    private HashMap<String, Set<String>> roomAllocations;

    private int reservationCounter = 1;

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

                String reservationId = "RES" + reservationCounter++;

                System.out.println("Reservation Confirmed!");
                System.out.println("Reservation ID: " + reservationId);
                System.out.println("Guest: " + request.guestName);
                System.out.println("Room ID: " + roomId);
                System.out.println("----------------------");

            } else {

                System.out.println("Reservation Failed for "
                        + request.guestName);
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

/* ---------------- ADD-ON SERVICE ---------------- */

class AddOnService {

    String name;
    double cost;

    public AddOnService(String name, double cost) {
        this.name = name;
        this.cost = cost;
    }
}

/* ---------------- ADD-ON SERVICE MANAGER ---------------- */

class AddOnServiceManager {

    private Map<String, List<AddOnService>> reservationServices;

    public AddOnServiceManager() {
        reservationServices = new HashMap<>();
    }

    public void addService(String reservationId, AddOnService service) {

        reservationServices
                .computeIfAbsent(reservationId, k -> new ArrayList<>())
                .add(service);

        System.out.println(service.name +
                " added to reservation " + reservationId);
    }

    public void displayServices(String reservationId) {

        List<AddOnService> services = reservationServices.get(reservationId);

        if (services == null) {
            System.out.println("No services for " + reservationId);
            return;
        }

        double total = 0;

        System.out.println("\nServices for " + reservationId);

        for (AddOnService s : services) {
            System.out.println("- " + s.name + " : ₹" + s.cost);
            total += s.cost;
        }

        System.out.println("Total Add-On Cost: ₹" + total);
    }
}