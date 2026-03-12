import java.util.*;

public class Main {

    public static void main(String[] args) {

        System.out.println("Welcome to BookMyStayApp!\n");

        RoomInventory inventory = new RoomInventory();

        SearchService searchService = new SearchService(inventory);
        searchService.displayAvailableRooms();

        BookingQueue queue = new BookingQueue();

        // Add requests using ReservationRequest (guestName, roomType)
        queue.addRequest(new ReservationRequest("Arun", "Single Room"));
        queue.addRequest(new ReservationRequest("Meena", "Double Room"));
        queue.addRequest(new ReservationRequest("Rahul", "Suite Room"));

        BookingHistory bookingHistory = new BookingHistory();

        BookingService bookingService = new BookingService(queue, inventory, bookingHistory);

        System.out.println("\nProcessing Booking Requests...\n");

        bookingService.processBookings();

        BookingReportService reportService = new BookingReportService(bookingHistory);

        System.out.println("\n--- Booking History Report ---\n");
        reportService.printAllBookings();

        System.out.println("\n--- Summary Report ---\n");
        reportService.printSummary();
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

/* ---------------- RESERVATION REQUEST ---------------- */

class ReservationRequest {

    String guestName;
    String roomType;

    public ReservationRequest(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }
}

/* ---------------- RESERVATION ---------------- */

class Reservation {

    String reservationId;
    String guestName;
    String roomType;
    String roomId;

    public Reservation(String reservationId, String guestName, String roomType, String roomId) {
        this.reservationId = reservationId;
        this.guestName = guestName;
        this.roomType = roomType;
        this.roomId = roomId;
    }
}

/* ---------------- BOOKING QUEUE ---------------- */

class BookingQueue {

    private Queue<ReservationRequest> bookingQueue;

    public BookingQueue() {
        bookingQueue = new LinkedList<>();
    }

    public void addRequest(ReservationRequest r) {
        bookingQueue.add(r);
    }

    public ReservationRequest getNextRequest() {
        return bookingQueue.poll();
    }

    public boolean hasRequests() {
        return !bookingQueue.isEmpty();
    }
}

/* ---------------- BOOKING HISTORY ---------------- */

class BookingHistory {

    private List<Reservation> confirmedBookings;

    public BookingHistory() {
        confirmedBookings = new ArrayList<>();
    }

    public void addReservation(Reservation reservation) {
        confirmedBookings.add(reservation);
    }

    public List<Reservation> getAllReservations() {
        return Collections.unmodifiableList(confirmedBookings);
    }
}

/* ---------------- BOOKING SERVICE ---------------- */

class BookingService {

    private BookingQueue queue;
    private RoomInventory inventory;
    private BookingHistory history;

    private Set<String> allocatedRoomIds;
    private HashMap<String, Set<String>> roomAllocations;

    private int reservationCounter = 1;

    public BookingService(BookingQueue queue, RoomInventory inventory, BookingHistory history) {

        this.queue = queue;
        this.inventory = inventory;
        this.history = history;

        allocatedRoomIds = new HashSet<>();
        roomAllocations = new HashMap<>();
    }

    public void processBookings() {

        while (queue.hasRequests()) {

            ReservationRequest request = queue.getNextRequest();

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

                Reservation reservation = new Reservation(
                        reservationId,
                        request.guestName,
                        roomType,
                        roomId);

                history.addReservation(reservation);

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
            id = prefix + (int) (Math.random() * 1000);
        } while (allocatedRoomIds.contains(id));

        return id;
    }
}

/* ---------------- BOOKING REPORT SERVICE ---------------- */

class BookingReportService {

    private BookingHistory history;

    public BookingReportService(BookingHistory history) {
        this.history = history;
    }

    public void printAllBookings() {
        List<Reservation> reservations = history.getAllReservations();

        if (reservations.isEmpty()) {
            System.out.println("No bookings found.");
            return;
        }

        for (Reservation r : reservations) {
            System.out.println("Reservation ID: " + r.reservationId);
            System.out.println("Guest: " + r.guestName);
            System.out.println("Room Type: " + r.roomType);
            System.out.println("Room ID: " + r.roomId);
            System.out.println("---------------------");
        }
    }

    public void printSummary() {
        List<Reservation> reservations = history.getAllReservations();

        if (reservations.isEmpty()) {
            System.out.println("No bookings to summarize.");
            return;
        }

        Map<String, Integer> countByRoomType = new HashMap<>();

        for (Reservation r : reservations) {
            countByRoomType.put(
                    r.roomType,
                    countByRoomType.getOrDefault(r.roomType, 0) + 1);
        }

        System.out.println("Booking Summary:");

        for (Map.Entry<String, Integer> entry : countByRoomType.entrySet()) {
            System.out.println(
                    entry.getKey() + ": " + entry.getValue() + " booking(s)");
        }
    }
}