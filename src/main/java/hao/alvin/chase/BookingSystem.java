package hao.alvin.chase;

public class BookingSystem {
    public static void main(String[]args) {
        if (args.length != 3) {
            System.out.println("FAIL");
            return;
        }

        String action = args[0];
        String seat = args[1];
        int numSeats;

        try {
            numSeats = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            System.out.println("FAIL");
            return;
        }

        SeatManager manager = SeatManager.getInstance();
        boolean result = switch (action.toUpperCase()) {
            case "BOOK" -> manager.bookSeats(seat, numSeats);
            case "CANCEL" -> manager.cancelSeats(seat, numSeats);
            default -> false;
        };

        System.out.println(result ? "SUCCESS" : "FAIL");
    }
}
