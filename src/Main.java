import java.util.*;

class RoomInventory {
    private Map<String, Integer> inventory;

    public RoomInventory() {
        inventory = new HashMap<>();
        inventory.put("Single", 5);
        inventory.put("Double", 5);
        inventory.put("Suite", 5);
    }

    public void allocateRoom(String roomType) {
        inventory.put(roomType, inventory.get(roomType) - 1);
    }

    public void restoreRoom(String roomType) {
        inventory.put(roomType, inventory.get(roomType) + 1);
    }

    public int getAvailability(String roomType) {
        return inventory.get(roomType);
    }
}

class CancellationService {
    private Map<String, String> reservationRoomTypeMap;
    private Stack<String> rollbackStack;

    public CancellationService() {
        reservationRoomTypeMap = new HashMap<>();
        rollbackStack = new Stack<>();
    }

    public void registerBooking(String reservationId, String roomType) {
        reservationRoomTypeMap.put(reservationId, roomType);
    }

    public void cancelBooking(String reservationId, RoomInventory inventory) {
        if (!reservationRoomTypeMap.containsKey(reservationId)) {
            System.out.println("Cancellation failed: Reservation not found.");
            return;
        }
        String roomType = reservationRoomTypeMap.get(reservationId);
        inventory.restoreRoom(roomType);
        rollbackStack.push(reservationId);
        reservationRoomTypeMap.remove(reservationId);

        System.out.println("Booking cancelled successfully. Inventory restored for room type: " + roomType);
        System.out.println("\nRollback History (Most Recent First):");
        System.out.println("Released Reservation ID: " + rollbackStack.peek());
        System.out.println("\nUpdated " + roomType + " Room Availability: " + inventory.getAvailability(roomType));
    }
}

public class BookMyStayApp {
    public static void main(String[] args) {
        System.out.println("Booking Cancellation");

        RoomInventory inventory = new RoomInventory();
        CancellationService cancellationService = new CancellationService();

        String reservationId = "Single-1";
        String roomType = "Single";

        inventory.allocateRoom(roomType);
        cancellationService.registerBooking(reservationId, roomType);

        cancellationService.cancelBooking(reservationId, inventory);
    }
}
