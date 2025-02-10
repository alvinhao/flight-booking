package hao.alvin.chase;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

class SeatManager {
    private static final int ROWS = 20;
    private static final int SEATS_PER_ROW = 8;
    private static final String DEFAULT_DATA_FILE_NAME = "seats.dat";
    private static SeatManager instance;

    private final DataAccess dataAccess;
    private final Map<Character, ReentrantLock> rowLocks = new ConcurrentHashMap<>();

    private SeatManager(String fileName) {
        this.dataAccess = new DataAccess(fileName);
        initializeFile(fileName);
    }

    private void initializeFile(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            try {
                dataAccess.initializeFile(ROWS);
            } catch (IOException e) {
                throw new IllegalStateException("Failed to initialize seat data file");
            }
        }
    }

    public static SeatManager getInstance() {
        if (instance == null) {
            instance = new SeatManager(DEFAULT_DATA_FILE_NAME);
        }
        return instance;
    }

    public static SeatManager getTestInstance(String testFileName) {
        instance = new SeatManager(testFileName);
        return instance;
    }

    public boolean bookSeats(String startSeat, int numSeats) {
        return modifySeats(startSeat, numSeats, true);
    }

    public boolean cancelSeats(String startSeat, int numSeats) {
        return modifySeats(startSeat, numSeats, false);
    }

    private boolean modifySeats(String startSeat, int numSeats, boolean isBooking) {
        if (startSeat.isEmpty() || numSeats <= 0) {
            return false;
        }
        char row = startSeat.charAt(0);

        if (row - 'A' >= 20) {
            return false;
        }

        int startSeatNum;

        try {
            startSeatNum = Integer.parseInt(startSeat.substring(1));
        } catch (NumberFormatException numberFormatException) {
            return false;
        }

        if (startSeatNum + numSeats > SEATS_PER_ROW) return false; // Ensure booking does not exceed row limit

        ReentrantLock rowLock = rowLocks.computeIfAbsent(row, r -> new ReentrantLock());
        rowLock.lock();

        try {
            return modifyRow(row, startSeatNum, numSeats, isBooking);
        } catch (IOException e) {
            System.out.print("Caught an IO exception trying to modify seats data file. \n");
            return false;
        } finally {
            rowLock.unlock();
        }
    }

    /*
        Each row is stored as a single byte (8 bits), where each bit represents a seat
        Bit position:  7  6  5  4  3  2  1  0
        Seats:         S8 S7 S6 S5 S4 S3 S2 S1
        A bit value of 0 means the seat is available. A bit value of 1 means the seat is booked.
     */
    private boolean modifyRow(char row, int startSeatNum, int numSeats, boolean isBooking) throws IOException {
        byte rowData = dataAccess.readRow(row - 'A');

        /*
               This shifts 1 left by numSeats bits, creating a power of 2.
               Then converts the power of 2 into a mask covering the numSeats bits.
               Shift Left by startSeatNum, to move the mask to the correct position in the byte.

               If startSeatNum = 5, numSeats = 3
               (1 << 3) - 1  →  00000111
               (00000111) << 5  →  11100000
        */
        int bitMask = ((1 << numSeats) - 1) << startSeatNum;

        if ((isBooking && (rowData & bitMask) != 0) || (!isBooking && (rowData & bitMask) == 0)) {
            return false;
        }

        if (isBooking) {
            rowData = (byte) (rowData | bitMask); // Set bits at once
        } else {
            rowData = (byte) (rowData & ~bitMask); // Clear bits at once
        }

        dataAccess.writeRow(row - 'A', rowData);

        return true;
    }
}
