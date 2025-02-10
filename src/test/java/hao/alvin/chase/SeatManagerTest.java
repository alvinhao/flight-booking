package hao.alvin.chase;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.util.concurrent.*;

class SeatManagerTest {
    private static final String TEST_DATA_FILE = "test_seats.dat";
    private SeatManager seatManager;

    @BeforeEach
    void setup() {
        File file = new File(TEST_DATA_FILE);
        if (file.exists() && !file.delete()) {
            throw new IllegalStateException("Failed to delete test file: " + TEST_DATA_FILE);
        }
        seatManager = SeatManager.getTestInstance(TEST_DATA_FILE);
    }

    @AfterEach
    void cleanup() {
        File file = new File(TEST_DATA_FILE);
        if (file.exists() && !file.delete()) {
            System.err.println("Warning: Failed to delete test file: " + TEST_DATA_FILE);
        }
    }

    @Test
    void testBasicBooking() {
        assertTrue(seatManager.bookSeats("A1", 2));
    }

    @Test
    void testBasicCancellation() {
        seatManager.bookSeats("A1", 2);
        assertTrue(seatManager.cancelSeats("A1", 2));
    }

    @Test
    void testBookingTakenSeatsFails() {
        seatManager.bookSeats("A1", 2);
        assertFalse(seatManager.bookSeats("A1", 2));
    }

    @Test
    void testCancellingUnbookedSeatsFails() {
        assertFalse(seatManager.cancelSeats("A3", 2));
    }

    @Test
    void testBookingFirstSeatInRow() {
        assertTrue(seatManager.bookSeats("A0", 1));
    }

    @Test
    void testBookingLastSeatInRow() {
        assertTrue(seatManager.bookSeats("A7", 1));
    }

    @Test
    void testBookingBeyondRowLimitFails() {
        assertFalse(seatManager.bookSeats("A6", 3));
    }

    @Test
    void testInvalidSeatNumberFails() {
        assertFalse(seatManager.bookSeats("A9", 2));
    }

    @Test
    void testRowBoundary() {
        assertTrue(seatManager.bookSeats("T1", 2));
    }

    @Test
    void testInvalidRowFails() {
        assertFalse(seatManager.bookSeats("U1", 2));
    }

    @Test
    void testBookingZeroSeatsFails() {
        assertFalse(seatManager.bookSeats("A1", 0));
    }

    @Test
    void testBookingNegativeSeatsFails() {
        assertFalse(seatManager.bookSeats("A1", -2));
    }

    @Test
    void testBookingNonNumericSeatFails() {
        assertFalse(seatManager.bookSeats("Axx", 2));
    }

    @Test
    void testEmptyInputFails() {
        assertFalse(seatManager.bookSeats("", 2));
    }

    @Test
    void testMissingParametersFails() {
        assertFalse(seatManager.bookSeats("BOOK A1", 2));
    }

    @Test
    void testTooManyParametersFails() {
        assertFalse(seatManager.bookSeats("BOOK A1 2 EXTRA", 2));
    }

    @Test
    void testCancelAndRebook() {
        seatManager.bookSeats("B2", 3);
        assertTrue(seatManager.cancelSeats("B2", 3));
        assertTrue(seatManager.bookSeats("B2", 3));
    }

    @Test
    void testConcurrentBooking() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(2);

        Callable<Boolean> task = () -> seatManager.bookSeats("C1", 1);

        Future<Boolean> result1 = executor.submit(task);
        Future<Boolean> result2 = executor.submit(task);

        executor.shutdown();
        if (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
            executor.shutdownNow(); // Force shutdown if not completed
            throw new IllegalStateException("Executor did not terminate properly");
        }

        // Ensure only one thread was able to book
        assertTrue(result1.get() ^ result2.get()); // XOR ensures only one is true
    }

    @Test
    void testConcurrentCancellation() throws Exception {
        seatManager.bookSeats("D1", 1);

        ExecutorService executor = Executors.newFixedThreadPool(2);

        Callable<Boolean> task = () -> seatManager.cancelSeats("D1", 1);

        Future<Boolean> result1 = executor.submit(task);
        Future<Boolean> result2 = executor.submit(task);

        executor.shutdown();
        if (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
            executor.shutdownNow(); // Force shutdown if not completed
            throw new IllegalStateException("Executor did not terminate properly");
        }

        // Ensure only one thread was able to cancel
        assertTrue(result1.get() ^ result2.get()); // XOR ensures only one is true
    }
}
