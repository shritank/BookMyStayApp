import java.util.*;

class Reservation {
    private String guestName;
    private String roomType;
    private String reservationId;

    public Reservation(String guestName, String roomType, String reservationId) {
        this.guestName = guestName;
        this.roomType = roomType;
        this.reservationId = reservationId;
    }

    public String getGuestName() {
        return guestName;
    }

    public String getRoomType() {
        return roomType;
    }

    public String getReservationId() {
        return reservationId;
    }
}

class RoomInventory {
    private Map<String, Integer> inventory;

    public RoomInventory() {
        inventory = new HashMap<>();
        inventory.put("Single", 5);
        inventory.put("Double", 3);
        inventory.put("Suite", 2);
    }

    public synchronized boolean allocateRoom(String roomType) {
        if (inventory.get(roomType) > 0) {
            inventory.put(roomType, inventory.get(roomType) - 1);
            return true;
        }
        return false;
    }

    public int getAvailability(String roomType) {
        return inventory.get(roomType);
    }
}

class BookingRequestQueue {
    private Queue<Reservation> queue = new LinkedList<>();

    public synchronized void addRequest(Reservation reservation) {
        queue.add(reservation);
    }

    public synchronized Reservation getNextRequest() {
        return queue.poll();
    }
}

class RoomAllocationService {
    public synchronized void allocateRoom(Reservation reservation, RoomInventory inventory) {
        if (inventory.allocateRoom(reservation.getRoomType())) {
            System.out.println("Booking confirmed for Guest: " + reservation.getGuestName() +
                    ", Room ID: " + reservation.getReservationId());
        } else {
            System.out.println("Booking failed for Guest: " + reservation.getGuestName() +
                    " (" + reservation.getRoomType() + ")");
        }
    }
}

class ConcurrentBookingProcessor implements Runnable {
    private BookingRequestQueue bookingQueue;
    private RoomInventory inventory;
    private RoomAllocationService allocationService;

    public ConcurrentBookingProcessor(BookingRequestQueue bookingQueue,
                                      RoomInventory inventory,
                                      RoomAllocationService allocationService) {
        this.bookingQueue = bookingQueue;
        this.inventory = inventory;
        this.allocationService = allocationService;
    }

    @Override
    public void run() {
        while (true) {
            Reservation reservation;
            synchronized (bookingQueue) {
                reservation = bookingQueue.getNextRequest();
            }
            if (reservation == null) break;
            synchronized (inventory) {
                allocationService.allocateRoom(reservation, inventory);
            }
        }
    }
}

public class BookMyStayApp {
    public static void main(String[] args) {
        System.out.println("Concurrent Booking Simulation");

        RoomInventory inventory = new RoomInventory();
        BookingRequestQueue bookingQueue = new BookingRequestQueue();
        RoomAllocationService allocationService = new RoomAllocationService();

        bookingQueue.addRequest(new Reservation("Abhi", "Single", "Single-1"));
        bookingQueue.addRequest(new Reservation("Vanmathi", "Double", "Double-1"));
        bookingQueue.addRequest(new Reservation("Kural", "Suite", "Suite-1"));
        bookingQueue.addRequest(new Reservation("Subha", "Single", "Single-2"));

        Thread t1 = new Thread(new ConcurrentBookingProcessor(bookingQueue, inventory, allocationService));
        Thread t2 = new Thread(new ConcurrentBookingProcessor(bookingQueue, inventory, allocationService));

        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            System.out.println("Thread execution interrupted.");
        }

        System.out.println("\nRemaining Inventory:");
        System.out.println("Single: " + inventory.getAvailability("Single"));
        System.out.println("Double: " + inventory.getAvailability("Double"));
        System.out.println("Suite: " + inventory.getAvailability("Suite"));
    }
}
