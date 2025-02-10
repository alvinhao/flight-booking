package hao.alvin.chase;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

public class BookingSystemTest {
    private static final String TEST_DATA_FILE = "seats.dat";

    @BeforeEach
    void setup() {
        File file = new File(TEST_DATA_FILE);

        if (file.exists() && !file.delete()) {
            throw new IllegalStateException("Failed to delete test file: " + TEST_DATA_FILE);
        }

        try {
            DataAccess dataAccess = new DataAccess(TEST_DATA_FILE);
            dataAccess.initializeFile(20); // Assuming 20 rows
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize test file", e);
        }
    }

    @AfterEach
    void cleanup() {
        File file = new File(TEST_DATA_FILE);
        if (file.exists() && !file.delete()) {
            System.err.println("Warning: Failed to delete test file: " + TEST_DATA_FILE);
        }
    }

    private String runCLICommand(String... args) throws IOException {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;

        try {
            System.setOut(new PrintStream(outContent));
            BookingSystem.main(args);
        } finally {
            System.setOut(originalOut);
        }

        return outContent.toString().trim();
    }

    @Test
    void testValidBooking() throws IOException {
        assertEquals("SUCCESS", runCLICommand("BOOK", "A1", "2"));
    }

    @Test
    void testValidCancellation() throws IOException {
        // ✅ Book the seat first
        assertEquals("SUCCESS", runCLICommand("BOOK", "A1", "3"));

        // ✅ Then cancel it
        assertEquals("SUCCESS", runCLICommand("CANCEL", "A1", "3"));
    }

    @Test
    void testCancelWithoutBookingFails() throws IOException {
        assertEquals("FAIL", runCLICommand("CANCEL", "A1", "2"));
    }

    @Test
    void testBookingAlreadyBookedSeatsFails() throws IOException {
        assertEquals("SUCCESS", runCLICommand("BOOK", "A1", "2"));
        assertEquals("FAIL", runCLICommand("BOOK", "A1", "2"));
    }

    @Test
    void testInvalidSeatNumberFails() throws IOException {
        assertEquals("FAIL", runCLICommand("BOOK", "A9", "2")); // A9 does not exist
    }

    @Test
    void testInvalidRowFails() throws IOException {
        assertEquals("FAIL", runCLICommand("BOOK", "Z1", "2")); // Row Z does not exist
    }

    @Test
    void testBookingBeyondRowLimitFails() throws IOException {
        assertEquals("FAIL", runCLICommand("BOOK", "A6", "3")); // Row limit exceeded
    }

    @Test
    void testBookingZeroSeatsFails() throws IOException {
        assertEquals("FAIL", runCLICommand("BOOK", "A1", "0")); // 0 is not a valid seat count
    }

    @Test
    void testBookingNegativeSeatsFails() throws IOException {
        assertEquals("FAIL", runCLICommand("BOOK", "A1", "-2")); // Negative seats not allowed
    }

    @Test
    void testNonNumericSeatFails() throws IOException {
        assertEquals("FAIL", runCLICommand("BOOK", "Axx", "2")); // "Axx" is invalid
    }

    @Test
    void testEmptyInputFails() throws IOException {
        assertEquals("FAIL", runCLICommand(""));
    }

    @Test
    void testTooManyArgumentsFails() throws IOException {
        assertEquals("FAIL", runCLICommand("BOOK", "A1", "2", "EXTRA")); // Too many args
    }
}
