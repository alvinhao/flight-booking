package hao.alvin.chase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class DataAccessTest {
    private static final String TEST_FILE = "test_seats.dat";
    private DataAccess dataAccess;

    @BeforeEach
    void setup() throws IOException {
        File file = new File(TEST_FILE);
        if (file.exists() && !file.delete()) {
            throw new IllegalStateException("Failed to delete test file: " + TEST_FILE);
        }
        dataAccess = new DataAccess(TEST_FILE);
    }

    @Test
    void testInitializeFile() throws IOException {
        dataAccess.initializeFile(10);
        File file = new File(TEST_FILE);
        assertTrue(file.exists(), "File should be created");
        assertEquals(10, file.length(), "File should be initialized with the correct size");
    }

    @Test
    void testReadEmptyRow() throws IOException {
        dataAccess.initializeFile(10);
        assertEquals(0, dataAccess.readRow(5), "An uninitialized row should return 0");
    }

    @Test
    void testWriteAndReadRow() throws IOException {
        dataAccess.initializeFile(10);
        dataAccess.writeRow(3, (byte) 0b10101010);
        assertEquals((byte) 0b10101010, dataAccess.readRow(3), "Written and read values should match");
    }

    @Test
    void testModifyExistingRow() throws IOException {
        dataAccess.initializeFile(10);
        dataAccess.writeRow(3, (byte) 0b10101010);
        dataAccess.writeRow(3, (byte) 0b11001100);
        assertEquals((byte) 0b11001100, dataAccess.readRow(3), "Modified data should be retained correctly");
    }

    @Test
    void testHandleNonexistentFile() {
        DataAccess invalidDataAccess = new DataAccess("nonexistent_file.dat");
        assertThrows(IOException.class, () -> invalidDataAccess.readRow(0), "Reading from a non-existent file should throw an exception");
    }

    @Test
    void testFilePersistence() throws IOException {
        dataAccess.initializeFile(10);
        dataAccess.writeRow(2, (byte) 0b11110000);

        // Simulate restarting the program
        DataAccess newDataAccess = new DataAccess(TEST_FILE);
        assertEquals((byte) 0b11110000, newDataAccess.readRow(2), "Data should persist after reloading the file");
    }
}
